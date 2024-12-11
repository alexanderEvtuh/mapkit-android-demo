package com.yandex.navikitdemo.domain.models

import com.yandex.mapkit.annotations.AnnotationLanguage

data class SettingsData(
    val annotationLanguage: AnnotationLanguage,
    val preRecordedAnnotations: Boolean,
    val textAnnotations: Boolean,
)
