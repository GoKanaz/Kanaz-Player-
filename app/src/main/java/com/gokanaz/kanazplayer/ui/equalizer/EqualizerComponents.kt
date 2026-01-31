package com.gokanaz.kanazplayer.ui.equalizer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gokanaz.kanazplayer.data.model.EqualizerPreset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqualizerPresetDropdown(
    currentPreset: String,
    onPresetSelected: (String) -> Unit,
    enabled: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    val presets = EqualizerPreset.getPresets()
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded }
    ) {
        OutlinedTextField(
            value = currentPreset,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            presets.forEach { preset ->
                DropdownMenuItem(
                    text = { Text(preset.name) },
                    onClick = {
                        onPresetSelected(preset.name)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun EqualizerBands(
    band60Hz: Int,
    band230Hz: Int,
    band910Hz: Int,
    band4kHz: Int,
    band14kHz: Int,
    onBand60HzChange: (Int) -> Unit,
    onBand230HzChange: (Int) -> Unit,
    onBand910HzChange: (Int) -> Unit,
    onBand4kHzChange: (Int) -> Unit,
    onBand14kHzChange: (Int) -> Unit,
    enabled: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "+10 dB",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            EqualizerBand(
                value = band60Hz,
                onValueChange = onBand60HzChange,
                label = "60 Hz",
                enabled = enabled
            )
            EqualizerBand(
                value = band230Hz,
                onValueChange = onBand230HzChange,
                label = "230 Hz",
                enabled = enabled
            )
            EqualizerBand(
                value = band910Hz,
                onValueChange = onBand910HzChange,
                label = "910 Hz",
                enabled = enabled
            )
            EqualizerBand(
                value = band4kHz,
                onValueChange = onBand4kHzChange,
                label = "4 kHz",
                enabled = enabled
            )
            EqualizerBand(
                value = band14kHz,
                onValueChange = onBand14kHzChange,
                label = "14 kHz",
                enabled = enabled
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "0 dB",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "-10 dB",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun EqualizerBand(
    value: Int,
    onValueChange: (Int) -> Unit,
    label: String,
    enabled: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(60.dp)
    ) {
        Box(
            modifier = Modifier
                .height(160.dp)
                .width(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Slider(
                value = -value.toFloat(), // Invert for visual correctness
                onValueChange = { onValueChange(-it.toInt()) },
                valueRange = -1000f..1000f,
                enabled = enabled,
                modifier = Modifier
                    .width(160.dp)
                    .rotate(270f), // Rotate to make it vertical
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                    disabledActiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = label,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        )
    }
}
