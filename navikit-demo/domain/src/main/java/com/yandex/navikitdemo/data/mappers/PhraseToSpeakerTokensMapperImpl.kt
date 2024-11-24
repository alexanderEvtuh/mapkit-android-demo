package com.yandex.navikitdemo.data.mappers

import com.yandex.mapkit.annotations.LocalizedPhrase
import com.yandex.mapkit.annotations.SpeakerPhraseToken
import com.yandex.navikitdemo.domain.LocalLanguageProvider
import com.yandex.navikitdemo.domain.SpeakerTokensManager
import com.yandex.navikitdemo.domain.mappers.PhraseToSpeakerTokensMapper
import com.yandex.navikitdemo.domain.models.LocalPhrase
import com.yandex.navikitdemo.domain.models.LocalToken
import com.yandex.navikitdemo.domain.utils.path
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhraseToSpeakerTokensMapperImpl @Inject constructor(
    private val localLanguageProvider: LocalLanguageProvider,
    private val speakerTokens: SpeakerTokensManager
) : PhraseToSpeakerTokensMapper {

    override fun map(phrase: LocalizedPhrase): LocalPhrase {
        val localTokens = mutableListOf<LocalToken>()
        val isSpeedLimit = phrase.tokens.map { it.path }
            .contains(SpeakerPhraseToken.SPEED_LIMIT_EXCEEDED.path)
        val path = localLanguageProvider.changes().value.getSoundPath(isSpeedLimit)

        phrase.tokens.forEach {
            localTokens.add(speakerTokens.getLocalToken(it, String.format(path, it.path)))
        }
        return LocalPhrase(localTokens)
    }
}