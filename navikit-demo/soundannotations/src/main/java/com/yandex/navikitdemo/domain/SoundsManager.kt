package com.yandex.navikitdemo.domain

import com.yandex.mapkit.annotations.LocalizedPhrase
import com.yandex.navikitdemo.domain.models.LocalPhrase

interface SoundsManager {
    fun generateLocalPhrase(phrase: LocalizedPhrase): LocalPhrase
}