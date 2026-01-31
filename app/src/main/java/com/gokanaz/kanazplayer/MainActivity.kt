package com.gokanaz.kanazplayer

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gokanaz.kanazplayer.service.MusicPlaybackService
import com.gokanaz.kanazplayer.ui.equalizer.EqualizerScreen
import com.gokanaz.kanazplayer.ui.library.SongListScreen
import com.gokanaz.kanazplayer.ui.player.PlayerScreen
import com.gokanaz.kanazplayer.ui.player.PlayerViewModel
import com.gokanaz.kanazplayer.ui.playlist.PlaylistScreen
import com.gokanaz.kanazplayer.ui.queue.QueueScreen
import com.gokanaz.kanazplayer.ui.settings.SettingsScreen
import com.gokanaz.kanazplayer.ui.theme.KanazPlayerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val serviceIntent = Intent(this, MusicPlaybackService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
        
        setContent {
            var isDarkTheme by remember { mutableStateOf(isSystemInDarkTheme()) }
            
            KanazPlayerTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MusicPlayerApp(
                        isDarkTheme = isDarkTheme,
                        onThemeChange = { isDarkTheme = it }
                    )
                }
            }
        }
    }
}

enum class Screen {
    PLAYER, LIBRARY, QUEUE, SETTINGS, PLAYLISTS, EQUALIZER
}

@Composable
fun MusicPlayerApp(
    viewModel: PlayerViewModel = viewModel(),
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    var currentScreen by remember { mutableStateOf(Screen.PLAYER) }
    val songs by viewModel.songs.collectAsState()
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val queue by viewModel.queue.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val equalizerEnabled by viewModel.equalizerEnabled.collectAsState()
    val equalizerPreset by viewModel.equalizerPreset.collectAsState()
    val equalizerBands by viewModel.equalizerBands.collectAsState()
    val bassBoost by viewModel.bassBoost.collectAsState()
    val virtualizer by viewModel.virtualizer.collectAsState()
    
    when (currentScreen) {
        Screen.PLAYER -> {
            PlayerScreen(
                viewModel = viewModel,
                onLibraryClick = { currentScreen = Screen.LIBRARY },
                onQueueClick = { currentScreen = Screen.QUEUE },
                onSettingsClick = { currentScreen = Screen.SETTINGS },
                onPlaylistsClick = { currentScreen = Screen.PLAYLISTS },
                onEqualizerClick = { currentScreen = Screen.EQUALIZER }
            )
        }
        Screen.LIBRARY -> {
            SongListScreen(
                songs = songs,
                currentSong = currentSong,
                isPlaying = isPlaying,
                onSongClick = { song ->
                    viewModel.playSong(song)
                    currentScreen = Screen.PLAYER
                },
                onBackClick = { currentScreen = Screen.PLAYER }
            )
        }
        Screen.QUEUE -> {
            QueueScreen(
                queue = queue,
                currentSong = currentSong,
                isPlaying = isPlaying,
                onSongClick = { song ->
                    viewModel.playSong(song)
                    currentScreen = Screen.PLAYER
                },
                onRemoveFromQueue = { index ->
                    viewModel.removeFromQueue(index)
                },
                onClearQueue = {
                    viewModel.clearQueue()
                },
                onBackClick = { currentScreen = Screen.PLAYER }
            )
        }
        Screen.SETTINGS -> {
            SettingsScreen(
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange,
                onBackClick = { currentScreen = Screen.PLAYER }
            )
        }
        Screen.PLAYLISTS -> {
            PlaylistScreen(
                playlists = playlists,
                onPlaylistClick = { playlist ->
                    currentScreen = Screen.PLAYER
                },
                onCreatePlaylist = { name ->
                    viewModel.createPlaylist(name)
                },
                onBackClick = { currentScreen = Screen.PLAYER }
            )
        }
        Screen.EQUALIZER -> {
            EqualizerScreen(
                isEnabled = equalizerEnabled,
                selectedPreset = equalizerPreset,
                bandLevels = equalizerBands,
                bassBoost = bassBoost,
                virtualizer = virtualizer,
                onEnabledChange = { viewModel.setEqualizerEnabled(it) },
                onPresetChange = { viewModel.setEqualizerPreset(it) },
                onBandLevelChange = { band, level -> viewModel.setEqualizerBand(band, level) },
                onBassBoostChange = { viewModel.setBassBoost(it) },
                onVirtualizerChange = { viewModel.setVirtualizer(it) },
                onBackClick = { currentScreen = Screen.PLAYER }
            )
        }
    }
}
