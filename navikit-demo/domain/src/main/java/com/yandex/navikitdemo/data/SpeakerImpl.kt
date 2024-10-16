package com.yandex.navikitdemo.data

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import com.yandex.mapkit.annotations.AnnotationLanguage
import com.yandex.mapkit.annotations.LocalizedPhrase
import com.yandex.navikitdemo.domain.SettingsManager
import com.yandex.navikitdemo.domain.SoundsManager
import com.yandex.navikitdemo.domain.SpeakerManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeakerImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val settingsManager: SettingsManager,
    private val soundsManager: SoundsManager,
) : SpeakerManager {

    private val scope = MainScope()

    private var ttsInitialized = false
    private val tts = TextToSpeech(context) { status ->
        if (status == TextToSpeech.SUCCESS) {
            ttsInitialized = true
            updateTtsLanguage()
        }
    }
    private val phrasesImpl = MutableSharedFlow<String>()

    private val mediaPlayer = MediaPlayer()

    private val playerRunnable = Runnable {
        playMediaIfExists()
    }

    private val playerHandler = Handler(Looper.myLooper() ?: Looper.getMainLooper())

    init {
        settingsManager.annotationLanguage.changes()
            .onEach {
                updateTtsLanguage()
            }
            .launchIn(scope)
    }

    override fun phrases(): Flow<String> = phrasesImpl

    override fun reset() {
        tts.stop()
        mediaPlayer.reset()
    }

    override fun say(phrase: LocalizedPhrase) {
        if (soundsManager.needUsePreRecorded() && soundsManager.initPhrase(phrase)) {
            playMediaIfExists()
        } else {
            tts.speak(phrase.text, TextToSpeech.QUEUE_FLUSH, null, UUID.randomUUID().toString())
        }

        scope.launch {
            phrasesImpl.emit(phrase.text)
        }
    }

    override fun duration(phrase: LocalizedPhrase): Double {
        // Heuristic formula for the russian language.
        return phrase.text.length * 0.06 + 0.6
    }

    private fun updateTtsLanguage() {
        val language = settingsManager.annotationLanguage.value
        tts.language = language.toLocale()
    }

    private fun playMediaIfExists() {
        playerHandler.removeCallbacksAndMessages(playerRunnable)
        try {
            soundsManager.pollSoundFile()?.let { item ->
                mediaPlayer.stop()
                mediaPlayer.reset()
                mediaPlayer.setDataSource(item.second)
                mediaPlayer.prepare()
                mediaPlayer.start()
                if (soundsManager.hasSoundFile()) {
                    val durationInMillis = soundsManager.getNextPlayDelay(item.first)
                    playerHandler.postDelayed(playerRunnable, durationInMillis)
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

    private fun AnnotationLanguage.toLocale(): Locale {
        return when (this) {
            AnnotationLanguage.RUSSIAN -> Locale("ru", "RU")
            AnnotationLanguage.ENGLISH -> Locale("en", "US")
            AnnotationLanguage.ITALIAN -> Locale("it", "IT")
            AnnotationLanguage.FRENCH -> Locale("fr", "FR")
            AnnotationLanguage.TURKISH -> Locale("tr", "TR")
            AnnotationLanguage.UKRAINIAN -> Locale("uk", "UA")
            AnnotationLanguage.HEBREW -> Locale("he", "IL")
            AnnotationLanguage.SERBIAN -> Locale("sr-Latn", "RS")
            AnnotationLanguage.LATVIAN -> Locale("lv", "LV")
            AnnotationLanguage.FINNISH -> Locale("fi", "FI")
            AnnotationLanguage.ROMANIAN -> Locale("ro", "RO")
            AnnotationLanguage.KYRGYZ -> Locale("ky", "KG")
            AnnotationLanguage.KAZAKH -> Locale("kk", "KZ")
            AnnotationLanguage.LITHUANIAN -> Locale("lt", "LT")
            AnnotationLanguage.ESTONIAN -> Locale("et", "EE")
            AnnotationLanguage.GEORGIAN -> Locale("ka", "GE")
            AnnotationLanguage.UZBEK -> Locale("uz", "UZ")
            AnnotationLanguage.ARMENIAN -> Locale("hy", "AM")
            AnnotationLanguage.AZERBAIJANI -> Locale("az", "AZ")
            AnnotationLanguage.ARABIC -> Locale("ar", "AE")
            AnnotationLanguage.TATAR -> Locale("tt", "RU")
            AnnotationLanguage.PORTUGUESE -> Locale("pt", "PT")
            AnnotationLanguage.LATIN_AMERICAN_SPANISH -> Locale("es-419", "BO")
            AnnotationLanguage.BASHKIR -> Locale("ba", "RU")
        }
    }
}
