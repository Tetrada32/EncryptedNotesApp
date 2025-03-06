package com.gahov.encrypted_notes.data.security.manager

import android.util.Base64
import com.gahov.encrypted_notes.data.security.key.SecretKeyProvider
import com.gahov.encrypted_notes.domain.common.Either
import com.gahov.encrypted_notes.domain.entities.Failure
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec

/**
 * A helper class that provides encryption and decryption methods using Android Keystore.
 *
 * The encryption method returns a single string that contains both the IV and the cipher text,
 * separated by a colon (:), after encoding them in Base64.
 */
class CryptoManagerImpl(
    private val keyProvider: SecretKeyProvider
): CryptoManager {

    companion object {
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val TAG_LENGTH = 128
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
            cipher.init(Cipher.ENCRYPT_MODE, keyProvider.getSecretKey())
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
            cipher.init(Cipher.DECRYPT_MODE, keyProvider.getSecretKey(), spec)
            val decryptedBytes = cipher.doFinal(cipherText)
            return Either.Right(String(decryptedBytes, Charsets.UTF_8))
        } catch (e: Exception) {
            return Either.Left(Failure.DataEncryptionException(e))
        }
    }
}

