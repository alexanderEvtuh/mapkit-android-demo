package com.yandex.navikitdemo.domain

import android.content.res.AssetFileDescriptor
import com.yandex.mapkit.annotations.LocalizedPhrase
import com.yandex.mapkit.annotations.SpeakerPhraseToken

interface SoundsManager {
    fun needUsePreRecorded(): Boolean
    fun initPhrase(phrase: LocalizedPhrase): Boolean
    fun updateDurations()

    fun pollSoundFile(): Pair<SpeakerPhraseToken, AssetFileDescriptor>?
    fun hasSoundFile(): Boolean

    fun getNextPlayDelay(phrase: SpeakerPhraseToken): Long

    fun clear()
}