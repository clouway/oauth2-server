package com.clouway.oauth2.util

import java.security.MessageDigest
import java.util.Base64

object Hashes {
    fun atHash(accessToken: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(accessToken.toByteArray())
        val half = digest.copyOfRange(0, digest.size / 2)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(half)
    }
}

