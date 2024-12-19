package com.yandex.navikitdemo.impl.data

import com.yandex.mapkit.annotations.LocalizedPhrase
import com.yandex.navikitdemo.impl.domain.SoundsManager
import com.yandex.navikitdemo.impl.domain.mappers.PhraseToSpeakerTokensMapper
import com.yandex.navikitdemo.impl.domain.models.LocalPhrase

internal class SoundsManagerImpl(
    private val phraseToSpeakerTokensMapper: PhraseToSpeakerTokensMapper,
) : SoundsManager {

    override fun generateLocalPhrase(phrase: LocalizedPhrase): LocalPhrase =
        phraseToSpeakerTokensMapper.map(phrase)
}