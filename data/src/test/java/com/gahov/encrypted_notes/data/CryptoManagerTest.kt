package com.gahov.encrypted_notes.data

import android.util.Base64
import com.gahov.encrypted_notes.data.security.key.SecretKeyProvider
import com.gahov.encrypted_notes.data.security.manager.CryptoManagerImpl
import com.gahov.encrypted_notes.domain.common.Either
import com.gahov.encrypted_notes.domain.entities.Failure
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import javax.crypto.spec.SecretKeySpec

class CryptoManagerTest {

    private lateinit var cryptoManager: CryptoManagerImpl
    private lateinit var keyProvider: SecretKeyProvider

    @Before
    fun setup() {
        keyProvider = mockk()
        cryptoManager = CryptoManagerImpl(keyProvider)

        val key = SecretKeySpec(HARDCODED_SECRET_KEY.toByteArray(Charsets.UTF_8), "AES")
        every { keyProvider.getSecretKey() } returns key

        mockkStatic(Base64::class)
        val arraySlot = slot<ByteArray>()

        /**
         * Android unit tests are not capable of executing methods from android.util.Base64,
         * so the java.util.Base64 ones must be used instead.
         *
         * @see: https://github.com/mockk/mockk/issues/418
         */
        every {
            Base64.encodeToString(capture(arraySlot), Base64.NO_WRAP)
        } answers {
            java.util.Base64.getEncoder().encodeToString(arraySlot.captured)
        }

        val stringSlot = slot<String>()
        every {
            Base64.decode(capture(stringSlot), Base64.NO_WRAP)
        } answers {
            java.util.Base64.getDecoder().decode(stringSlot.captured)
        }
    }

    @Test
    fun `encrypt To String should work`() {
        // Arrange
        val message = "Hello, World!"

        // Act: Encrypt the message.
        val encryptResult = cryptoManager.encryptToString(message)

        // Assert encryption succeeded and the output contains a colon separator.
        assertTrue("Encryption should succeed", encryptResult is Either.Right)

        val encryptedString = (encryptResult as Either.Right).success
        assertTrue("Encrypted string should contain colon", encryptedString.contains(":"))

        // Act: Decrypt the encrypted string.
        val decryptResult = cryptoManager.decryptFromString(encryptedString)

        // Assert decryption succeeded and returns the original message.
        assertTrue("Decryption should succeed", decryptResult is Either.Right)
        val decryptedMessage = (decryptResult as Either.Right).success
        assertEquals(message, decryptedMessage)
    }

    @Test
    fun `decryptFromString should fail for invalid format`() {
        // Arrange: Create an invalid encrypted string (missing colon separator).
        val invalidEncryptedString = "invalidStringWithoutColon"

        // Act: Attempt to decrypt the invalid string.
        val decryptResult = cryptoManager.decryptFromString(invalidEncryptedString)

        // Assert: The decryption should fail.
        assertTrue("Decryption should fail for invalid input", decryptResult is Either.Left)
        val error = (decryptResult as Either.Left).failure
        assertTrue(
            "Error should be a DataEncryptionException",
            error is Failure.DataEncryptionException
        )
    }

    @Test
    fun `decryptFromString should fail when input is null`() {
        // Act: Decrypt a null string.
        val decryptResult = cryptoManager.decryptFromString(null)

        // Assert: The decryption should fail.
        assertTrue("Decryption should fail for null input", decryptResult is Either.Left)
        val error = (decryptResult as Either.Left).failure
        assertTrue(
            "Error should be a DataEncryptionException",
            error is Failure.DataEncryptionException
        )
    }

    companion object {
        private const val HARDCODED_SECRET_KEY = "12345678901234567890123456789012"
    }
}
