package com.yandex.navikitdemo.api

import com.yandex.mapkit.annotations.AnnotationLanguage
import com.yandex.navikitdemo.impl.domain.models.AnnotatedEventsType
import com.yandex.navikitdemo.impl.domain.models.AnnotatedRoadEventsType

data class AnnotationsSettingsData(
    val annotationLanguage: AnnotationLanguage,
    val preRecordedAnnotations: Boolean,
    val textAnnotations: Boolean,
    val annotatedEventEnabled: Pair<AnnotatedEventsType, Boolean>? = null,
    val annotatedRoadEventEnabled: Pair<AnnotatedRoadEventsType, Boolean>? = null,
    val muteAnnotations: Boolean,

    )