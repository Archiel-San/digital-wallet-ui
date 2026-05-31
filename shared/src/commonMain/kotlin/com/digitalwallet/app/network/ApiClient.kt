package com.digitalwallet.app.network

import com.digitalwallet.app.data.local.TokenStorage
import com.digitalwallet.app.data.model.AuthTokens
import digitalwalletapp.shared.generated.resources.Res
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object ApiClient {

    // Android emulator → 10.0.2.2 reaches your Mac's localhost
    // iOS simulator   → localhost works directly
    //const val BASE_URL = "http://10.0.2.2:8085"

    const val BASE_URL =  "https://2bcf-2605-59ca-86bc-6e08-5109-add4-4224-b9b4.ngrok-free.app"
    val client = HttpClient {
        install(ContentNegotiation){
            json(Json{ignoreUnknownKeys= true})
        }
        install(Logging){
            level = LogLevel.BODY
        }
    }

    suspend inline fun <reified T> getWithAuth(
        path: String,
        tokenStorage: TokenStorage,
    ): Result<T> {
        return try {
            val token = tokenStorage.getTokens()?.accessToken
                ?: return Result.failure(Exception("Not logged in"))

            var response = client.get("$BASE_URL$path") {
                bearerAuth(token)
            }

            if(response.status == HttpStatusCode.Unauthorized){
                val refreshed = refreshTokens(tokenStorage)
                if (refreshed == null){
                    tokenStorage.clearTokens()
                    return Result.failure(Exception("Session expired"))
                }

                response = client.get("$BASE_URL$path") {
                    bearerAuth(refreshed.accessToken)
                }
            }

            if (!response.status.isSuccess()) {
                return Result.failure(Exception("Request failed: ${response.status}"))
            }

            Result.success(response.body())
        }
        catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun refreshTokens(tokenStorage: TokenStorage): AuthTokens? {
        return try {
            val current = tokenStorage.getTokens()?: return null

            val tokens = client.post("$BASE_URL/api/auth/refresh"){
                contentType(ContentType.Application.Json)
                setBody(mapOf("refreshToken" to current.refreshToken))
            }.body<AuthTokens>()

            tokenStorage.saveTokens(tokens)
            tokens
        } catch (e: Exception){
            null
        }
    }
}
