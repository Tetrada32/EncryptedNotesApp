package com.gahov.encrypted_notes.arch.di.module

import com.gahov.encrypted_notes.data.security.CryptoManager
import com.gahov.encrypted_notes.data.security.CryptoManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SecurityModule {

    @Provides
    @Singleton
    internal fun provideCryptoManager(): CryptoManager = CryptoManagerImpl()
}