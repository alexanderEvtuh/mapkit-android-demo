package com.yandex.navikitdemo.domain

import com.yandex.mapkit.annotations.SpeakerPhraseToken
import com.yandex.navikitdemo.domain.models.LocalPhrase

interface SpeakerTokensManager {
    fun getLocalPhrase(token: SpeakerPhraseToken, path: String): LocalPhrase
}