package com.gokanaz.kanazplayer.ui.equalizer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqualizerScreen(
    isEnabled: Boolean,
    selectedPreset: String,
    bandLevels: List<Float>,
    bassBoost: Float,
    virtualizer: Float,
    onEnabledChange: (Boolean) -> Unit,
    onPresetChange: (String) -> Unit,
    onBandLevelChange: (Int, Float) -> Unit,
    onBassBoostChange: (Float) -> Unit,
    onVirtualizerChange: (Float) -> Unit,
    onBackClick: () -> Unit
) {
    var showPresetMenu by remember { mutableStateOf(false) }
    
    val presets = listOf(
        "Normal", "Klasik", "Dance", "Rata", "Folk",
        "Heavy Metal", "Hip Hop", "Jazz", "Pop", "Rock",
        "Penguat FX", "Pengguna"
    )
    
    val frequencies = listOf("60 Hz", "230 Hz", "910 Hz", "4 kHz", "14 kHz")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Equalizer",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = onEnabledChange
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedButton(
                    onClick = { showPresetMenu = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isEnabled
                ) {
                    Text(
                        selectedPreset,
                        modifier = Modifier.weight(1f),
                        fontSize = 18.sp
                    )
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
                
                DropdownMenu(
                    expanded = showPresetMenu,
                    onDismissRequest = { showPresetMenu = false },
                    modifier = Modifier.fillMaxWidth(0.5f)
                ) {
                    presets.forEach { preset ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    preset,
                                    fontSize = 16.sp,
                                    fontWeight = if (preset == selectedPreset) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                onPresetChange(preset)
                                showPresetMenu = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "+10 dB",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                bandLevels.forEachIndexed { index, level ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        VerticalSlider(
                            value = level,
                            onValueChange = { onBandLevelChange(index, it) },
                            enabled = isEnabled,
                            modifier = Modifier
                                .height(200.dp)
                                .width(48.dp)
                        )
                    }
                }
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "-10 dB",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                frequencies.forEach { freq ->
                    Text(
                        freq,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Penguat bass",
                        fontSize = 16.sp,
                        color = if (isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Slider(
                    value = bassBoost,
                    onValueChange = onBassBoostChange,
                    enabled = isEnabled,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Suara surround",
                        fontSize = 16.sp,
                        color = if (isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Slider(
                    value = virtualizer,
                    onValueChange = onVirtualizerChange,
                    enabled = isEnabled,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun VerticalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = -10f..10f,
            enabled = enabled,
            modifier = Modifier
                .fillMaxHeight()
                .width(48.dp)
        )
    }
}
