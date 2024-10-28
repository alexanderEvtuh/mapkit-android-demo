package com.yandex.navikitdemo.domain.models

import android.content.res.AssetFileDescriptor
import com.yandex.mapkit.annotations.SpeakerPhraseToken

data class LocalToken(
    val token: SpeakerPhraseToken,
    val fileDescriptor: AssetFileDescriptor,
    val duration: Double
)

class LocalPhrase(val items: List<LocalToken>) {
    fun summDuration() = items.sumOf { it.duration }
}
