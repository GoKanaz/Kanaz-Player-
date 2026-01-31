package com.gokanaz.kanazplayer.service

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.Virtualizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object EqualizerManager {
    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null
    
    private val _isEnabled = MutableStateFlow(false)
    val isEnabled: StateFlow<Boolean> = _isEnabled
    
    private val _selectedPreset = MutableStateFlow("Normal")
    val selectedPreset: StateFlow<String> = _selectedPreset
    
    private val _bandLevels = MutableStateFlow(List(5) { 0f })
    val bandLevels: StateFlow<List<Float>> = _bandLevels
    
    private val _bassBoostLevel = MutableStateFlow(0f)
    val bassBoostLevel: StateFlow<Float> = _bassBoostLevel
    
    private val _virtualizerLevel = MutableStateFlow(0f)
    val virtualizerLevel: StateFlow<Float> = _virtualizerLevel
    
    private val presetValues = mapOf(
        "Normal" to listOf(0f, 0f, 0f, 0f, 0f),
        "Klasik" to listOf(5f, 3f, -2f, 4f, 4f),
        "Dance" to listOf(4f, 7f, 2f, 0f, 1f),
        "Rata" to listOf(6f, 0f, 0f, 0f, 6f),
        "Folk" to listOf(5f, 0f, 0f, 2f, -1f),
        "Heavy Metal" to listOf(4f, 3f, 0f, 4f, 5f),
        "Hip Hop" to listOf(5f, 4f, -1f, 3f, 4f),
        "Jazz" to listOf(5f, 3f, -2f, 2f, 6f),
        "Pop" to listOf(-2f, 2f, 5f, 1f, -2f),
        "Rock" to listOf(5f, 3f, -1f, 3f, 5f),
        "Penguat FX" to listOf(3f, 0f, 4f, 5f, 3f),
        "Pengguna" to listOf(0f, 0f, 0f, 0f, 0f)
    )
    
    fun initialize(audioSessionId: Int) {
        try {
            equalizer = Equalizer(0, audioSessionId).apply {
                enabled = false
            }
            bassBoost = BassBoost(0, audioSessionId).apply {
                enabled = false
            }
            virtualizer = Virtualizer(0, audioSessionId).apply {
                enabled = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun setEnabled(enabled: Boolean) {
        _isEnabled.value = enabled
        equalizer?.enabled = enabled
        bassBoost?.enabled = enabled
        virtualizer?.enabled = enabled
    }
    
    fun setPreset(preset: String) {
        _selectedPreset.value = preset
        presetValues[preset]?.let { values ->
            _bandLevels.value = values
            applyBandLevels(values)
        }
    }
    
    fun setBandLevel(band: Int, level: Float) {
        val levels = _bandLevels.value.toMutableList()
        levels[band] = level
        _bandLevels.value = levels
        _selectedPreset.value = "Pengguna"
        
        try {
            equalizer?.setBandLevel(
                band.toShort(),
                (level * 100).toInt().toShort()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun applyBandLevels(levels: List<Float>) {
        try {
            levels.forEachIndexed { index, level ->
                equalizer?.setBandLevel(
                    index.toShort(),
                    (level * 100).toInt().toShort()
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun setBassBoost(level: Float) {
        _bassBoostLevel.value = level
        try {
            bassBoost?.setStrength((level * 1000).toInt().toShort())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun setVirtualizer(level: Float) {
        _virtualizerLevel.value = level
        try {
            virtualizer?.setStrength((level * 1000).toInt().toShort())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun release() {
        equalizer?.release()
        bassBoost?.release()
        virtualizer?.release()
        equalizer = null
        bassBoost = null
        virtualizer = null
    }
}
