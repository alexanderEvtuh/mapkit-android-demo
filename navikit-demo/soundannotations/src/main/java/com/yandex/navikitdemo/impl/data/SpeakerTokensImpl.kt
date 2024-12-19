package com.yandex.navikitdemo.impl.data

import android.content.Context
import android.net.Uri
import com.yandex.mapkit.annotations.SpeakerPhraseToken
import com.yandex.navikitdemo.impl.domain.LocalLanguageProvider
import com.yandex.navikitdemo.impl.domain.SpeakerTokensManager
import com.yandex.navikitdemo.impl.domain.models.LocalToken
import com.yandex.navikitdemo.impl.domain.utils.path
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


internal class SpeakerTokensImpl(
    private val context: Context,
    localLanguageProvider: LocalLanguageProvider,
) : SpeakerTokensManager {

    private val scope = MainScope()

    private val soundDurations = mutableMapOf<String, Double>()

    init {
        localLanguageProvider.changes().onEach {
            soundDurations.clear()
            soundDurations.putAll(it.getDurations(context.assets))
        }.launchIn(scope)
    }

    override fun getLocalToken(token: SpeakerPhraseToken, path: String): LocalToken =
        LocalToken(token, soundDurations[token.path] ?: 0.0, Uri.parse("asset:///$path"))
}