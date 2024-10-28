package com.yandex.navikitdemo.domain

import com.yandex.mapkit.annotations.SpeakerPhraseToken
import com.yandex.navikitdemo.domain.models.LocalToken

interface SpeakerTokensManager {
    fun getLocalPhrase(token: SpeakerPhraseToken, path: String): LocalToken
}