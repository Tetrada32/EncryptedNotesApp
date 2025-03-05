package com.gahov.encrypted_notes.data.security

import com.gahov.encrypted_notes.domain.common.Either
import com.gahov.encrypted_notes.domain.entities.Failure

interface CryptoManager {

    fun encryptToString(plainText: String?): Either<Failure, String>

    fun decryptFromString(encryptedString: String?): Either<Failure, String>
}