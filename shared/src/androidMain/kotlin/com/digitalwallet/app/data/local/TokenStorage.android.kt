package com.digitalwallet.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.digitalwallet.app.data.model.AuthTokens
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore("wallet_prefs")

actual class TokenStorage (private val context: Context) {

    private val ACCESS = stringPreferencesKey("access_token")
    private val REFRESH = stringPreferencesKey("refresh_token")
    private val EXPIRES = stringPreferencesKey("expires_in")

    actual suspend fun saveTokens(tokens: AuthTokens) {
        context.dataStore.edit {
            it[ACCESS] = tokens.accessToken
            it[REFRESH] = tokens.refreshToken
            it[EXPIRES] = tokens.expiresIn.toString()
        }
    }

    actual suspend fun getTokens(): AuthTokens? {
        val prefs = context.dataStore.data.first()
        val access = prefs[ACCESS] ?: return null
        val refresh = prefs[REFRESH] ?: return null
        val expires = prefs[EXPIRES]?.toLongOrNull() ?: return null

        return AuthTokens(access, refresh,expires)
    }

    actual suspend fun clearTokens() {
        context.dataStore.edit { it.clear() }
    }
}
