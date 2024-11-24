package com.yandex.navikitdemo.data

import android.content.Context
import com.yandex.mapkit.annotations.AnnotationLanguage
import com.yandex.navikitdemo.domain.LocalLanguageProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalLanguageProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context,
): LocalLanguageProvider {
    private val data =
        MutableStateFlow(LocalLanguageData(context, AnnotationLanguage.RUSSIAN))

    override fun changes(): StateFlow<LocalLanguageData> = data.asStateFlow()
    override fun emitLanguage(language: AnnotationLanguage) {
        data.tryEmit(LocalLanguageData(context, language))
    }
}