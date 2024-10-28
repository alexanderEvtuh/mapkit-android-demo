package com.yandex.navikitdemo.domain

import com.yandex.navikitdemo.domain.models.LocalPhrase

interface PlayerManager {
    fun play(phrase: LocalPhrase)
    fun reset()
}