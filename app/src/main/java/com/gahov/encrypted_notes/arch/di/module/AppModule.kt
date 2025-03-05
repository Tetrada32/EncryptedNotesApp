package com.gahov.encrypted_notes.arch.di.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module(
    includes = [
        MapperModule::class,
        RepositoryModule::class,
        SourceModule::class,
        DatabaseModule::class,
        SecurityModule::class
    ]
)
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    internal fun provideApplicationContext(application: Application): Context {
        return application.applicationContext
    }

}