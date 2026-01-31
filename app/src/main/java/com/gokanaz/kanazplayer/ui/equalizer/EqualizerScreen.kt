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
                onPresetSelected = { viewModel.setEqualizerPreset(it) },
                enabled = equalizerEnabled
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            EqualizerBands(
                band60Hz = band60Hz,
                band230Hz = band230Hz,
                band910Hz = band910Hz,
                band4kHz = band4kHz,
                band14kHz = band14kHz,
                onBand60HzChange = { viewModel.setBand60Hz(it) },
                onBand230HzChange = { viewModel.setBand230Hz(it) },
                onBand910HzChange = { viewModel.setBand910Hz(it) },
                onBand4kHzChange = { viewModel.setBand4kHz(it) },
                onBand14kHzChange = { viewModel.setBand14kHz(it) },
                enabled = equalizerEnabled
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Penguat bass",
                style = MaterialTheme.typography.titleSmall
            )
            Slider(
                value = bassBoost.toFloat(),
                onValueChange = { viewModel.setBassBoost(it.toInt()) },
                valueRange = 0f..1000f,
                enabled = equalizerEnabled
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Suara surround",
                style = MaterialTheme.typography.titleSmall
            )
            Slider(
                value = virtualizerStrength.toFloat(),
                onValueChange = { viewModel.setVirtualizerStrength(it.toInt()) },
                valueRange = 0f..1000f,
                enabled = equalizerEnabled
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
