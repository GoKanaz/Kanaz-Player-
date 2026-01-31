package com.gokanaz.kanazplayer.ui.equalizer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun VerticalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = -1000f..1000f,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val thumbColor = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    val trackColor = if (enabled) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    
    var sliderHeight by remember { mutableStateOf(0f) }
    
    val normalizedValue = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)
    
    Canvas(
        modifier = modifier
            .width(40.dp)
            .height(160.dp)
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                detectVerticalDragGestures { change, dragAmount ->
                    change.consume()
                    val newPosition = (normalizedValue * sliderHeight - dragAmount).coerceIn(0f, sliderHeight)
                    val newValue = valueRange.start + ((sliderHeight - newPosition) / sliderHeight) * (valueRange.endInclusive - valueRange.start)
                    onValueChange(newValue.coerceIn(valueRange.start, valueRange.endInclusive))
                }
            }
    ) {
        sliderHeight = size.height
        
        val trackWidth = 4.dp.toPx()
        val thumbRadius = 10.dp.toPx()
        val centerX = size.width / 2
        
        drawLine(
            color = trackColor,
            start = Offset(centerX, thumbRadius),
            end = Offset(centerX, size.height - thumbRadius),
            strokeWidth = trackWidth,
            cap = StrokeCap.Round
        )
        
        val centerY = size.height / 2
        val thumbY = size.height - (normalizedValue * (size.height - 2 * thumbRadius)) - thumbRadius
        
        drawLine(
            color = thumbColor,
            start = Offset(centerX, centerY),
            end = Offset(centerX, thumbY),
            strokeWidth = trackWidth,
            cap = StrokeCap.Round
        )
        
        drawCircle(
            color = thumbColor,
            radius = thumbRadius,
            center = Offset(centerX, thumbY)
        )
    }
}
