package com.yandex.navikitdemo.impl.data.mappers

import com.yandex.mapkit.annotations.LocalizedPhrase
import com.yandex.mapkit.annotations.SpeakerPhraseToken
import com.yandex.navikitdemo.impl.data.getSoundPath
import com.yandex.navikitdemo.impl.domain.LocalLanguageProvider
import com.yandex.navikitdemo.impl.domain.SpeakerTokensManager
import com.yandex.navikitdemo.impl.domain.mappers.PhraseToSpeakerTokensMapper
import com.yandex.navikitdemo.impl.domain.models.LocalPhrase
import com.yandex.navikitdemo.impl.domain.models.LocalToken
import com.yandex.navikitdemo.impl.domain.utils.path

internal class PhraseToSpeakerTokensMapperImpl(
    private val localLanguageProvider: LocalLanguageProvider,
    private val speakerTokens: SpeakerTokensManager
) : PhraseToSpeakerTokensMapper {

    override fun map(phrase: LocalizedPhrase): LocalPhrase {
        val localTokens = mutableListOf<LocalToken>()
        val isSpeedLimit = phrase.tokens.map { it.path }
            .contains(SpeakerPhraseToken.SPEED_LIMIT_EXCEEDED.path)
        val path = localLanguageProvider.changes().value.getSoundPath()

        phrase.tokens.forEach {
            localTokens.add(speakerTokens.getLocalToken(it, String.format(path, it.path)))
        }
        return LocalPhrase(localTokens)
    }
}