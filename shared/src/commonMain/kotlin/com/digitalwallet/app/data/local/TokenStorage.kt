package com.digitalwallet.app.data.local

import com.digitalwallet.app.data.model.AuthTokens
import kotlinx.serialization.Serializable

// expect/actual pattern — each platform implements storage differently
expect class TokenStorage {
    suspend fun saveTokens(tokens: AuthTokens)
    suspend fun getTokens(): AuthTokens?
    suspend fun clearTokens()
}