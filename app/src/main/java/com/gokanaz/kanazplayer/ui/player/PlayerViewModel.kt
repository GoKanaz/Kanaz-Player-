package com.gokanaz.kanazplayer.ui.player

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gokanaz.kanazplayer.data.model.Song
import com.gokanaz.kanazplayer.data.repository.MusicRepository
import com.gokanaz.kanazplayer.service.MusicPlayerService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MusicRepository(application)
    private val playerService = MusicPlayerService(application)
    
    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs
    
    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong
    
    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition
    
    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration
    
    val isPlaying = playerService.isPlaying
    
    init {
        startPositionUpdater()
    }
    
    private fun startPositionUpdater() {
        viewModelScope.launch {
            while (true) {
                if (isPlaying.value) {
                    _currentPosition.value = playerService.getCurrentPosition()
                    _duration.value = playerService.getDuration()
                }
                delay(100)
            }
        }
    }
    
    fun loadSongs() {
        viewModelScope.launch {
            _songs.value = repository.getAllSongs()
            if (_songs.value.isNotEmpty() && _currentSong.value == null) {
                _currentSong.value = _songs.value.first()
            }
        }
    }
    
    fun playSong(song: Song) {
        _currentSong.value = song
        playerService.playSong(song)
    }
    
    fun togglePlayPause() {
        _currentSong.value?.let { song ->
            if (!isPlaying.value && playerService.getCurrentPosition() == 0L) {
                playerService.playSong(song)
            } else {
                playerService.togglePlayPause()
            }
        }
    }
    
    fun seekTo(position: Long) {
        playerService.seekTo(position)
        _currentPosition.value = position
    }
    
    fun playNext() {
        val currentIndex = _songs.value.indexOf(_currentSong.value)
        if (currentIndex < _songs.value.size - 1) {
            playSong(_songs.value[currentIndex + 1])
        }
    }
    
    fun playPrevious() {
        val currentIndex = _songs.value.indexOf(_currentSong.value)
        if (currentIndex > 0) {
            playSong(_songs.value[currentIndex - 1])
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        playerService.release()
    }
}
