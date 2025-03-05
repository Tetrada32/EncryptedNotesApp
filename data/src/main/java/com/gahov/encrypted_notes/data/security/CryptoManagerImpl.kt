package com.gahov.encrypted_notes.data.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.gahov.encrypted_notes.domain.common.Either
import com.gahov.encrypted_notes.domain.entities.Failure
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * A helper class that provides encryption and decryption methods using Android Keystore.
 *
 * The encryption method returns a single string that contains both the IV and the cipher text,
 * separated by a colon (:), after encoding them in Base64.
 */
class CryptoManagerImpl : CryptoManager {

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val KEY_ALIAS = "notes_key"
        private const val KEY_SIZE = 256
        private const val TAG_LENGTH = 128
    }

    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

    init {
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            generateKey()
        }
    }


    /**
     * Generates an AES key and stores it in the Android Keystore.
     */
    private fun generateKey() {
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

    /**
     * Retrieves the secret key from the Keystore.
     *
     * @return the SecretKey used for encryption and decryption.
     */
    private fun getSecretKey(): SecretKey {
        return (keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey
    }

    /**
     * Encrypts the provided plain text and returns a single string containing the IV and
     * cipher text, separated by a colon. Both IV and cipher text are encoded in Base64.
     *
     * @param plainText The text to encrypt.
     * @return A string in the format "iv:cipherText".
     */
    override fun encryptToString(plainText: String?): Either<Failure, String> {
        try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
            val iv = cipher.iv
            val cipherText = cipher.doFinal(plainText?.toByteArray(Charsets.UTF_8))

            // Encode IV and cipherText in Base64 and join with colon as delimiter:
            val ivBase64 = Base64.encodeToString(iv, Base64.NO_WRAP)
            val cipherTextBase64 = Base64.encodeToString(cipherText, Base64.NO_WRAP)
            return Either.Right("$ivBase64:$cipherTextBase64")
        } catch (e: Exception) {
            return Either.Left(Failure.DataEncryptionException(e))
        }
    }

    /**
     * Decrypts a string containing the IV and cipher text (separated by a colon) and returns the original plain text.
     *
     * @param encryptedString A string in the format "iv:cipherText".
     * @return The decrypted plain text.
     * @throws IllegalArgumentException if the provided string does not match the expected format.
     */
    override fun decryptFromString(encryptedString: String?): Either<Failure, String> {
        try {
            val parts = encryptedString.toString().split(":")
            if (parts.size != 2) {
                throw IllegalArgumentException("Wrong string format! ")
            }
            val iv = Base64.decode(parts[0], Base64.NO_WRAP)
            val cipherText = Base64.decode(parts[1], Base64.NO_WRAP)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)
            val decryptedBytes = cipher.doFinal(cipherText)
            return Either.Right(String(decryptedBytes, Charsets.UTF_8))
        } catch (e: Exception) {
            return Either.Left(Failure.DataEncryptionException(e))
        }
    }
}

