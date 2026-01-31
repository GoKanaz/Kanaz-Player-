package com.gokanaz.kanazplayer.ui.library.tabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gokanaz.kanazplayer.data.repository.Album
import com.gokanaz.kanazplayer.ui.player.PlayerViewModel

@Composable
fun AlbumsTab(viewModel: PlayerViewModel) {
    val albums by viewModel.albums.collectAsState()
    val songs by viewModel.songs.collectAsState()
    var selectedAlbum by remember { mutableStateOf<Album?>(null) }
    
    if (albums.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Album,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No albums found",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(albums) { album ->
                AlbumGridItem(
                    album = album,
                    onClick = { 
                        val albumSongs = songs.filter { it.album == album.name }
                        if (albumSongs.isNotEmpty()) {
                            viewModel.playSongs(albumSongs, 0)
                        }
                    },
                    onMenuClick = { selectedAlbum = album }
                )
            }
        }
    }
    
    selectedAlbum?.let { album ->
        AlbumOptionsBottomSheet(
            album = album,
            onDismiss = { selectedAlbum = null },
            onPlay = {
                val albumSongs = songs.filter { it.album == album.name }
                if (albumSongs.isNotEmpty()) {
                    viewModel.playSongs(albumSongs, 0)
                }
                selectedAlbum = null
            },
            onAddToQueue = {
                val albumSongs = songs.filter { it.album == album.name }
                albumSongs.forEach { viewModel.addToQueue(it) }
                selectedAlbum = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumOptionsBottomSheet(
    album: Album,
    onDismiss: () -> Unit,
    onPlay: () -> Unit,
    onAddToQueue: () -> Unit
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
                text = album.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Text(
                text = "${album.songCount} songs",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            com.gokanaz.kanazplayer.ui.components.OptionItem(
                icon = Icons.Default.PlayArrow,
                text = "Play",
                onClick = onPlay
            )
            
            com.gokanaz.kanazplayer.ui.components.OptionItem(
                icon = Icons.Default.QueueMusic,
                text = "Add to playing queue",
                onClick = onAddToQueue
            )
            
            com.gokanaz.kanazplayer.ui.components.OptionItem(
                icon = Icons.Default.PlaylistAdd,
                text = "Add to playlist",
                onClick = onDismiss
            )
            
            com.gokanaz.kanazplayer.ui.components.OptionItem(
                icon = Icons.Default.Image,
                text = "Set album cover",
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
fun AlbumGridItem(
    album: Album,
    onClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Album,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = album.name,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = album.artist,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${album.songCount} songs",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onMenuClick) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
