package com.example.linternapro.core.di

import android.content.Context
import com.example.linternapro.core.preferences.preferencesManager
import com.example.linternapro.presenter.viewmodels.TorchManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object storeProvider {

    @Provides
    @Singleton
    fun provideSharedPrefManager(@ApplicationContext context:Context): preferencesManager {
        return preferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideTorchManager(@ApplicationContext context:Context): TorchManager {
        return TorchManager(context)
    }
}