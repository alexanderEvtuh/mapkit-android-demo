package com.yandex.navikitdemo.data.mappers

import com.yandex.mapkit.annotations.AnnotationLanguage
import com.yandex.mapkit.annotations.LocalizedPhrase
import com.yandex.mapkit.annotations.SpeakerPhraseToken
import com.yandex.navikitdemo.domain.SettingsManager
import com.yandex.navikitdemo.domain.SpeakerTokensManager
import com.yandex.navikitdemo.domain.mappers.PhraseToSpeakerTokensMapper
import com.yandex.navikitdemo.domain.models.LocalPhrase
import com.yandex.navikitdemo.domain.utils.path
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PhraseToSpeakerTokensMapperImpl @Inject constructor(
    private val settingsManager: SettingsManager,
    private val speakerTokens: SpeakerTokensManager
) : PhraseToSpeakerTokensMapper {

    override fun map(phrase: LocalizedPhrase): List<LocalPhrase> {
        val localPhrases = mutableListOf<LocalPhrase>()
        (if (phrase.tokens.map { it.path }
                .contains(SpeakerPhraseToken.SPEED_LIMIT_EXCEEDED.path)) {
            phrase.tokens.map {
                speakerTokens.getLocalPhrase(
                    it,
                    "sounds/default/${SpeakerPhraseToken.SPEED_LIMIT_EXCEEDED.path}/0.mp3"
                )
            }
        } else if (settingsManager.annotationLanguage.value == AnnotationLanguage.ENGLISH) {
            phrase.tokens.map {
                speakerTokens.getLocalPhrase(it, "sounds/en_male/${it.path}/0.mp3")
            }
        } else {
            phrase.tokens.map {
                speakerTokens.getLocalPhrase(it, "sounds/ru_female/${it.path}/0.mp3")
            }
        }).forEach(localPhrases::add)
        return localPhrases
    }
}