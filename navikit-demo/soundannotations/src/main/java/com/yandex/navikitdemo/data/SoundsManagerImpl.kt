package com.yandex.navikitdemo.data

import com.yandex.mapkit.annotations.LocalizedPhrase
import com.yandex.navikitdemo.domain.SoundsManager
import com.yandex.navikitdemo.domain.mappers.PhraseToSpeakerTokensMapper
import com.yandex.navikitdemo.domain.models.LocalPhrase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundsManagerImpl @Inject constructor(
    private val phraseToSpeakerTokensMapper: PhraseToSpeakerTokensMapper,
) : SoundsManager {

    override fun generateLocalPhrase(phrase: LocalizedPhrase): LocalPhrase =
        phraseToSpeakerTokensMapper.map(phrase)
}