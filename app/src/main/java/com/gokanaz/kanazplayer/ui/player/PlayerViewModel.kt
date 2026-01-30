package com.gokanaz.kanazplayer.ui.player

import android.app.Application
import android.util.Log
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
    private val TAG = "PlayerViewModel"
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
        Log.d(TAG, "PlayerViewModel initialized")
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
            Log.d(TAG, "Loading songs...")
            _songs.value = repository.getAllSongs()
            Log.d(TAG, "Loaded ${_songs.value.size} songs")
            
            if (_songs.value.isNotEmpty() && _currentSong.value == null) {
                _currentSong.value = _songs.value.first()
                Log.d(TAG, "Set current song to: ${_currentSong.value?.title}")
            }
        }
    }
    
    fun playSong(song: Song) {
        Log.d(TAG, "======================================")
        Log.d(TAG, "playSong called from UI")
        Log.d(TAG, "Song: ${song.title}")
        Log.d(TAG, "Path: ${song.path}")
        Log.d(TAG, "======================================")
        _currentSong.value = song
        playerService.playSong(song)
    }
    
    fun togglePlayPause() {
        val song = _currentSong.value
        val playing = isPlaying.value
        
        Log.d(TAG, "======================================")
        Log.d(TAG, "togglePlayPause called")
        Log.d(TAG, "Current song: ${song?.title ?: "NULL"}")
        Log.d(TAG, "Is playing: $playing")
        Log.d(TAG, "======================================")
        
        if (song == null) {
            Log.e(TAG, "❌ No song selected!")
            if (_songs.value.isNotEmpty()) {
                Log.d(TAG, "Auto-selecting first song")
                val firstSong = _songs.value.first()
                _currentSong.value = firstSong
                playerService.playSong(firstSong)
            }
            return
        }
        
        if (!playing) {
            Log.d(TAG, "Not playing - calling playSong")
            playerService.playSong(song)
        } else {
            Log.d(TAG, "Is playing - calling togglePlayPause")
            playerService.togglePlayPause()
        }
    }
    
    fun seekTo(position: Long) {
        playerService.seekTo(position)
        _currentPosition.value = position
    }
    
    fun playNext() {
        if (_songs.value.isEmpty()) {
            Log.w(TAG, "No songs available")
            return
        }
        
        val currentIndex = _songs.value.indexOf(_currentSong.value)
        Log.d(TAG, "playNext - current index: $currentIndex")
        
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
        
        Log.d(TAG, "Playing next at index: $nextIndex")
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
        
        Log.d(TAG, "Playing previous at index: $prevIndex")
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
        Log.d(TAG, "PlayerViewModel cleared")
        playerService.release()
    }
}
