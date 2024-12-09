package com.yandex.navikitdemo.data

import android.content.res.AssetManager
import com.yandex.mapkit.annotations.AnnotationLanguage
import com.yandex.mapkit.annotations.SpeakerPhraseToken
import com.yandex.navikitdemo.domain.LocalLanguageProvider
import com.yandex.navikitdemo.domain.utils.path
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class LocalLanguageProviderImpl @Inject constructor(
    @Named("languageFlow") private val data: MutableStateFlow<AnnotationLanguage>,
) : LocalLanguageProvider {
    override fun changes(): StateFlow<AnnotationLanguage> = data.asStateFlow()
    override fun emitLanguage(language: AnnotationLanguage) {
        data.tryEmit(language)
    }
}

const val ENGLISH_SOUND_PATH = "sounds/en_male/"
const val RUSSIAN_SOUND_PATH = "sounds/ru_female/"

const val RUSSIAN_DURATION_PATH = "sounds/ru_female/"
const val ENGLISH_DURATION_PATH = "sounds/en_male/"

const val SOUND_NAME = "0.mp3"

private fun AnnotationLanguage.openInputStream(assets: AssetManager): InputStream? =
    try {
        when (this) {
            AnnotationLanguage.RUSSIAN -> assets.open(RUSSIAN_DURATION_PATH.plus("durations.json"))
            AnnotationLanguage.ENGLISH -> assets.open(ENGLISH_DURATION_PATH.plus("durations.json"))
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

fun AnnotationLanguage.getDurations(assets: AssetManager): Map<String, Double> {
    val soundDurations = mutableMapOf<String, Double>()
    val json = openInputStream(assets)?.readJson() ?: return soundDurations
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

fun AnnotationLanguage.getSoundPath(): String =
    when {
        this == AnnotationLanguage.ENGLISH -> ENGLISH_SOUND_PATH.plus("%s/0.mp3")
        else -> RUSSIAN_SOUND_PATH.plus("%s/0.mp3")
    }