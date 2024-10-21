package com.yandex.navikitdemo.data

import com.yandex.mapkit.annotations.AnnotationLanguage
import com.yandex.mapkit.annotations.LocalizedPhrase
import com.yandex.navikitdemo.domain.SettingsManager
import com.yandex.navikitdemo.domain.SoundsManager
import com.yandex.navikitdemo.domain.mappers.PhraseToSpeakerTokensMapper
import com.yandex.navikitdemo.domain.models.LocalPhrase
import java.util.LinkedList
import java.util.Queue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundsManagerImpl @Inject constructor(
    private val settingsManager: SettingsManager,
    private val phraseToSpeakerTokensMapper: PhraseToSpeakerTokensMapper,
) : SoundsManager {

    private val soundQueue: Queue<LocalPhrase> = LinkedList()

    override fun needUsePreRecorded(): Boolean =
        settingsManager.annotationLanguage.value in listOf(
            AnnotationLanguage.RUSSIAN,
            AnnotationLanguage.ENGLISH
        ) && settingsManager.preRecordedAnnotations.value

    override fun initPhrase(phrase: LocalizedPhrase): Boolean =
        try {
            soundQueue.clear()
            soundQueue.addAll(phraseToSpeakerTokensMapper.map(phrase))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    override fun clear() {
        soundQueue.clear()
    }

    override fun pollSoundFile(): LocalPhrase? =
        soundQueue.poll()

    override fun hasSoundFile(): Boolean =
        soundQueue.isNotEmpty()
}