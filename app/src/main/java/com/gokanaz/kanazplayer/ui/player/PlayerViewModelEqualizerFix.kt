package com.gokanaz.kanazplayer.ui.player

import android.media.audiofx.Equalizer

fun PlayerViewModel.applyEqualizerSettings() {
    try {
        equalizerEffect?.let { eq ->
            if (eq.enabled) {
                if (eq.numberOfBands >= 5) {
                    eq.setBandLevel(0, band60Hz.value.toShort())
                    eq.setBandLevel(1, band230Hz.value.toShort())
                    eq.setBandLevel(2, band910Hz.value.toShort())
                    eq.setBandLevel(3, band4kHz.value.toShort())
                    eq.setBandLevel(4, band14kHz.value.toShort())
                }
            }
        }
        
        bassBoostEffect?.let { bass ->
            if (bass.enabled) {
                bass.setStrength(bassBoost.value.toShort())
            }
        }
        
        virtualizerEffect?.let { virt ->
            if (virt.enabled) {
                virt.setStrength(virtualizerStrength.value.toShort())
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
