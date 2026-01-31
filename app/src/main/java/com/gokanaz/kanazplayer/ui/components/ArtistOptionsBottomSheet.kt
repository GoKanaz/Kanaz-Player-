package com.gokanaz.kanazplayer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gokanaz.kanazplayer.data.repository.Artist

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistOptionsBottomSheet(
    artist: Artist,
    onDismiss: () -> Unit,
    onPlay: () -> Unit = {},
    onAddToQueue: () -> Unit = {},
    onAddToPlaylist: () -> Unit = {},
    onSetCover: () -> Unit = {}
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
                text = artist.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Text(
                text = "${artist.songCount} songs",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            OptionItem(
                icon = Icons.Default.PlayArrow,
                text = "Play",
                onClick = {
                    onPlay()
                    onDismiss()
                }
            )
            
            OptionItem(
                icon = Icons.Default.QueueMusic,
                text = "Add to playing queue",
                onClick = {
                    onAddToQueue()
                    onDismiss()
                }
            )
            
            OptionItem(
                icon = Icons.Default.PlaylistAdd,
                text = "Add to playlist",
                onClick = {
                    onAddToPlaylist()
                    onDismiss()
                }
            )
            
            OptionItem(
                icon = Icons.Default.Image,
                text = "Set artist cover",
                onClick = {
                    onSetCover()
                    onDismiss()
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
