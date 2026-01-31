package com.gokanaz.kanazplayer.ui.player

import com.gokanaz.kanazplayer.data.model.Song

fun PlayerViewModel.addToQueue(song: Song) {
    val currentQueue = _queue.value.toMutableList()
    currentQueue.add(song)
    _queue.value = currentQueue
}

fun PlayerViewModel.addToQueueNext(song: Song) {
    val currentQueue = _queue.value.toMutableList()
    if (currentQueue.isNotEmpty()) {
        val currentIndex = currentQueue.indexOfFirst { it.id == _currentSong.value?.id }
        if (currentIndex >= 0 && currentIndex < currentQueue.size - 1) {
            currentQueue.add(currentIndex + 1, song)
        } else {
            currentQueue.add(song)
        }
    } else {
        currentQueue.add(song)
    }
    _queue.value = currentQueue
}
