package com.damiandantas.daylighthabits.common.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DispatcherDefault

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DispatcherIO

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {
    @DispatcherDefault
    @Provides
    fun provideDispatcherDefault(): CoroutineDispatcher = Dispatchers.Default

    @DispatcherIO
    @Provides
    fun provideDispatcherIO(): CoroutineDispatcher = Dispatchers.IO
}