package com.gokanaz.kanazplayer.service

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.util.Log
import androidx.media3.common.AudioAttributes as Media3AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
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
    
    fun getPlayer(context: Context): ExoPlayer {
        if (exoPlayer == null) {
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
                                Player.STATE_READY -> {
                                    _duration.value = duration
                                    Log.d(TAG, "Player ready, duration: ${duration}ms")
                                }
                                Player.STATE_ENDED -> {
                                    Log.d(TAG, "Playback ended")
                                }
                                Player.STATE_BUFFERING -> {
                                    Log.d(TAG, "Buffering...")
                                }
                                Player.STATE_IDLE -> {
                                    Log.d(TAG, "Player idle")
                                }
                            }
                        }
                        
                        override fun onIsPlayingChanged(playing: Boolean) {
                            _isPlaying.value = playing
                            Log.d(TAG, "Playing changed: $playing")
                        }
                        
                        override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                            Log.e(TAG, "Player error: ${error.message}", error)
                        }
                    })
                }
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
            Log.d(TAG, "Audio focus request result: $result")
            result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } else {
            @Suppress("DEPRECATION")
            val result = audioManager?.requestAudioFocus(
                audioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
            Log.d(TAG, "Audio focus request result (legacy): $result")
            result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
    }
    
    fun playSong(context: Context, song: Song) {
        Log.d(TAG, "playSong called: ${song.title}, path: ${song.path}")
        
        val player = getPlayer(context)
        
        if (!requestAudioFocus(context)) {
            Log.e(TAG, "Failed to gain audio focus")
            return
        }
        
        try {
            val mediaMetadata = MediaMetadata.Builder()
                .setTitle(song.title)
                .setArtist(song.artist)
                .setAlbumTitle(song.album)
                .build()
            
            val mediaItem = MediaItem.Builder()
                .setUri(song.path)
                .setMediaMetadata(mediaMetadata)
                .build()
            
            player.stop()
            player.clearMediaItems()
            player.setMediaItem(mediaItem)
            player.prepare()
            player.playWhenReady = true
            
            _currentSong.value = song
            _duration.value = song.duration
            
            Log.d(TAG, "Song prepared and playing: ${song.title}")
        } catch (e: Exception) {
            Log.e(TAG, "Error playing song", e)
            e.printStackTrace()
        }
    }
    
    fun togglePlayPause(context: Context) {
        val player = getPlayer(context)
        Log.d(TAG, "togglePlayPause called, current isPlaying: ${player.isPlaying}")
        
        if (player.isPlaying) {
            player.pause()
            _isPlaying.value = false
        } else {
            if (requestAudioFocus(context)) {
                player.play()
                _isPlaying.value = true
            }
        }
    }
    
    fun seekTo(context: Context, position: Long) {
        val player = getPlayer(context)
        player.seekTo(position)
        _currentPosition.value = position
        Log.d(TAG, "Seeked to position: $position")
    }
    
    fun getCurrentPosition(context: Context): Long {
        val pos = getPlayer(context).currentPosition
        _currentPosition.value = pos
        return pos
    }
    
    fun getDuration(context: Context): Long {
        val dur = getPlayer(context).duration
        return if (dur > 0) dur else _duration.value
    }
    
    fun release() {
        Log.d(TAG, "Releasing player")
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
    }
}
