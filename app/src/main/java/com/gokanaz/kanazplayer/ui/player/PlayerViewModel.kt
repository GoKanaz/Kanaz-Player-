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
    
    private val _isShuffleEnabled = MutableStateFlow(false)
    val isShuffleEnabled: StateFlow<Boolean> = _isShuffleEnabled
    
    private val _isRepeatEnabled = MutableStateFlow(false)
    val isRepeatEnabled: StateFlow<Boolean> = _isRepeatEnabled
    
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
    
    // 🔧 PERBAIKAN BUG #1: Simplifikasi logika togglePlayPause
    fun togglePlayPause() {
        _currentSong.value?.let { song ->
            // Jika tidak sedang playing dan belum ada lagu yang dimainkan, play lagu saat ini
            if (!isPlaying.value && playerService.getCurrentPosition() == 0L && playerService.getDuration() == 0L) {
                playerService.playSong(song)
            } else {
                // Toggle pause/play
                playerService.togglePlayPause()
            }
        }
    }
    
    fun seekTo(position: Long) {
        playerService.seekTo(position)
        _currentPosition.value = position
    }
    
    // 🔧 PERBAIKAN BUG #3 & #4: Perbaiki logika playNext
    fun playNext() {
        if (_songs.value.isEmpty()) return
        
        val currentIndex = _songs.value.indexOf(_currentSong.value)
        
        // Jika repeat enabled, ulangi lagu saat ini
        if (_isRepeatEnabled.value) {
            _currentSong.value?.let { playSong(it) }
            return
        }
        
        val nextIndex = if (_isShuffleEnabled.value) {
            // Random next (pastikan tidak sama dengan current)
            val availableIndices = _songs.value.indices.filter { it != currentIndex }
            if (availableIndices.isNotEmpty()) {
                availableIndices.random()
            } else {
                currentIndex
            }
        } else {
            // Sequential next (loop ke awal jika sudah di akhir)
            if (currentIndex >= 0 && currentIndex < _songs.value.size - 1) {
                currentIndex + 1
            } else {
                0
            }
        }
        
        // Play lagu berikutnya
        if (nextIndex >= 0 && nextIndex < _songs.value.size) {
            playSong(_songs.value[nextIndex])
        }
    }
    
    fun playPrevious() {
        if (_songs.value.isEmpty()) return
        
        val currentIndex = _songs.value.indexOf(_currentSong.value)
        
        val prevIndex = if (_isShuffleEnabled.value) {
            // Random previous (pastikan tidak sama dengan current)
            val availableIndices = _songs.value.indices.filter { it != currentIndex }
            if (availableIndices.isNotEmpty()) {
                availableIndices.random()
            } else {
                currentIndex
            }
        } else {
            // Sequential previous (loop ke akhir jika sudah di awal)
            if (currentIndex > 0) {
                currentIndex - 1
            } else {
                _songs.value.size - 1
            }
        }
        
        if (prevIndex >= 0 && prevIndex < _songs.value.size) {
            playSong(_songs.value[prevIndex])
        }
    }
    
    fun toggleShuffle() {
        _isShuffleEnabled.value = !_isShuffleEnabled.value
    }
    
    fun toggleRepeat() {
        _isRepeatEnabled.value = !_isRepeatEnabled.value
    }
    
    override fun onCleared() {
        super.onCleared()
        playerService.release()
    }
}
