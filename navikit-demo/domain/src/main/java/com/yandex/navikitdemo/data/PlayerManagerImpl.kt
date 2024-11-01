package com.yandex.navikitdemo.data

import android.content.Context
import android.media.MediaPlayer
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.yandex.navikitdemo.domain.PlayerManager
import com.yandex.navikitdemo.domain.models.LocalToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import javax.inject.Inject

class PlayerManagerImpl @Inject constructor(
    @ApplicationContext context: Context,
) : PlayerManager {
    private val player: ExoPlayer by lazy { ExoPlayer.Builder(context).build() }
    private val mediaPlayer = MediaPlayer()

    private var playListJob: Job? = null

    override fun play(queue: List<LocalToken>) {
        player.stop()
        player.clearMediaItems()
        queue.map { MediaItem.fromUri(it.uri) }.forEach(player::addMediaItem)
        player.prepare()
        player.play()
    }

    override fun reset() {
        playListJob?.cancel()
        mediaPlayer.reset()
    }
}