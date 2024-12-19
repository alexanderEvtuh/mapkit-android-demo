package com.yandex.navikitdemo.impl.domain

import com.yandex.navikitdemo.impl.domain.models.LocalToken

internal interface PlayerManager {
    fun play(queue: List<LocalToken>)
    fun reset()
}