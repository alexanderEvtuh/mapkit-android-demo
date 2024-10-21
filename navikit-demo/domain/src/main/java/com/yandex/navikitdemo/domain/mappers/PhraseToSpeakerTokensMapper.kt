package com.yandex.navikitdemo.domain.mappers

import com.yandex.mapkit.annotations.LocalizedPhrase
import com.yandex.navikitdemo.domain.models.LocalPhrase

interface PhraseToSpeakerTokensMapper {
    fun map(phrase: LocalizedPhrase): List<LocalPhrase>
}