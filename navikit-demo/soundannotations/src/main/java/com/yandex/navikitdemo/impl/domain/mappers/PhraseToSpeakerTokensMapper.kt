package com.yandex.navikitdemo.impl.domain.mappers

import com.yandex.mapkit.annotations.LocalizedPhrase
import com.yandex.navikitdemo.impl.domain.models.LocalPhrase

internal interface PhraseToSpeakerTokensMapper {
    fun map(phrase: LocalizedPhrase): LocalPhrase
}