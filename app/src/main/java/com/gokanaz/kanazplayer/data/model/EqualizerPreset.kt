package com.gokanaz.kanazplayer.data.model

data class EqualizerPreset(
    val name: String,
    val bands: List<Int>
) {
    companion object {
        fun getPresets(): List<EqualizerPreset> {
            return listOf(
                EqualizerPreset("Normal", listOf(0, 0, 0, 0, 0)),
                EqualizerPreset("Klasik", listOf(0, 0, 0, 200, 300)),
                EqualizerPreset("Dance", listOf(600, 300, 100, 0, 300)),
                EqualizerPreset("Rata", listOf(300, 300, 300, 300, 300)),
                EqualizerPreset("Folk", listOf(300, 200, 0, -100, -200)),
                EqualizerPreset("Heavy Metal", listOf(600, 300, 100, 400, 500)),
                EqualizerPreset("Hip Hop", listOf(500, 400, 100, 200, 300)),
                EqualizerPreset("Jazz", listOf(300, 200, 100, 200, 400)),
                EqualizerPreset("Pop", listOf(200, 400, 300, 200, 100)),
                EqualizerPreset("Rock", listOf(500, 300, -100, 100, 500)),
                EqualizerPreset("Penguat FX", listOf(400, 200, 0, 300, 400)),
                EqualizerPreset("Pengguna", listOf(0, 0, 0, 0, 0))
            )
        }
    }
}
