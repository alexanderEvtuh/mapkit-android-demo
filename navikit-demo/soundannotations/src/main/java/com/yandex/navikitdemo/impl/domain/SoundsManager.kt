package com.yandex.navikitdemo.impl.domain

import com.yandex.mapkit.annotations.LocalizedPhrase
import com.yandex.navikitdemo.impl.domain.models.LocalPhrase

internal interface SoundsManager {
    fun generateLocalPhrase(phrase: LocalizedPhrase): LocalPhrase
}