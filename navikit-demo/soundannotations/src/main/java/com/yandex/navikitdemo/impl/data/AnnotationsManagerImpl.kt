package com.yandex.navikitdemo.impl.data

import android.util.Log
import com.yandex.mapkit.annotations.AnnotationLanguage
import com.yandex.mapkit.annotations.Speaker
import com.yandex.mapkit.navigation.automotive.Annotator
import com.yandex.mapkit.navigation.automotive.AnnotatorListener
import com.yandex.navikitdemo.api.AnnotationDependencies
import com.yandex.navikitdemo.api.AnnotationsManager
import com.yandex.navikitdemo.impl.domain.models.AnnotatedEventsType
import com.yandex.navikitdemo.impl.domain.models.AnnotatedRoadEventsType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.plus

internal class AnnotationsManagerImpl(
    private val ttsSpeaker: Speaker,
    private val localSpeaker: Speaker,
    private val ttsToastSpeaker: Speaker,
    private val localToastSpeaker: Speaker,
    private val annotationDependencies: AnnotationDependencies,
) : AnnotationsManager {

    private val scope = MainScope() + Dispatchers.Main.immediate
    private var annotator: Annotator = annotationDependencies.annotatorFlow.value.guidance.annotator

    private val annotatorListener = object : AnnotatorListener {
        override fun manoeuvreAnnotated() {
            Log.d(TAG, "manoeuvreAnnotated")
        }

        override fun roadEventAnnotated() {
            Log.d(TAG, "roadEventAnnotated")
        }

        override fun speedingAnnotated() {
            Log.d(TAG, "speedingAnnotated")
        }

        override fun fasterAlternativeAnnotated() {
            Log.d(TAG, "fasterAlternativeAnnotated")
        }
    }

    init {
        val annotatorFlow = annotationDependencies.annotatorFlow.map { it.guidance.annotator }

        val speakerFlow = annotationDependencies.annotationsSettingsFlow.map {
            it.annotatedEventEnabled?.let { setAnnotatedEventEnabled(it.first, it.second) }
            it.annotatedRoadEventEnabled?.let { setAnnotatedRoadEventEnabled(it.first, it.second) }
            setAnnotationsEnabled(it.muteAnnotations)
            return@map (if (it.annotationLanguage in listOf(
                    AnnotationLanguage.RUSSIAN,
                    AnnotationLanguage.ENGLISH
                ) && it.preRecordedAnnotations
            ) {
                if (it.textAnnotations) localToastSpeaker else localSpeaker
            } else {
                if (it.textAnnotations) ttsToastSpeaker else ttsSpeaker
            })
        }

        combine(speakerFlow, annotatorFlow) { speaker, otherAnnotator ->
            annotator.apply {
                removeListener(annotatorListener)
                setSpeaker(null)
            }
            if (annotator != otherAnnotator) {
                annotator = otherAnnotator
            }
            annotator.apply {
                setSpeaker(speaker)
                addListener(annotatorListener)
            }
        }
            .launchIn(scope)
    }

    override fun setAnnotationsEnabled(isEnabled: Boolean) {
        with(annotator) {
            if (isEnabled) unmute() else mute()
        }
    }

    override fun setAnnotatedEventEnabled(event: AnnotatedEventsType, isEnabled: Boolean) {
        annotator.annotatedEvents =
            applyEventAvailabilityToMask(
                event.mapkitEnum.value,
                isEnabled,
                annotator.annotatedEvents
            )
    }

    override fun setAnnotatedRoadEventEnabled(event: AnnotatedRoadEventsType, isEnabled: Boolean) {
        annotator.annotatedRoadEvents =
            applyEventAvailabilityToMask(
                event.mapkitEnum.value,
                isEnabled,
                annotator.annotatedRoadEvents
            )
    }

    private fun applyEventAvailabilityToMask(event: Int, isEnabled: Boolean, mask: Int): Int {
        return if (isEnabled) mask or event else mask and event.inv()
    }

    private companion object {
        const val TAG = "AnnotationsManagerImpl"
    }
}
