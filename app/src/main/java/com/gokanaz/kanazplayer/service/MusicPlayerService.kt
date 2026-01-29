package com.gokanaz.kanazplayer.service

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.gokanaz.kanazplayer.data.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MusicPlayerService(context: Context) {
    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying
    
    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition
    
    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration
    
    private val _currentSongState = MutableStateFlow<Song?>(null)
    val currentSongState: StateFlow<Song?> = _currentSongState
    
    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                _isPlaying.value = exoPlayer.isPlaying
            }
            
            override fun onIsPlayingChanged(playing: Boolean) {
                _isPlaying.value = playing
            }
        })
    }
    
    fun playSong(song: Song) {
        try {
            val mediaItem = MediaItem.fromUri(song.path)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
            _currentSongState.value = song
            _isPlaying.value = true
            _duration.value = song.duration
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun togglePlayPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
        _isPlaying.value = exoPlayer.isPlaying
    }
    
    fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
        _currentPosition.value = position
    }
    
    fun getCurrentPosition(): Long {
        return exoPlayer.currentPosition
    }
    
    fun getDuration(): Long {
        return exoPlayer.duration.takeIf { it > 0 } ?: 0
    }
    
    fun release() {
        exoPlayer.release()
        _isPlaying.value = false
    }
}
