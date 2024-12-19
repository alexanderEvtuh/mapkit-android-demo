package com.yandex.navikitdemo.impl.data

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.yandex.navikitdemo.impl.domain.PlayerManager
import com.yandex.navikitdemo.impl.domain.models.LocalToken

internal class PlayerManagerImpl(context: Context) : PlayerManager {
    private val player: ExoPlayer by lazy { ExoPlayer.Builder(context).build() }

    override fun play(queue: List<LocalToken>) {
        player.stop()
        player.clearMediaItems()
        queue.map { MediaItem.fromUri(it.uri) }.forEach(player::addMediaItem)
        player.prepare()
        player.play()
    }

    override fun reset() {
        player.stop()
        player.clearMediaItems()
    }
}