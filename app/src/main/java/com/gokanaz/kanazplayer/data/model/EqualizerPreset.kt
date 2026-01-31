package com.gokanaz.kanazplayer.data.model

data class EqualizerPreset(
    val name: String,
    val bands: List<Int>
) {
    companion object {
        fun getPresets(): List<EqualizerPreset> {
            return listOf(
                EqualizerPreset("Normal", listOf(0, 0, 0, 0, 0)),
                EqualizerPreset("Klasik", listOf(0, 0, 0, 0, 0)),
                EqualizerPreset("Dance", listOf(300, 0, 200, 400, 100)),
                EqualizerPreset("Rata", listOf(300, 300, 300, 300, 300)),
                EqualizerPreset("Folk", listOf(300, 0, 0, 200, -100)),
                EqualizerPreset("Heavy Metal", listOf(400, 100, 300, 400, 300)),
                EqualizerPreset("Hip Hop", listOf(500, 300, 0, 200, 300)),
                EqualizerPreset("Jazz", listOf(0, 0, 0, 300, 400)),
                EqualizerPreset("Pop", listOf(-100, 200, 400, 300, 0)),
                EqualizerPreset("Rock", listOf(500, 300, -100, 300, 500)),
                EqualizerPreset("Penguat FX", listOf(300, 0, 400, 500, 300)),
                EqualizerPreset("Pengguna", listOf(0, 0, 0, 0, 0))
            )
        }
    }
}
