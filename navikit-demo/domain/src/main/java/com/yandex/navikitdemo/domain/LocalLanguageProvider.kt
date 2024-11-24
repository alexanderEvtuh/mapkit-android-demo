package com.yandex.navikitdemo.domain

import com.yandex.mapkit.annotations.AnnotationLanguage
import com.yandex.navikitdemo.data.LocalLanguageData
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface LocalLanguageProvider {
    fun changes(): StateFlow<LocalLanguageData>
    fun emitLanguage(language: AnnotationLanguage)
}