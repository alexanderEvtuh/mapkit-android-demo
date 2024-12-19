package com.yandex.navikitdemo.di

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import com.yandex.mapkit.annotations.AnnotationLanguage
import com.yandex.navikitdemo.api.AnnotationDependencies
import com.yandex.navikitdemo.api.AnnotationsManager
import com.yandex.navikitdemo.api.AnnotationsSettingsData
import com.yandex.navikitdemo.data.LocationManagerImpl
import com.yandex.navikitdemo.data.NavigationHolderImpl
import com.yandex.navikitdemo.data.NavigationManagerImpl
import com.yandex.navikitdemo.data.NavigationStyleManagerImpl
import com.yandex.navikitdemo.data.RequestPointsManagerImpl
import com.yandex.navikitdemo.data.SettingsManagerImpl
import com.yandex.navikitdemo.data.SimulationManagerImpl
import com.yandex.navikitdemo.data.VehicleOptionsManagerImpl
import com.yandex.navikitdemo.data.helpers.BackgroundServiceManagerImpl
import com.yandex.navikitdemo.data.helpers.KeyValueStorageImpl
import com.yandex.navikitdemo.data.helpers.NavigationDeserializerImpl
import com.yandex.navikitdemo.data.helpers.NavigationFactoryImpl
import com.yandex.navikitdemo.data.helpers.NavigationSuspenderManagerImpl
import com.yandex.navikitdemo.domain.LocationManager
import com.yandex.navikitdemo.domain.NavigationHolder
import com.yandex.navikitdemo.domain.NavigationManager
import com.yandex.navikitdemo.domain.NavigationStyleManager
import com.yandex.navikitdemo.domain.RequestPointsManager
import com.yandex.navikitdemo.domain.SettingsManager
import com.yandex.navikitdemo.domain.SimulationManager
import com.yandex.navikitdemo.domain.VehicleOptionsManager
import com.yandex.navikitdemo.domain.helpers.BackgroundServiceManager
import com.yandex.navikitdemo.domain.helpers.KeyValueStorage
import com.yandex.navikitdemo.domain.helpers.NavigationDeserializer
import com.yandex.navikitdemo.domain.helpers.NavigationFactory
import com.yandex.navikitdemo.domain.helpers.NavigationSuspenderManager
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
        @Named("annotationsSettingsData")
        fun annotationsSettingsData(settingsManager: SettingsManager): MutableStateFlow<AnnotationsSettingsData> =
            MutableStateFlow(
                AnnotationsSettingsData(
                    settingsManager.annotationLanguage.value,
                    settingsManager.preRecordedAnnotations.value,
                    settingsManager.textAnnotations.value,
                    muteAnnotations = settingsManager.muteAnnotations.value
                )
            )

        @Singleton
        @Provides
        fun annotationsManager(
            @ApplicationContext context: Context,
            @Named("languageFlow") languageFlow: MutableStateFlow<AnnotationLanguage>,
            navigationHolder: NavigationHolder,
            @Named("annotationsSettingsData") annotationsSettingsData: MutableStateFlow<AnnotationsSettingsData>,
        ): AnnotationsManager = AnnotationsManager.factoryMethod(
            context,
            languageFlow,
            AnnotationDependencies(
                navigationHolder.navigation,
                annotationsSettingsData
            )
        )
    }
}
