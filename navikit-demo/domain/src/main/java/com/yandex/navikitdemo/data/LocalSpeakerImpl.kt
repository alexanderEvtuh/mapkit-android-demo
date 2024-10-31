package com.yandex.navikitdemo.data

import com.yandex.mapkit.annotations.LocalizedPhrase
import com.yandex.navikitdemo.domain.PlayerManager
import com.yandex.navikitdemo.domain.SoundsManager
import com.yandex.navikitdemo.domain.SpeakerManager
import com.yandex.navikitdemo.domain.models.LocalPhrase
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalSpeakerImpl @Inject constructor(
    private val soundsManager: SoundsManager,
    private val playerManager: PlayerManager
) : SpeakerManager {

    private val scope = MainScope()
    private val phrasesImpl = MutableSharedFlow<String>()

    private var localPhrase: LocalPhrase? = null

    override fun phrases(): Flow<String> = phrasesImpl

    override fun reset() {
        playerManager.reset()
    }

    override fun say(phrase: LocalizedPhrase) {
        localPhrase?.items?.let(playerManager::play) ?: return

        scope.launch {
            phrasesImpl.emit(phrase.text)
        }
    }

    override fun duration(phrase: LocalizedPhrase): Double {
        localPhrase = soundsManager.generateLocalPhrase(phrase)
        return (localPhrase?.duration ?: 0.0) / 1000.0
    }
}