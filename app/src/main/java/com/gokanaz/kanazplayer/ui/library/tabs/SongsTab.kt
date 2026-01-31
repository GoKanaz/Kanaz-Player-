package com.gokanaz.kanazplayer.ui.library.tabs

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gokanaz.kanazplayer.data.model.Playlist
import com.gokanaz.kanazplayer.data.model.Song
import com.gokanaz.kanazplayer.ui.player.PlayerViewModel
import kotlinx.coroutines.launch

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
    var showPlaylistDialog by remember { mutableStateOf(false) }
    var showSongDetails by remember { mutableStateOf(false) }
    var songForPlaylist by remember { mutableStateOf<Song?>(null) }
    var songForDetails by remember { mutableStateOf<Song?>(null) }
    
    val scope = rememberCoroutineScope()
    
    if (songs.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No songs found",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        Column {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (songs.isNotEmpty()) {
                                viewModel.playSongs(songs.shuffled(), 0)
                            }
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Shuffle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        "Shuffle all (${songs.size} songs)",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(songs) { song ->
                    var albumArt by remember { mutableStateOf<Bitmap?>(null) }
                    
                    LaunchedEffect(song.id) {
                        scope.launch {
                            albumArt = viewModel.getAlbumArt(song)
                        }
                    }
                    
                    SongItemWithArt(
                        song = song,
                        albumArt = albumArt,
                        isCurrentSong = song.id == currentSong?.id,
                        isPlaying = isPlaying && song.id == currentSong?.id,
                        onClick = { onSongClick(song) },
                        onMenuClick = { selectedSong = song }
                    )
                }
            }
        }
    }
    
    selectedSong?.let { song ->
        SongOptionsBottomSheet(
            song = song,
            onDismiss = { selectedSong = null },
            onPlayNext = {
                viewModel.addToQueueNext(song)
                selectedSong = null
            },
            onAddToQueue = {
                viewModel.addToQueue(song)
                selectedSong = null
            },
            onAddToPlaylist = {
                songForPlaylist = song
                showPlaylistDialog = true
                selectedSong = null
            },
            onShowDetails = {
                songForDetails = song
                showSongDetails = true
                selectedSong = null
            }
        )
    }
    
    if (showPlaylistDialog && songForPlaylist != null) {
        com.gokanaz.kanazplayer.ui.components.AddToPlaylistDialog(
            song = songForPlaylist!!,
            playlists = playlists,
            onDismiss = {
                showPlaylistDialog = false
                songForPlaylist = null
            },
            onCreateNew = { name ->
                viewModel.createPlaylist(name)
            },
            onAddToPlaylist = { playlistId ->
                viewModel.addSongToPlaylist(playlistId, songForPlaylist!!.id)
                showPlaylistDialog = false
                songForPlaylist = null
            }
        )
    }
    
    if (showSongDetails && songForDetails != null) {
        com.gokanaz.kanazplayer.ui.components.SongDetailsDialog(
            song = songForDetails!!,
            onDismiss = {
                showSongDetails = false
                songForDetails = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongOptionsBottomSheet(
    song: Song,
    onDismiss: () -> Unit,
    onPlayNext: () -> Unit,
    onAddToQueue: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onShowDetails: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            com.gokanaz.kanazplayer.ui.components.OptionItem(
                icon = Icons.Default.PlayArrow,
                text = "Play next",
                onClick = onPlayNext
            )
            
            com.gokanaz.kanazplayer.ui.components.OptionItem(
                icon = Icons.Default.QueueMusic,
                text = "Add to playing queue",
                onClick = onAddToQueue
            )
            
            com.gokanaz.kanazplayer.ui.components.OptionItem(
                icon = Icons.Default.PlaylistAdd,
                text = "Add to playlist",
                onClick = onAddToPlaylist
            )
            
            com.gokanaz.kanazplayer.ui.components.OptionItem(
                icon = Icons.Default.Info,
                text = "Song details",
                onClick = onShowDetails
            )
            
            com.gokanaz.kanazplayer.ui.components.OptionItem(
                icon = Icons.Default.Favorite,
                text = "Add to my favorites",
                onClick = onDismiss
            )
            
            com.gokanaz.kanazplayer.ui.components.OptionItem(
                icon = Icons.Default.Share,
                text = "Share",
                onClick = onDismiss
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            com.gokanaz.kanazplayer.ui.components.OptionItem(
                icon = Icons.Default.Delete,
                text = "Remove",
                onClick = onDismiss,
                isDestructive = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SongItemWithArt(
    song: Song,
    albumArt: Bitmap?,
    isCurrentSong: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit,
    onMenuClick: () -> Unit
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
                text = "${song.artist} • ${song.album}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall
            )
        },
        leadingContent = {
            if (albumArt != null) {
                Image(
                    bitmap = albumArt.asImageBitmap(),
                    contentDescription = "Album Art",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isCurrentSong && isPlaying) 
                                Icons.Default.GraphicEq 
                            else 
                                Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = if (isCurrentSong) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        trailingContent = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
    HorizontalDivider()
}
