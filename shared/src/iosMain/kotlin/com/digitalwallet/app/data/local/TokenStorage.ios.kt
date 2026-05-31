package com.digitalwallet.app.data.local

import com.digitalwallet.app.data.model.AuthTokens
import platform.Foundation.NSUserDefaults

actual class TokenStorage() {

    private val defaults = NSUserDefaults.standardUserDefaults


    actual suspend fun saveTokens(tokens: AuthTokens) {
        defaults.setObject(tokens.accessToken, "access_token")
        defaults.setObject(tokens.refreshToken, "refresh_token")
        defaults.setObject(tokens.expiresIn.toString(), "expires_in")

    }

    actual suspend fun getTokens(): AuthTokens? {
        val access = defaults.stringForKey("access_token") ?: return null
        val refresh = defaults.stringForKey("refresh_token") ?: return null
        val expires =defaults.stringForKey("expires_in")?.toLong()?: return null

        return AuthTokens(access, refresh, expires)
    }

    actual suspend fun clearTokens() {
        defaults.removeObjectForKey("access_token")
        defaults.removeObjectForKey("refresh_token")
        defaults.removeObjectForKey("expires_in")
    }
}