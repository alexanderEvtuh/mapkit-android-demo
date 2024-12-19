package com.yandex.navikitdemo.impl.domain

import com.yandex.mapkit.annotations.AnnotationLanguage
import kotlinx.coroutines.flow.StateFlow

internal interface LocalLanguageProvider {
    fun changes(): StateFlow<AnnotationLanguage>
    fun emitLanguage(language: AnnotationLanguage)
}