package com.gokanaz.kanazplayer.ui.library.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.gokanaz.kanazplayer.ui.player.PlayerViewModel

@Composable
fun PlaylistsTab(viewModel: PlayerViewModel) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Playlists Tab")
    }
}
