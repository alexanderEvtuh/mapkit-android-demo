package com.yandex.navikitdemo.impl.domain

import com.yandex.mapkit.annotations.SpeakerPhraseToken
import com.yandex.navikitdemo.impl.domain.models.LocalToken

internal interface SpeakerTokensManager {
    fun getLocalToken(token: SpeakerPhraseToken, path: String): LocalToken
}