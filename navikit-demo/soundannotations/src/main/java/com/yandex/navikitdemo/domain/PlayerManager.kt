package com.yandex.navikitdemo.domain

import com.yandex.navikitdemo.domain.models.LocalToken

interface PlayerManager {
    fun play(queue: List<LocalToken>)
    fun reset()
}