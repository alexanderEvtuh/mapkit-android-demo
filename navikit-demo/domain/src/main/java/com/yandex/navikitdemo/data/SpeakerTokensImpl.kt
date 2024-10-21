package com.yandex.navikitdemo.data

import android.content.Context
import com.yandex.mapkit.annotations.AnnotationLanguage
import com.yandex.mapkit.annotations.SpeakerPhraseToken
import com.yandex.navikitdemo.domain.SettingsManager
import com.yandex.navikitdemo.domain.SpeakerTokensManager
import com.yandex.navikitdemo.domain.models.LocalPhrase
import com.yandex.navikitdemo.domain.utils.path
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.json.JSONObject
import java.nio.charset.Charset
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SpeakerTokensImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val settingsManager: SettingsManager,
) : SpeakerTokensManager {

    private companion object {
        const val OFFSET_EN = 280L
        const val OFFSET_RU = 0L
    }

    private val scope = MainScope()

    private val soundDurations = mutableMapOf<String, Double>()

    private val assets = context.assets

    init {
        settingsManager.annotationLanguage.changes()
            .onEach {
                updateDurations()
            }
            .launchIn(scope)
    }

    override fun getLocalPhrase(token: SpeakerPhraseToken, path: String): LocalPhrase =
        LocalPhrase(token, assets.openFd(path), soundDurations[token.path] ?: 0.0)

    fun updateDurations() {
        soundDurations.clear()
        val json = try {
            val inputStream = when (settingsManager.annotationLanguage.value) {
                AnnotationLanguage.RUSSIAN -> assets.open("sounds/ru_female/durations.json")
                AnnotationLanguage.ENGLISH -> assets.open("sounds/en_male/durations.json")
                else -> return
            }
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charset.forName("UTF-8"))
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }

        try {
            val jsonObject = JSONObject(json)
            SpeakerPhraseToken.values().map { it.path }.forEach { key ->
                if (jsonObject.has(key)) {
                    jsonObject.getJSONObject(key).let { keyObject ->
                        if (keyObject.has("0.mp3")) {
                            keyObject.getDouble("0.mp3").takeIf { it > 0 }?.let { value ->
                                soundDurations[key] =
                                    value * 1000 - when (settingsManager.annotationLanguage.value) {
                                        AnnotationLanguage.RUSSIAN -> OFFSET_RU
                                        AnnotationLanguage.ENGLISH -> OFFSET_EN
                                        else -> 0
                                    }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
    }
}