package com.gokanaz.kanazplayer.ui.player

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gokanaz.kanazplayer.data.model.Song
import com.gokanaz.kanazplayer.data.repository.MusicRepository
import com.gokanaz.kanazplayer.service.MusicPlayerManager
import com.gokanaz.kanazplayer.service.MusicPlayerService
import com.gokanaz.kanazplayer.service.SleepTimerManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MusicRepository(application)
    private val playerService = MusicPlayerService(application)
    private val context = application
    
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
    
    private val _albumArt = MutableStateFlow<Bitmap?>(null)
    val albumArt: StateFlow<Bitmap?> = _albumArt
    
    private val _queue = MutableStateFlow<List<Song>>(emptyList())
    val queue: StateFlow<List<Song>> = _queue
    
    val isPlaying = playerService.isPlaying
    val sleepTimerActive = SleepTimerManager.isActive
    val sleepTimerRemaining = SleepTimerManager.remainingTime
    
    init {
        startPositionUpdater()
        observeCurrentSong()
    }
    
    private fun observeCurrentSong() {
        viewModelScope.launch {
            MusicPlayerManager.currentSong.collect { song ->
                _currentSong.value = song
                song?.let { loadAlbumArt(it) }
            }
        }
    }
    
    private fun loadAlbumArt(song: Song) {
        viewModelScope.launch {
            _albumArt.value = repository.getAlbumArt(song)
        }
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
            updateQueue()
        }
    }
    
    private fun updateQueue() {
        val currentIndex = _songs.value.indexOf(_currentSong.value)
        if (currentIndex >= 0) {
            _queue.value = _songs.value.drop(currentIndex)
        }
    }
    
    fun playSong(song: Song) {
        _currentSong.value = song
        playerService.playSong(song)
        loadAlbumArt(song)
        updateQueue()
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
        if (_isRepeatEnabled.value) {
            _currentSong.value?.let { playSong(it) }
            return
        }
        
        val currentIndex = _songs.value.indexOf(_currentSong.value)
        val nextIndex = if (_isShuffleEnabled.value) {
            (0 until _songs.value.size).filter { it != currentIndex }.randomOrNull() ?: 0
        } else {
            if (currentIndex < _songs.value.size - 1) currentIndex + 1 else 0
        }
        
        if (nextIndex < _songs.value.size) {
            playSong(_songs.value[nextIndex])
        }
    }
    
    fun playPrevious() {
        val currentIndex = _songs.value.indexOf(_currentSong.value)
        val prevIndex = if (currentIndex > 0) currentIndex - 1 else _songs.value.size - 1
        
        if (prevIndex >= 0 && prevIndex < _songs.value.size) {
            playSong(_songs.value[prevIndex])
        }
    }
    
    fun removeFromQueue(index: Int) {
        val mutableQueue = _queue.value.toMutableList()
        if (index in mutableQueue.indices) {
            mutableQueue.removeAt(index)
            _queue.value = mutableQueue
        }
    }
    
    fun clearQueue() {
        _queue.value = listOf(_currentSong.value).filterNotNull()
    }
    
    fun toggleShuffle() {
        _isShuffleEnabled.value = !_isShuffleEnabled.value
    }
    
    fun toggleRepeat() {
        _isRepeatEnabled.value = !_isRepeatEnabled.value
    }
    
    fun setSleepTimer(minutes: Int) {
        SleepTimerManager.startTimer(context, minutes)
    }
    
    fun cancelSleepTimer() {
        SleepTimerManager.cancelTimer()
    }
    
    override fun onCleared() {
        super.onCleared()
        playerService.release()
    }
}
