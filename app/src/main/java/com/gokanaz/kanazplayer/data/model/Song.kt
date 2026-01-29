package com.gokanaz.kanazplayer.data.model

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val path: String,
    val albumArt: String? = null
)
