package com.gokanaz.kanazplayer.service

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.media3.common.AudioAttributes as Media3AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.gokanaz.kanazplayer.data.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object MusicPlayerManager {
    private const val TAG = "MusicPlayerManager"
    private var exoPlayer: ExoPlayer? = null
    private var audioManager: AudioManager? = null
    private var audioFocusRequest: AudioFocusRequest? = null
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying
    
    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition
    
    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration
    
    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong
    
    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        Log.d(TAG, "Audio focus changed: $focusChange")
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                exoPlayer?.volume = 1.0f
                if (_isPlaying.value) {
                    exoPlayer?.play()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                exoPlayer?.pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                exoPlayer?.volume = 0.3f
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                exoPlayer?.pause()
                _isPlaying.value = false
            }
        }
    }
    
    fun getPlayer(context: Context): ExoPlayer {
        if (exoPlayer == null) {
            Log.d(TAG, "Creating new ExoPlayer instance")
            audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            
            val audioAttributes = Media3AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .build()
            
            exoPlayer = ExoPlayer.Builder(context.applicationContext)
                .setAudioAttributes(audioAttributes, true)
                .setHandleAudioBecomingNoisy(true)
                .setWakeMode(C.WAKE_MODE_LOCAL)
                .build().apply {
                    addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            val stateName = when (playbackState) {
                                Player.STATE_IDLE -> "IDLE"
                                Player.STATE_BUFFERING -> "BUFFERING"
                                Player.STATE_READY -> "READY"
                                Player.STATE_ENDED -> "ENDED"
                                else -> "UNKNOWN"
                            }
                            Log.d(TAG, "Playback state: $stateName")
                            
                            when (playbackState) {
                                Player.STATE_READY -> {
                                    _duration.value = duration
                                    Log.d(TAG, "Duration set: ${duration}ms")
                                }
                                Player.STATE_ENDED -> {
                                    _isPlaying.value = false
                                }
                            }
                        }
                        
                        override fun onIsPlayingChanged(playing: Boolean) {
                            _isPlaying.value = playing
                            Log.d(TAG, "Is playing changed: $playing")
                        }
                        
                        override fun onPlayerError(error: PlaybackException) {
                            Log.e(TAG, "Player error: ${error.errorCodeName}")
                            Log.e(TAG, "Error message: ${error.message}")
                            error.cause?.let {
                                Log.e(TAG, "Cause: ${it.message}")
                            }
                            _isPlaying.value = false
                        }
                    })
                }
            
            Log.d(TAG, "ExoPlayer created successfully")
        }
        return exoPlayer!!
    }
    
    private fun requestAudioFocus(context: Context): Boolean {
        audioManager = audioManager ?: context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
            
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes)
                .setOnAudioFocusChangeListener(audioFocusChangeListener)
                .build()
            
            val result = audioManager?.requestAudioFocus(audioFocusRequest!!)
            val granted = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
            Log.d(TAG, "Audio focus request result: $granted")
            granted
        } else {
            @Suppress("DEPRECATION")
            val result = audioManager?.requestAudioFocus(
                audioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
            val granted = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
            Log.d(TAG, "Audio focus request result (legacy): $granted")
            granted
        }
    }
    
    fun playSong(context: Context, song: Song) {
        Log.d(TAG, "========================================")
        Log.d(TAG, "PLAY SONG REQUESTED")
        Log.d(TAG, "Title: ${song.title}")
        Log.d(TAG, "Path: ${song.path}")
        Log.d(TAG, "========================================")
        
        try {
            if (!requestAudioFocus(context)) {
                Log.e(TAG, "Failed to gain audio focus!")
                return
            }
            
            val player = getPlayer(context)
            Log.d(TAG, "Player instance obtained")
            
            val uri = Uri.parse(song.path)
            Log.d(TAG, "URI created: $uri")
            
            val mediaMetadata = MediaMetadata.Builder()
                .setTitle(song.title)
                .setArtist(song.artist)
                .setAlbumTitle(song.album)
                .build()
            
            val mediaItem = MediaItem.Builder()
                .setUri(uri)
                .setMediaMetadata(mediaMetadata)
                .build()
            
            Log.d(TAG, "Stopping previous playback")
            player.stop()
            player.clearMediaItems()
            
            Log.d(TAG, "Setting new media item")
            player.setMediaItem(mediaItem)
            
            Log.d(TAG, "Preparing player")
            player.prepare()
            
            Log.d(TAG, "Starting playback")
            player.play()
            
            _currentSong.value = song
            _duration.value = song.duration
            _currentPosition.value = 0L
            
            Log.d(TAG, "Playback initiated successfully")
            Log.d(TAG, "Player state: ${player.playbackState}")
            Log.d(TAG, "Player isPlaying: ${player.isPlaying}")
            
        } catch (e: Exception) {
            Log.e(TAG, "EXCEPTION in playSong!", e)
            e.printStackTrace()
        }
    }
    
    fun togglePlayPause(context: Context) {
        val player = exoPlayer
        if (player == null) {
            Log.e(TAG, "togglePlayPause: Player is NULL!")
            return
        }
        
        Log.d(TAG, "Toggle Play/Pause")
        Log.d(TAG, "Current isPlaying: ${player.isPlaying}")
        
        if (player.isPlaying) {
            Log.d(TAG, "Pausing")
            player.pause()
            _isPlaying.value = false
        } else {
            Log.d(TAG, "Playing")
            if (requestAudioFocus(context)) {
                player.play()
                _isPlaying.value = true
            } else {
                Log.e(TAG, "Failed to gain audio focus")
            }
        }
    }
    
    fun seekTo(context: Context, position: Long) {
        exoPlayer?.seekTo(position)
        _currentPosition.value = position
        Log.d(TAG, "Seeked to: ${position}ms")
    }
    
    fun getCurrentPosition(context: Context): Long {
        val player = exoPlayer ?: return 0L
        val pos = player.currentPosition
        _currentPosition.value = pos
        return pos
    }
    
    fun getDuration(context: Context): Long {
        val player = exoPlayer ?: return _duration.value
        val dur = player.duration
        return if (dur > 0) {
            _duration.value = dur
            dur
        } else {
            _duration.value
        }
    }
    
    fun release() {
        Log.d(TAG, "Releasing player")
        exoPlayer?.stop()
        exoPlayer?.release()
        exoPlayer = null
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { audioManager?.abandonAudioFocusRequest(it) }
        } else {
            @Suppress("DEPRECATION")
            audioManager?.abandonAudioFocus(audioFocusChangeListener)
        }
        
        _isPlaying.value = false
        _currentPosition.value = 0L
        _duration.value = 0L
        _currentSong.value = null
    }
}
