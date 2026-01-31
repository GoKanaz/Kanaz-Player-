package com.gokanaz.kanazplayer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gokanaz.kanazplayer.data.model.Song
import com.gokanaz.kanazplayer.ui.player.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerOptionsBottomSheet(
    song: Song,
    viewModel: PlayerViewModel,
    onDismiss: () -> Unit,
    onShowPlaylistDialog: () -> Unit = {},
    onShowSongDetails: () -> Unit = {},
    onShowTagEditor: () -> Unit = {},
    onShowSleepTimer: () -> Unit = {},
    onShowEqualizer: () -> Unit = {},
    onShowPlaySpeed: () -> Unit = {}
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
            OptionItem(
                icon = Icons.Default.Edit,
                text = "Tag editor",
                onClick = {
                    onShowTagEditor()
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
                icon = Icons.Default.Favorite,
                text = "Add to my favorites",
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
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            OptionItem(
                icon = Icons.Default.Timer,
                text = "Sleep timer",
                onClick = {
                    onShowSleepTimer()
                    onDismiss()
                }
            )
            
            OptionItem(
                icon = Icons.Default.GraphicEq,
                text = "Equalizer",
                onClick = {
                    onShowEqualizer()
                    onDismiss()
                }
            )
            
            OptionItem(
                icon = Icons.Default.Lyrics,
                text = "Lyrics settings",
                onClick = {
                    onDismiss()
                }
            )
            
            OptionItem(
                icon = Icons.Default.Speed,
                text = "Play speed",
                onClick = {
                    onShowPlaySpeed()
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
