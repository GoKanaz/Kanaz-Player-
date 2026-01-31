package com.gokanaz.kanazplayer.ui.equalizer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqualizerScreen(
    onBackClick: () -> Unit,
    viewModel: com.gokanaz.kanazplayer.ui.player.PlayerViewModel
) {
    val equalizerEnabled by viewModel.equalizerEnabled.collectAsState()
    val currentPreset by viewModel.currentPreset.collectAsState()
    val band60Hz by viewModel.band60Hz.collectAsState()
    val band230Hz by viewModel.band230Hz.collectAsState()
    val band910Hz by viewModel.band910Hz.collectAsState()
    val band4kHz by viewModel.band4kHz.collectAsState()
    val band14kHz by viewModel.band14kHz.collectAsState()
    val bassBoost by viewModel.bassBoost.collectAsState()
    val virtualizerStrength by viewModel.virtualizerStrength.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Equalizer")
                        Switch(
                            checked = equalizerEnabled,
                            onCheckedChange = { viewModel.setEqualizerEnabled(it) }
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            EqualizerPresetDropdown(
                currentPreset = currentPreset,
                onPresetSelected = { 
                    viewModel.setEqualizerPreset(it)
                    if (it != "Pengguna") {
                    }
                },
                enabled = equalizerEnabled
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (equalizerEnabled) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Current: $currentPreset",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Adjust sliders to customize",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            EqualizerBands(
                band60Hz = band60Hz,
                band230Hz = band230Hz,
                band910Hz = band910Hz,
                band4kHz = band4kHz,
                band14kHz = band14kHz,
                onBand60HzChange = { 
                    viewModel.setBand60Hz(it)
                    viewModel.setEqualizerPreset("Pengguna")
                },
                onBand230HzChange = { 
                    viewModel.setBand230Hz(it)
                    viewModel.setEqualizerPreset("Pengguna")
                },
                onBand910HzChange = { 
                    viewModel.setBand910Hz(it)
                    viewModel.setEqualizerPreset("Pengguna")
                },
                onBand4kHzChange = { 
                    viewModel.setBand4kHz(it)
                    viewModel.setEqualizerPreset("Pengguna")
                },
                onBand14kHzChange = { 
                    viewModel.setBand14kHz(it)
                    viewModel.setEqualizerPreset("Pengguna")
                },
                enabled = equalizerEnabled
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            HorizontalDivider()
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Penguat bass",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "0",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$bassBoost",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "1000",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Slider(
                value = bassBoost.toFloat(),
                onValueChange = { viewModel.setBassBoost(it.toInt()) },
                valueRange = 0f..1000f,
                enabled = equalizerEnabled,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Suara surround",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "0",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$virtualizerStrength",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "1000",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Slider(
                value = virtualizerStrength.toFloat(),
                onValueChange = { viewModel.setVirtualizerStrength(it.toInt()) },
                valueRange = 0f..1000f,
                enabled = equalizerEnabled,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary
                )
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    viewModel.resetEqualizer()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = equalizerEnabled
            ) {
                Text("Reset to Default")
            }
        }
    }
}
