package com.yandex.navikitdemo.data

import android.media.MediaPlayer
import com.yandex.navikitdemo.domain.PlayerManager
import com.yandex.navikitdemo.domain.models.LocalToken
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class PlayerManagerImpl @Inject constructor() : PlayerManager {
    private val scope = MainScope()
    private val mediaPlayer = MediaPlayer()

    private var playListJob: Job? = null

    override fun play(queue: List<LocalToken>) {
        playListJob?.cancel()
        playListJob = queue.asFlow()
            .onEach {
                mediaPlayer.stop()
                mediaPlayer.reset()
                mediaPlayer.setDataSource(it.fileDescriptor)
                mediaPlayer.prepare()
                mediaPlayer.start()
                delay(it.duration.toLong())
            }
            .launchIn(scope)
    }

    override fun reset() {
        playListJob?.cancel()
        mediaPlayer.reset()
    }
}