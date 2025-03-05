package com.gahov.encrypted_notes.arch.di.module

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module(
    includes = [
        MapperModule::class,
        RepositoryModule::class,
        SourceModule::class,
        DatabaseModule::class
    ]
)
@InstallIn(SingletonComponent::class)
class AppModule