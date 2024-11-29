package com.yandex.navikitdemo.data.mappers

import com.yandex.mapkit.annotations.LocalizedPhrase
import com.yandex.mapkit.annotations.SpeakerPhraseToken
import com.yandex.navikitdemo.data.getSoundPath
import com.yandex.navikitdemo.domain.LocalLanguageProvider
import com.yandex.navikitdemo.domain.mappers.PhraseToSpeakerTokensMapper
import com.yandex.navikitdemo.domain.utils.path
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhraseToSpeakerTokensMapperImpl @Inject constructor(
    private val localLanguageProvider: LocalLanguageProvider,
    private val speakerTokens: com.yandex.navikitdemo.domain.SpeakerTokensManager
) : PhraseToSpeakerTokensMapper {

    override fun map(phrase: LocalizedPhrase): com.yandex.navikitdemo.domain.models.LocalPhrase {
        val localTokens = mutableListOf<com.yandex.navikitdemo.domain.models.LocalToken>()
        val isSpeedLimit = phrase.tokens.map { it.path }
            .contains(SpeakerPhraseToken.SPEED_LIMIT_EXCEEDED.path)
        val path = localLanguageProvider.changes().value.getSoundPath()

        phrase.tokens.forEach {
            localTokens.add(speakerTokens.getLocalToken(it, String.format(path, it.path)))
        }
        return com.yandex.navikitdemo.domain.models.LocalPhrase(localTokens)
    }
}