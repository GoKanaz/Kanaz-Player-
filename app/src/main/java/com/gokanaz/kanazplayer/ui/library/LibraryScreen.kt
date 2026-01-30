package com.gokanaz.kanazplayer.ui.library

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.gokanaz.kanazplayer.data.model.Song
import com.gokanaz.kanazplayer.ui.player.PlayerViewModel

@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel,
    onBackClick: () -> Unit,
    onSongClick: (Song) -> Unit
) {
    Text("Library")
}
