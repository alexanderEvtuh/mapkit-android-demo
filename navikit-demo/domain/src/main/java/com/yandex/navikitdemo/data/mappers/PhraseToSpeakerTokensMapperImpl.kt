package com.yandex.navikitdemo.data.mappers

import com.yandex.mapkit.annotations.AnnotationLanguage
import com.yandex.mapkit.annotations.LocalizedPhrase
import com.yandex.mapkit.annotations.SpeakerPhraseToken
import com.yandex.navikitdemo.domain.SettingsManager
import com.yandex.navikitdemo.domain.SpeakerTokensManager
import com.yandex.navikitdemo.domain.mappers.PhraseToSpeakerTokensMapper
import com.yandex.navikitdemo.domain.models.LocalPhrase
import com.yandex.navikitdemo.domain.models.LocalToken
import com.yandex.navikitdemo.domain.utils.path
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PhraseToSpeakerTokensMapperImpl @Inject constructor(
    private val settingsManager: SettingsManager,
    private val speakerTokens: SpeakerTokensManager
) : PhraseToSpeakerTokensMapper {

    override fun map(phrase: LocalizedPhrase): LocalPhrase {
        val localTokens = mutableListOf<LocalToken>()
        val path = when {
            phrase.tokens.map { it.path }
                .contains(SpeakerPhraseToken.SPEED_LIMIT_EXCEEDED.path) -> "sounds/default/%s/0.mp3"

            settingsManager.annotationLanguage.value == AnnotationLanguage.ENGLISH ->
                "sounds/en_male/%s/0.mp3"

            else -> "sounds/ru_female/%s/0.mp3"
        }

        phrase.tokens.forEach {
            localTokens.add(speakerTokens.getLocalToken(it, String.format(path, it.path)))
        }
        return LocalPhrase(localTokens)
    }
}