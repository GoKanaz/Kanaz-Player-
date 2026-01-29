package com.gokanaz.kanazplayer.service

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.gokanaz.kanazplayer.data.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object MusicPlayerManager {
    private var exoPlayer: ExoPlayer? = null
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying
    
    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition
    
    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration
    
    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong

    private var isTransitioning = false
    
    fun getPlayer(context: Context): ExoPlayer {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context.applicationContext).build().apply {
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(playing: Boolean) {
                        if (!isTransitioning) {
                            _isPlaying.value = playing
                        }
                    }
                    
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_READY) {
                            _duration.value = duration
                            isTransitioning = false
                            _isPlaying.value = isPlaying
                        } else if (playbackState == Player.STATE_ENDED) {
                            _isPlaying.value = false
                        }
                    }
                })
            }
        }
        return exoPlayer!!
    }
    
    fun playSong(context: Context, song: Song) {
        val player = getPlayer(context)
        try {
            isTransitioning = true
            _isPlaying.value = true
            _currentPosition.value = 0L
            
            val mediaItem = MediaItem.fromUri(song.path)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
            
            _currentSong.value = song
            _duration.value = song.duration
        } catch (e: Exception) {
            isTransitioning = false
            _isPlaying.value = false
            e.printStackTrace()
        }
    }
    
    fun togglePlayPause(context: Context) {
        val player = getPlayer(context)
        if (player.isPlaying) {
            player.pause()
            _isPlaying.value = false
        } else {
            player.play()
            _isPlaying.value = true
        }
    }
    
    fun seekTo(context: Context, position: Long) {
        val player = getPlayer(context)
        player.seekTo(position)
        _currentPosition.value = position
    }
    
    fun getCurrentPosition(context: Context): Long {
        return getPlayer(context).currentPosition
    }
    
    fun getDuration(context: Context): Long {
        val d = getPlayer(context).duration
        return if (d > 0) d else 0L
    }
    
    fun release() {
        exoPlayer?.release()
        exoPlayer = null
        _isPlaying.value = false
    }
}
// Update: Thu Jan 29 12:56:04 WIB 2026
