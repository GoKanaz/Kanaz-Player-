package com.gokanaz.kanazplayer.ui.library.tabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gokanaz.kanazplayer.data.model.Song
import com.gokanaz.kanazplayer.ui.components.*
import com.gokanaz.kanazplayer.ui.player.PlayerViewModel

@Composable
fun SongsTab(
    viewModel: PlayerViewModel,
    onSongClick: (Song) -> Unit
) {
    val songs by viewModel.songs.collectAsState()
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    
    var selectedSong by remember { mutableStateOf<Song?>(null) }
    var showSongOptions by remember { mutableStateOf(false) }
    var showSongDetails by remember { mutableStateOf(false) }
    var showAddToPlaylist by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${songs.size} Lagu",
                style = MaterialTheme.typography.titleMedium
            )
            Row {
                IconButton(onClick = { }) {
                    Icon(Icons.Default.SwapVert, contentDescription = "Sort")
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Default.ViewList, contentDescription = "View")
                }
            }
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { 
                    viewModel.toggleShuffle()
                    if (songs.isNotEmpty()) {
                        onSongClick(songs.random())
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Shuffle, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Acak")
            }
            OutlinedButton(
                onClick = { 
                    if (songs.isNotEmpty()) {
                        onSongClick(songs.first())
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Putar")
            }
        }
        
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(songs) { song ->
                SongListItem(
                    song = song,
                    isCurrentSong = song == currentSong,
                    isPlaying = isPlaying && song == currentSong,
                    onClick = { onSongClick(song) },
                    onMoreClick = {
                        selectedSong = song
                        showSongOptions = true
                    }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
    
    if (showSongOptions && selectedSong != null) {
        SongOptionsBottomSheet(
            song = selectedSong!!,
            viewModel = viewModel,
            onDismiss = { showSongOptions = false },
            onShowPlaylistDialog = {
                showSongOptions = false
                showAddToPlaylist = true
            },
            onShowSongDetails = {
                showSongOptions = false
                showSongDetails = true
            }
        )
    }
    
    if (showSongDetails && selectedSong != null) {
        SongDetailsDialog(
            song = selectedSong!!,
            onDismiss = { showSongDetails = false }
        )
    }
    
    if (showAddToPlaylist && selectedSong != null) {
        AddToPlaylistDialog(
            song = selectedSong!!,
            playlists = playlists,
            onDismiss = { showAddToPlaylist = false },
            onCreateNew = { name ->
                viewModel.createPlaylist(name)
            },
            onAddToPlaylist = { playlistId ->
                viewModel.addSongToPlaylist(playlistId, selectedSong!!.id)
            }
        )
    }
}

@Composable
fun SongListItem(
    song: Song,
    isCurrentSong: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                text = song.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (isCurrentSong) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        },
        supportingContent = {
            Text(
                text = "${song.artist} - ${song.album}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall
            )
        },
        leadingContent = {
            Icon(
                imageVector = if (isCurrentSong && isPlaying) Icons.Default.PlayArrow else Icons.Default.MusicNote,
                contentDescription = null,
                tint = if (isCurrentSong) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            IconButton(onClick = onMoreClick) {
                Icon(Icons.Default.MoreVert, contentDescription = "More")
            }
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}
