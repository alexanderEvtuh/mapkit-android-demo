package com.yandex.navikitdemo.di

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import com.yandex.mapkit.annotations.AnnotationLanguage
import com.yandex.mapkit.annotations.Speaker
import com.yandex.navikitdemo.data.AnnotationsManagerImpl
import com.yandex.navikitdemo.data.LocalLanguageProviderImpl
import com.yandex.navikitdemo.data.LocalSpeaker
import com.yandex.navikitdemo.data.LocationManagerImpl
import com.yandex.navikitdemo.data.NavigationHolderImpl
import com.yandex.navikitdemo.data.NavigationManagerImpl
import com.yandex.navikitdemo.data.NavigationStyleManagerImpl
import com.yandex.navikitdemo.data.PlayerManagerImpl
import com.yandex.navikitdemo.data.RequestPointsManagerImpl
import com.yandex.navikitdemo.data.SettingsManagerImpl
import com.yandex.navikitdemo.data.SimulationManagerImpl
import com.yandex.navikitdemo.data.SoundsManagerImpl
import com.yandex.navikitdemo.data.SpeakerTokensImpl
import com.yandex.navikitdemo.data.ToastSpeaker
import com.yandex.navikitdemo.data.TtsSpeaker
import com.yandex.navikitdemo.data.VehicleOptionsManagerImpl
import com.yandex.navikitdemo.data.helpers.BackgroundServiceManagerImpl
import com.yandex.navikitdemo.data.helpers.KeyValueStorageImpl
import com.yandex.navikitdemo.data.helpers.NavigationDeserializerImpl
import com.yandex.navikitdemo.data.helpers.NavigationFactoryImpl
import com.yandex.navikitdemo.data.helpers.NavigationSuspenderManagerImpl
import com.yandex.navikitdemo.data.mappers.PhraseToSpeakerTokensMapperImpl
import com.yandex.navikitdemo.domain.AnnotationsManager
import com.yandex.navikitdemo.domain.LocalLanguageProvider
import com.yandex.navikitdemo.domain.LocationManager
import com.yandex.navikitdemo.domain.NavigationHolder
import com.yandex.navikitdemo.domain.NavigationManager
import com.yandex.navikitdemo.domain.NavigationStyleManager
import com.yandex.navikitdemo.domain.PlayerManager
import com.yandex.navikitdemo.domain.RequestPointsManager
import com.yandex.navikitdemo.domain.SettingsManager
import com.yandex.navikitdemo.domain.SimulationManager
import com.yandex.navikitdemo.domain.SoundsManager
import com.yandex.navikitdemo.domain.SpeakerTokensManager
import com.yandex.navikitdemo.domain.VehicleOptionsManager
import com.yandex.navikitdemo.domain.helpers.BackgroundServiceManager
import com.yandex.navikitdemo.domain.helpers.KeyValueStorage
import com.yandex.navikitdemo.domain.helpers.NavigationDeserializer
import com.yandex.navikitdemo.domain.helpers.NavigationFactory
import com.yandex.navikitdemo.domain.helpers.NavigationSuspenderManager
import com.yandex.navikitdemo.domain.mappers.PhraseToSpeakerTokensMapper
import com.yandex.navikitdemo.domain.models.SettingsData
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun navigationStyleManager(impl: NavigationStyleManagerImpl): NavigationStyleManager

    @Binds
    abstract fun backgroundServiceManager(impl: BackgroundServiceManagerImpl): BackgroundServiceManager

    @Binds
    abstract fun keyValueStorage(impl: KeyValueStorageImpl): KeyValueStorage

    @Binds
    abstract fun locationManager(impl: LocationManagerImpl): LocationManager

    @Binds
    abstract fun navigationManager(impl: NavigationManagerImpl): NavigationManager

    @Binds
    abstract fun requestPointsManager(impl: RequestPointsManagerImpl): RequestPointsManager

    @Binds
    abstract fun settingsManager(impl: SettingsManagerImpl): SettingsManager

    @Binds
    abstract fun simulationManager(impl: SimulationManagerImpl): SimulationManager

    @Binds
    abstract fun vehicleOptionsManager(impl: VehicleOptionsManagerImpl): VehicleOptionsManager

    @Binds
    abstract fun navigationFactory(impl: NavigationFactoryImpl): NavigationFactory

    @Binds
    abstract fun navigationSuspenderManger(impl: NavigationSuspenderManagerImpl): NavigationSuspenderManager

    @Binds
    abstract fun navigationDeserializer(impl: NavigationDeserializerImpl): NavigationDeserializer

    @Binds
    abstract fun navigationHolder(impl: NavigationHolderImpl): NavigationHolder

    @Named("ttsSpeaker")
    @Binds
    abstract fun speakerManager(impl: TtsSpeaker): Speaker

    @Named("localSpeaker")
    @Binds
    abstract fun localSpeakerManager(impl: LocalSpeaker): Speaker

    @Binds
    abstract fun annotationsManager(impl: AnnotationsManagerImpl): AnnotationsManager

    @Binds
    abstract fun soundsManager(impl: SoundsManagerImpl): SoundsManager

    @Binds
    abstract fun phraseToSpeakerTokensMapper(impl: PhraseToSpeakerTokensMapperImpl): PhraseToSpeakerTokensMapper

    @Binds
    abstract fun speakerTokens(impl: SpeakerTokensImpl): SpeakerTokensManager

    @Binds
    abstract fun playerManager(impl: PlayerManagerImpl): PlayerManager

    @Binds
    abstract fun localLanguageProvider(impl: LocalLanguageProviderImpl): LocalLanguageProvider

    companion object {
        @Singleton
        @Provides
        fun notificationManager(
            application: Application,
        ): NotificationManager {
            return application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

        @Singleton
        @Provides
        @Named("languageFlow")
        fun languageFlow(): MutableStateFlow<AnnotationLanguage> =
            MutableStateFlow(AnnotationLanguage.RUSSIAN)

        @Singleton
        @Provides
        @Named("settingFlow")
        fun settingFlow(settingsManager: SettingsManager): MutableStateFlow<SettingsData> =
            MutableStateFlow(
                SettingsData(
                    settingsManager.annotationLanguage.value,
                    settingsManager.preRecordedAnnotations.value,
                    settingsManager.textAnnotations.value
                )
            )

        @Singleton
        @Provides
        @Named("toastLocalSpeaker")
        fun toastLocalSpeaker(
            @ApplicationContext context: Context,
            @Named("localSpeaker") localSpeaker: Speaker,
        ): Speaker = ToastSpeaker(context, localSpeaker)

        @Singleton
        @Provides
        @Named("toastTtsSpeaker")
        fun toastTtsSpeaker(
            @ApplicationContext context: Context,
            @Named("ttsSpeaker") ttsSpeaker: Speaker,
        ): Speaker = ToastSpeaker(context, ttsSpeaker)
    }
}
