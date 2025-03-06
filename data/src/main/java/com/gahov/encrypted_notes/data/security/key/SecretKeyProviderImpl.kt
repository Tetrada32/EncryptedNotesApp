package com.gahov.encrypted_notes.data.security.key

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class SecretKeyProviderImpl : SecretKeyProvider {

    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

    companion object {
        private const val ALGORITHM = "AES"
        private const val HARDCODED_SECRET_KEY = "12345678901234567890123456789012"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "notes_key"
        private const val KEY_SIZE = 256
    }

    init {
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            generateKey()
        }
    }

    /**
     * Retrieves the secret key from the Keystore, or returns hardcoded one.
     * @param isExportFriendly boolean param which allows user to import/export notes and
     * encrypt/decrypt them, but at the expense of reduced security.
     *
     * @return the SecretKey used for encryption and decryption.
     */
    override fun getSecretKey(isExportFriendly: Boolean): SecretKey {
        return if (isExportFriendly) {
            SecretKeySpec(HARDCODED_SECRET_KEY.toByteArray(Charsets.UTF_8), ALGORITHM)
        } else {
            (keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey
        }
    }

    /**
     * Generates an AES key and stores it in the Android Keystore.
     *
     * NOTE! This encryption method works better, as uses unique keys and stores it in [KeyStore],
     * but then importing/exporting makes no sense!
     */
    @Suppress("Unused")
    override fun generateKey() {
        val keyGenerator =
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(KEY_SIZE)
            .build()
        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }
}