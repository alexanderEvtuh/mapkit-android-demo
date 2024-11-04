package com.yandex.navikitdemo.data

import com.yandex.mapkit.annotations.LocalizedPhrase
import com.yandex.mapkit.annotations.Speaker
import com.yandex.navikitdemo.domain.PlayerManager
import com.yandex.navikitdemo.domain.SoundsManager
import com.yandex.navikitdemo.domain.models.LocalPhrase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalSpeakerImpl @Inject constructor(
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