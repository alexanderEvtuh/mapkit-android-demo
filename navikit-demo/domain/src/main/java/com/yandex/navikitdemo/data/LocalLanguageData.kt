package com.yandex.navikitdemo.data

import android.content.Context
import android.content.res.AssetManager
import com.yandex.mapkit.annotations.AnnotationLanguage
import com.yandex.mapkit.annotations.SpeakerPhraseToken
import com.yandex.navikitdemo.domain.utils.path
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

class LocalLanguageData(
    context: Context,
    private val language: AnnotationLanguage,
) {
    private companion object {
        const val DEFAULT_SOUND_PATH = "sounds/default/%s/0.mp3"
        const val ENGLISH_SOUND_PATH = "sounds/en_male/%s/0.mp3"
        const val RUSSIAN_SOUND_PATH = "sounds/ru_female/%s/0.mp3"

        const val RUSSIAN_DURATION_PATH = "sounds/ru_female/durations.json"
        const val ENGLISH_DURATION_PATH = "sounds/en_male/durations.json"

        const val SOUND_NAME = "0.mp3"
    }

    private val assets: AssetManager by lazy { context.assets }

    private fun openInputStream(): InputStream? =
        try {
            when (language) {
                AnnotationLanguage.RUSSIAN -> assets.open(RUSSIAN_DURATION_PATH)
                AnnotationLanguage.ENGLISH -> assets.open(ENGLISH_DURATION_PATH)
                else -> null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }

    private fun InputStream.readJson(): String? =
        try {
            val size = available()
            val buffer = ByteArray(size)
            read(buffer)
            close()
            String(buffer, Charset.forName("UTF-8"))
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }

    fun getDurations(): Map<String, Double> {
        val soundDurations = mutableMapOf<String, Double>()
        val json = openInputStream()?.readJson() ?: return soundDurations
        try {
            val jsonObject = JSONObject(json)
            SpeakerPhraseToken.values().map { it.path }.forEach { key ->
                if (jsonObject.has(key)) {
                    jsonObject.getJSONObject(key).let { keyObject ->
                        if (keyObject.has(SOUND_NAME)) {
                            keyObject.getDouble(SOUND_NAME).takeIf { it > 0 }?.let { value ->
                                soundDurations[key] = value * 1000
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return soundDurations
    }

    fun getSoundPath(isSpeedLimit: Boolean = false): String =
        when {
            isSpeedLimit -> DEFAULT_SOUND_PATH
            language == AnnotationLanguage.ENGLISH -> ENGLISH_SOUND_PATH
            else -> RUSSIAN_SOUND_PATH
        }
}