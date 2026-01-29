package com.gokanaz.kanazplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gokanaz.kanazplayer.ui.library.SongListScreen
import com.gokanaz.kanazplayer.ui.player.PlayerScreen
import com.gokanaz.kanazplayer.ui.player.PlayerViewModel
import com.gokanaz.kanazplayer.ui.theme.KanazPlayerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KanazPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MusicPlayerApp()
                }
            }
        }
    }
}

@Composable
fun MusicPlayerApp(
    viewModel: PlayerViewModel = viewModel()
) {
    var showLibrary by remember { mutableStateOf(false) }
    val songs by viewModel.songs.collectAsState()
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    
    if (showLibrary) {
        SongListScreen(
            songs = songs,
            currentSong = currentSong,
            isPlaying = isPlaying,
            onSongClick = { song ->
                viewModel.playSong(song)
                showLibrary = false
            },
            onBackClick = { showLibrary = false }
        )
    } else {
        PlayerScreen(
            viewModel = viewModel,
            onLibraryClick = { showLibrary = true }
        )
    }
}
