package com.gokanaz.kanazplayer.ui.library.tabs

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gokanaz.kanazplayer.ui.player.PlayerViewModel
import kotlin.random.Random

data class Genre(
    val name: String,
    val songCount: Int,
    val color: Color
)

@Composable
fun GenresTab(viewModel: PlayerViewModel) {
    val songs by viewModel.songs.collectAsState()
    
    val genres = remember(songs) {
        listOf(
            Genre("Music", 19, Color(0xFF8B7355)),
            Genre("Heavy Metal", 3, Color(0xFF6B4C9A)),
            Genre("Industrial Music Metal", 1, Color(0xFF8B4C4C)),
            Genre("Thrash Metal", 1, Color(0xFF4C7B8B))
        )
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${genres.size} Genre",
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = { }) {
                Icon(Icons.Default.SwapVert, contentDescription = "Sort")
            }
        }
        
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(genres) { genre ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = genre.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    supportingContent = {
                        Text("${genre.songCount} lagu")
                    },
                    leadingContent = {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = genre.color,
                                    shape = MaterialTheme.shapes.medium
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = genre.name.first().uppercase(),
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                        }
                    },
                    trailingContent = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }
                    },
                    modifier = Modifier.clickable { }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
