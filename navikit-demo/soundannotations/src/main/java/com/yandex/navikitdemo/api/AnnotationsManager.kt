package com.yandex.navikitdemo.api

import android.content.Context
import android.util.Log
import com.yandex.mapkit.annotations.AnnotationLanguage
import com.yandex.navikitdemo.impl.data.AnnotationsManagerImpl
import com.yandex.navikitdemo.impl.data.LocalLanguageProviderImpl
import com.yandex.navikitdemo.impl.data.LocalSpeaker
import com.yandex.navikitdemo.impl.data.PlayerManagerImpl
import com.yandex.navikitdemo.impl.data.SoundsManagerImpl
import com.yandex.navikitdemo.impl.data.SpeakerTokensImpl
import com.yandex.navikitdemo.impl.data.ToastSpeaker
import com.yandex.navikitdemo.impl.data.TtsSpeaker
import com.yandex.navikitdemo.impl.data.mappers.PhraseToSpeakerTokensMapperImpl
import com.yandex.navikitdemo.impl.domain.models.AnnotatedEventsType
import com.yandex.navikitdemo.impl.domain.models.AnnotatedRoadEventsType
import kotlinx.coroutines.flow.MutableStateFlow

interface AnnotationsManager {
    companion object {
        fun factoryMethod(
            context: Context,
            annotationLanguage: MutableStateFlow<AnnotationLanguage>,
            annotationDependencies: AnnotationDependencies,
        ): AnnotationsManager {
            val localLanguageProvider = LocalLanguageProviderImpl(annotationLanguage)
            val speakerTokenManager = SpeakerTokensImpl(context, localLanguageProvider)
            val phraseToSpeakerTokensMapper = PhraseToSpeakerTokensMapperImpl(
                localLanguageProvider, speakerTokenManager
            )
            val soundsManager = SoundsManagerImpl(phraseToSpeakerTokensMapper)
            val playerManager = PlayerManagerImpl(context)
            val ttsSpeaker = TtsSpeaker(context, localLanguageProvider)
            val ttsToastSpeaker = ToastSpeaker(context, ttsSpeaker)
            val localSpeaker = LocalSpeaker(soundsManager, playerManager)
            val localToastSpeaker = ToastSpeaker(context, localSpeaker)
            Log.e("Test", "!!!!!")
            return AnnotationsManagerImpl(
                ttsSpeaker,
                localSpeaker,
                ttsToastSpeaker,
                localToastSpeaker,
                annotationDependencies,
            )
        }
    }

    fun setAnnotationsEnabled(isEnabled: Boolean)

    fun setAnnotatedEventEnabled(event: AnnotatedEventsType, isEnabled: Boolean)
    fun setAnnotatedRoadEventEnabled(event: AnnotatedRoadEventsType, isEnabled: Boolean)
}
