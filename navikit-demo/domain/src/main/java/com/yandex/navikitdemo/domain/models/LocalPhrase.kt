package com.yandex.navikitdemo.domain.models

import android.content.res.AssetFileDescriptor
import com.yandex.mapkit.annotations.SpeakerPhraseToken

data class LocalPhrase(
    val token: SpeakerPhraseToken,
    val fileDescriptor: AssetFileDescriptor,
    val duration: Double
)
