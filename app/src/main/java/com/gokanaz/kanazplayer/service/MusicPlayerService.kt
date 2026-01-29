package com.gokanaz.kanazplayer.service

import android.media.MediaPlayer
import com.gokanaz.kanazplayer.data.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MusicPlayerService {
    private var mediaPlayer: MediaPlayer? = null
    private var currentSong: Song? = null
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying
    
    private val _currentPosition = MutableStateFlow(0)
    val currentPosition: StateFlow<Int> = _currentPosition
    
    private val _currentSongState = MutableStateFlow<Song?>(null)
    val currentSongState: StateFlow<Song?> = _currentSongState
    
    fun playSong(song: Song) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(song.path)
                prepare()
                start()
            }
            currentSong = song
            _currentSongState.value = song
            _isPlaying.value = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun togglePlayPause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _isPlaying.value = false
            } else {
                it.start()
                _isPlaying.value = true
            }
        }
    }
    
    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
        _currentPosition.value = position
    }
    
    fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }
    
    fun getDuration(): Int {
        return mediaPlayer?.duration ?: 0
    }
    
    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        _isPlaying.value = false
    }
}
