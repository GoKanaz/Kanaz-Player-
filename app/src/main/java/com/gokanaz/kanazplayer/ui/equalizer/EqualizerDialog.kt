package com.gokanaz.kanazplayer.ui.equalizer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gokanaz.kanazplayer.ui.player.PlayerViewModel

@Composable
fun EqualizerDialog(
    viewModel: PlayerViewModel,
    onDismiss: () -> Unit
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
    
    AlertDialog(
        onDismissRequest = onDismiss,
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
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 600.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                EqualizerPresetDropdown(
                    currentPreset = currentPreset,
                    onPresetSelected = { viewModel.setEqualizerPreset(it) },
                    enabled = equalizerEnabled
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
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
                
                Spacer(modifier = Modifier.height(16.dp))
                
                HorizontalDivider()
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Penguat bass: $bassBoost")
                Slider(
                    value = bassBoost.toFloat(),
                    onValueChange = { viewModel.setBassBoost(it.toInt()) },
                    valueRange = 0f..1000f,
                    enabled = equalizerEnabled
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text("Suara surround: $virtualizerStrength")
                Slider(
                    value = virtualizerStrength.toFloat(),
                    onValueChange = { viewModel.setVirtualizerStrength(it.toInt()) },
                    valueRange = 0f..1000f,
                    enabled = equalizerEnabled
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                viewModel.resetEqualizer()
            }) {
                Text("Reset")
            }
        }
    )
}
