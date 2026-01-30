package com.gokanaz.kanazplayer.ui.player

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gokanaz.kanazplayer.data.model.Song
import com.gokanaz.kanazplayer.data.repository.MusicRepository
import com.gokanaz.kanazplayer.service.MusicPlayerManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "PlayerViewModel"
    private val repository = MusicRepository(application)
    
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
    
    val isPlaying = MusicPlayerManager.isPlaying
    
    init {
        startPositionUpdater()
    }
    
    private fun startPositionUpdater() {
        viewModelScope.launch {
            while (true) {
                if (isPlaying.value) {
                    _currentPosition.value = MusicPlayerManager.getCurrentPosition(getApplication())
                    _duration.value = MusicPlayerManager.getDuration(getApplication())
                }
                delay(100)
            }
        }
    }
    
    fun loadSongs() {
        viewModelScope.launch {
            _songs.value = repository.getAllSongs()
            Log.d(TAG, "Loaded ${_songs.value.size} songs")
            if (_songs.value.isNotEmpty() && _currentSong.value == null) {
                _currentSong.value = _songs.value.first()
                Log.d(TAG, "Set current song to: ${_currentSong.value?.title}")
            }
        }
    }
    
    fun playSong(song: Song) {
        Log.d(TAG, "=== PLAY SONG CALLED ===")
        Log.d(TAG, "Song: ${song.title}")
        _currentSong.value = song
        MusicPlayerManager.playSong(getApplication(), song)
    }
    
    fun togglePlayPause() {
        val song = _currentSong.value
        Log.d(TAG, "=== TOGGLE PLAY/PAUSE CALLED ===")
        Log.d(TAG, "Current song: ${song?.title}")
        Log.d(TAG, "Is playing: ${isPlaying.value}")
        
        if (song == null) {
            Log.w(TAG, "No song selected, loading first song")
            if (_songs.value.isNotEmpty()) {
                playSong(_songs.value.first())
            }
            return
        }
        
        MusicPlayerManager.togglePlayPause(getApplication())
    }
    
    fun seekTo(position: Long) {
        MusicPlayerManager.seekTo(getApplication(), position)
        _currentPosition.value = position
    }
    
    fun playNext() {
        if (_songs.value.isEmpty()) {
            Log.w(TAG, "No songs available")
            return
        }
        
        val currentIndex = _songs.value.indexOf(_currentSong.value)
        
        if (_isRepeatEnabled.value) {
            _currentSong.value?.let { playSong(it) }
            return
        }
        
        val nextIndex = if (_isShuffleEnabled.value) {
            val availableIndices = _songs.value.indices.filter { it != currentIndex }
            if (availableIndices.isNotEmpty()) {
                availableIndices.random()
            } else {
                currentIndex
            }
        } else {
            if (currentIndex >= 0 && currentIndex < _songs.value.size - 1) {
                currentIndex + 1
            } else {
                0
            }
        }
        
        if (nextIndex >= 0 && nextIndex < _songs.value.size) {
            playSong(_songs.value[nextIndex])
        }
    }
    
    fun playPrevious() {
        if (_songs.value.isEmpty()) {
            Log.w(TAG, "No songs available")
            return
        }
        
        val currentIndex = _songs.value.indexOf(_currentSong.value)
        
        val prevIndex = if (_isShuffleEnabled.value) {
            val availableIndices = _songs.value.indices.filter { it != currentIndex }
            if (availableIndices.isNotEmpty()) {
                availableIndices.random()
            } else {
                currentIndex
            }
        } else {
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
        Log.d(TAG, "Shuffle: ${_isShuffleEnabled.value}")
    }
    
    fun toggleRepeat() {
        _isRepeatEnabled.value = !_isRepeatEnabled.value
        Log.d(TAG, "Repeat: ${_isRepeatEnabled.value}")
    }
    
    override fun onCleared() {
        super.onCleared()
        MusicPlayerManager.release()
    }
}
