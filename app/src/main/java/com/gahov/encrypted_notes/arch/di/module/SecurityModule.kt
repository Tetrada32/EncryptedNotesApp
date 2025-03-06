package com.gahov.encrypted_notes.arch.di.module

import com.gahov.encrypted_notes.data.security.key.SecretKeyProvider
import com.gahov.encrypted_notes.data.security.key.SecretKeyProviderImpl
import com.gahov.encrypted_notes.data.security.manager.CryptoManager
import com.gahov.encrypted_notes.data.security.manager.CryptoManagerImpl
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
    internal fun provideCryptoManager(
        keyProvider: SecretKeyProvider
    ): CryptoManager = CryptoManagerImpl(keyProvider)

    @Provides
    @Singleton
    internal fun provideSecretKey(): SecretKeyProvider = SecretKeyProviderImpl()
}