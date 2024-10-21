package com.yandex.navikitdemo.domain

import android.content.res.AssetFileDescriptor
import com.yandex.mapkit.annotations.LocalizedPhrase
import com.yandex.mapkit.annotations.SpeakerPhraseToken
import com.yandex.navikitdemo.domain.models.LocalPhrase

interface SoundsManager {
    fun needUsePreRecorded(): Boolean
    fun initPhrase(phrase: LocalizedPhrase): Boolean

    fun pollSoundFile(): LocalPhrase?
    fun hasSoundFile(): Boolean

    fun clear()
}