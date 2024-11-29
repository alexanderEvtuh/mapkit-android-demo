package com.yandex.navikitdemo.domain.mappers

import com.yandex.mapkit.annotations.LocalizedPhrase

interface PhraseToSpeakerTokensMapper {
    fun map(phrase: LocalizedPhrase): com.yandex.navikitdemo.domain.models.LocalPhrase
}