package com.gahov.encrypted_notes.data

import com.gahov.encrypted_notes.data.security.key.SecretKeyProvider
import com.gahov.encrypted_notes.data.security.manager.CryptoManagerImpl
import com.gahov.encrypted_notes.domain.common.Either
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import javax.crypto.spec.SecretKeySpec

@OptIn(ExperimentalCoroutinesApi::class)
class CryptoManagerTest {

    private lateinit var cryptoManager: CryptoManagerImpl
    private lateinit var keyProvider: SecretKeyProvider

    @Before
    fun setup() {
        // Create a fake key provider using mockk.
        keyProvider = mockk()
        val key = SecretKeySpec(HARDCODED_SECRET_KEY.toByteArray(Charsets.UTF_8), "AES")
        every { keyProvider.getSecretKey() } returns key

        // Instantiate the real CryptoManagerImpl with the fake key provider.
        cryptoManager = CryptoManagerImpl(keyProvider)
    }

    @Test
    fun `encryptToString and decryptFromString return original message`() = runTest {
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

    companion object {
        private const val HARDCODED_SECRET_KEY = "12345678901234567890123456789012"
    }
}
