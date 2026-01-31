package com.gokanaz.kanazplayer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gokanaz.kanazplayer.data.model.Song
import com.gokanaz.kanazplayer.ui.player.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongOptionsBottomSheet(
    song: Song,
    viewModel: PlayerViewModel,
    onDismiss: () -> Unit,
    onShowPlaylistDialog: () -> Unit = {},
    onShowSongDetails: () -> Unit = {},
    onShowTagEditor: () -> Unit = {}
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
            
            OptionItem(
                icon = Icons.Default.PlayArrow,
                text = "Play next",
                onClick = {
                    val queue = viewModel.queue.value.toMutableList()
                    if (queue.size > 1) {
                        queue.add(1, song)
                    }
                    onDismiss()
                }
            )
            
            OptionItem(
                icon = Icons.Default.QueueMusic,
                text = "Add to playing queue",
                onClick = {
                    onDismiss()
                }
            )
            
            OptionItem(
                icon = Icons.Default.PlaylistAdd,
                text = "Add to playlist",
                onClick = {
                    onShowPlaylistDialog()
                    onDismiss()
                }
            )
            
            OptionItem(
                icon = Icons.Default.Info,
                text = "Song details",
                onClick = {
                    onShowSongDetails()
                    onDismiss()
                }
            )
            
            OptionItem(
                icon = Icons.Default.Edit,
                text = "Tag editor",
                onClick = {
                    onShowTagEditor()
                    onDismiss()
                }
            )
            
            OptionItem(
                icon = Icons.Default.Image,
                text = "Set album cover",
                onClick = {
                    onDismiss()
                }
            )
            
            OptionItem(
                icon = Icons.Default.Favorite,
                text = "Add to my favorites",
                onClick = {
                    onDismiss()
                }
            )
            
            OptionItem(
                icon = Icons.Default.Share,
                text = "Share",
                onClick = {
                    onDismiss()
                }
            )
            
            OptionItem(
                icon = Icons.Default.MusicNote,
                text = "Ringtone",
                onClick = {
                    onDismiss()
                }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            OptionItem(
                icon = Icons.Default.Delete,
                text = "Remove",
                onClick = {
                    onDismiss()
                },
                isDestructive = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
