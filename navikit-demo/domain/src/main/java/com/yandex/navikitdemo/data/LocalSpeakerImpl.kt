package com.yandex.navikitdemo.data

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.yandex.mapkit.annotations.LocalizedPhrase
import com.yandex.navikitdemo.domain.SoundsManager
import com.yandex.navikitdemo.domain.SpeakerManager
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalSpeakerImpl @Inject constructor(
    private val soundsManager: SoundsManager,
) : SpeakerManager {

    private val scope = MainScope()
    private val phrasesImpl = MutableSharedFlow<String>()

    private val mediaPlayer = MediaPlayer()

    private val playerRunnable = Runnable {
        playMediaIfExists()
    }

    private val playerHandler = Handler(Looper.myLooper() ?: Looper.getMainLooper())

    override fun phrases(): Flow<String> = phrasesImpl

    override fun reset() {
        mediaPlayer.reset()
    }

    override fun say(phrase: LocalizedPhrase) {
        soundsManager.initPhrase(phrase)
        playMediaIfExists()

        scope.launch {
            phrasesImpl.emit(phrase.text)
        }
    }

    override fun duration(phrase: LocalizedPhrase): Double {
        // Heuristic formula for the russian language.
        return phrase.text.length * 0.06 + 0.6
    }

    private fun playMediaIfExists() {
        playerHandler.removeCallbacksAndMessages(playerRunnable)
        try {
            soundsManager.pollSoundFile()?.let { item ->
                mediaPlayer.stop()
                mediaPlayer.reset()
                mediaPlayer.setDataSource(item.fileDescriptor)
                mediaPlayer.prepare()
                mediaPlayer.start()
                if (soundsManager.hasSoundFile()) {
                    playerHandler.postDelayed(playerRunnable, item.duration.toLong())
                }
            } ?: run {
                mediaPlayer.stop()
                mediaPlayer.reset()
                soundsManager.clear()
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }
}