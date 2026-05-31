package com.digitalwallet.app.data.repository

import com.digitalwallet.app.data.local.TokenStorage
import com.digitalwallet.app.data.model.AuthTokens
import com.digitalwallet.app.data.model.LoginRequest
import com.digitalwallet.app.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthRepository(private val tokenStorage: TokenStorage) {

    private val client = ApiClient.client
    private val base = ApiClient.BASE_URL

    // ── Login ────────────────────────────────────────────────────────
    suspend fun login (email: String, password: String): Result<AuthTokens>{
        return  try {
            val tokens = client.post ( "$base/api/auth/login" ){
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email,password))
            }.body<AuthTokens>()

            tokenStorage.saveTokens(tokens)
            Result.success(tokens)
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    // ── Refresh ──────────────────────────────────────────────────────
    // Called automatically when a request returns 401
    suspend fun refresh ():Result<AuthTokens>{
        val tokens = ApiClient.refreshTokens(tokenStorage)
            ?: return Result.failure(Exception("Session expired"))

        return Result.success(tokens)
    }

    // ── Logout ───────────────────────────────────────────────────────
    suspend fun logout() {
        tokenStorage.clearTokens()
    }

    // ── Check if logged in ───────────────────────────────────────────
    suspend fun isLoggedIn(): Boolean {
        return tokenStorage.getTokens() != null
    }
}
