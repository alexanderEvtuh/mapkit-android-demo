package com.yandex.navikitdemo.api

import com.yandex.mapkit.navigation.automotive.Navigation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

data class AnnotationDependencies(
    val annotatorFlow: StateFlow<Navigation>,
    val annotationsSettingsFlow: Flow<AnnotationsSettingsData>,
)
