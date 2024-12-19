package com.yandex.navikitdemo.impl.data

import com.yandex.mapkit.annotations.LocalizedPhrase
import com.yandex.mapkit.annotations.Speaker
import com.yandex.navikitdemo.impl.domain.PlayerManager
import com.yandex.navikitdemo.impl.domain.SoundsManager
import com.yandex.navikitdemo.impl.domain.models.LocalPhrase

internal class LocalSpeaker(
    private val soundsManager: SoundsManager,
    private val playerManager: PlayerManager
) : Speaker {

    private var localPhrase: LocalPhrase? = null

    override fun reset() {
        playerManager.reset()
    }

    override fun say(phrase: LocalizedPhrase) {
        localPhrase?.items?.let(playerManager::play) ?: return
    }

    override fun duration(phrase: LocalizedPhrase): Double {
        localPhrase = soundsManager.generateLocalPhrase(phrase)
        return (localPhrase?.duration ?: 0.0) / 1000.0
    }
}