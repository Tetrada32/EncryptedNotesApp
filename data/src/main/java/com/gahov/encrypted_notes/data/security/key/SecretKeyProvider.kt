package com.gahov.encrypted_notes.data.security.key

import javax.crypto.SecretKey

interface SecretKeyProvider {

    fun getSecretKey(isExportFriendly: Boolean = true): SecretKey

    fun generateKey()

}