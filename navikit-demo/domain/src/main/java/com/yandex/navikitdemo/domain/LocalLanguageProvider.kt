package com.yandex.navikitdemo.domain

import com.yandex.mapkit.annotations.AnnotationLanguage
import kotlinx.coroutines.flow.StateFlow

interface LocalLanguageProvider {
    fun changes(): StateFlow<AnnotationLanguage>
    fun emitLanguage(language: AnnotationLanguage)
}