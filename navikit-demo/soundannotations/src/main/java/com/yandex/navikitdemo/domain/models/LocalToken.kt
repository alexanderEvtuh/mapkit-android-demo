package com.yandex.navikitdemo.domain.models

import android.net.Uri
import com.yandex.mapkit.annotations.SpeakerPhraseToken

data class LocalToken(
    val token: SpeakerPhraseToken,
    val duration: Double,
    val uri: Uri,
)

class LocalPhrase(val items: List<LocalToken>) {
    val duration by lazy { items.sumOf { it.duration } }
}
