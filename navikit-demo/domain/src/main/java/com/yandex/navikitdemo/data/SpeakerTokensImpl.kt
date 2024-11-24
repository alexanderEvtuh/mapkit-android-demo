package com.yandex.navikitdemo.data

import android.net.Uri
import com.yandex.mapkit.annotations.SpeakerPhraseToken
import com.yandex.navikitdemo.domain.LocalLanguageProvider
import com.yandex.navikitdemo.domain.SpeakerTokensManager
import com.yandex.navikitdemo.domain.models.LocalToken
import com.yandex.navikitdemo.domain.utils.path
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SpeakerTokensImpl @Inject constructor(
    private val localLanguageProvider: LocalLanguageProvider,
) : SpeakerTokensManager {

    private val scope = MainScope()

    private val soundDurations = mutableMapOf<String, Double>()

    init {
        localLanguageProvider.changes().onEach {
            soundDurations.clear()
            soundDurations.putAll(it.getDurations())
        }.launchIn(scope)
    }

    override fun getLocalToken(token: SpeakerPhraseToken, path: String): LocalToken =
        LocalToken(token, soundDurations[token.path] ?: 0.0, Uri.parse("asset:///$path"))
}