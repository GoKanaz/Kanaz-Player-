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
    
    private fun initializePlayer(context: Context): ExoPlayer {
        if (exoPlayer == null) {
            Log.d(TAG, "Initializing ExoPlayer")
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
                            when (playbackState) {
                                Player.STATE_IDLE -> {
                                    Log.d(TAG, "State: IDLE")
                                }
                                Player.STATE_BUFFERING -> {
                                    Log.d(TAG, "State: BUFFERING")
                                }
                                Player.STATE_READY -> {
                                    Log.d(TAG, "State: READY - Duration: ${duration}ms")
                                    _duration.value = duration
                                }
                                Player.STATE_ENDED -> {
                                    Log.d(TAG, "State: ENDED")
                                    _isPlaying.value = false
                                }
                            }
                        }
                        
                        override fun onIsPlayingChanged(playing: Boolean) {
                            _isPlaying.value = playing
                            Log.d(TAG, "Is Playing: $playing")
                        }
                        
                        override fun onPlayerError(error: PlaybackException) {
                            Log.e(TAG, "Player Error: ${error.errorCodeName}")
                            Log.e(TAG, "Error Message: ${error.message}")
                            Log.e(TAG, "Cause: ${error.cause?.message}")
                            _isPlaying.value = false
                        }
                    })
                }
            
            Log.d(TAG, "ExoPlayer initialized successfully")
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
            Log.d(TAG, "Audio Focus Request: $result")
            result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } else {
            @Suppress("DEPRECATION")
            val result = audioManager?.requestAudioFocus(
                audioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
            Log.d(TAG, "Audio Focus Request (legacy): $result")
            result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
    }
    
    fun playSong(context: Context, song: Song) {
        Log.d(TAG, "========================================")
        Log.d(TAG, "PLAY SONG REQUESTED")
        Log.d(TAG, "Title: ${song.title}")
        Log.d(TAG, "Artist: ${song.artist}")
        Log.d(TAG, "Path: ${song.path}")
        Log.d(TAG, "Duration from Song: ${song.duration}ms")
        Log.d(TAG, "========================================")
        
        if (!requestAudioFocus(context)) {
            Log.e(TAG, "Failed to gain audio focus")
            return
        }
        
        try {
            val player = initializePlayer(context)
            
            val uri = Uri.parse(song.path)
            Log.d(TAG, "Parsed URI: $uri")
            
            val mediaMetadata = MediaMetadata.Builder()
                .setTitle(song.title)
                .setArtist(song.artist)
                .setAlbumTitle(song.album)
                .build()
            
            val mediaItem = MediaItem.Builder()
                .setUri(uri)
                .setMediaMetadata(mediaMetadata)
                .build()
            
            player.stop()
            player.clearMediaItems()
            
            Log.d(TAG, "Setting media item...")
            player.setMediaItem(mediaItem)
            
            Log.d(TAG, "Preparing player...")
            player.prepare()
            
            Log.d(TAG, "Starting playback...")
            player.play()
            
            _currentSong.value = song
            _duration.value = song.duration
            _currentPosition.value = 0L
            
            Log.d(TAG, "Playback initiated successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Exception in playSong", e)
            e.printStackTrace()
        }
    }
    
    fun togglePlayPause(context: Context) {
        val player = exoPlayer
        if (player == null) {
            Log.w(TAG, "togglePlayPause: Player is null")
            return
        }
        
        Log.d(TAG, "Toggle Play/Pause - Current state: ${player.isPlaying}")
        
        if (player.isPlaying) {
            player.pause()
            _isPlaying.value = false
            Log.d(TAG, "Paused")
        } else {
            if (requestAudioFocus(context)) {
                player.play()
                _isPlaying.value = true
                Log.d(TAG, "Playing")
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
