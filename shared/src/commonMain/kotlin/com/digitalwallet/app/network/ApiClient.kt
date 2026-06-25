package com.digitalwallet.app.network

import com.digitalwallet.app.data.local.TokenStorage
import com.digitalwallet.app.data.model.AuthTokens
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

    const val BASE_URL =  "https://04f3-2605-59ca-86bc-6e08-c078-e84c-ecc6-8934.ngrok-free.app"
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

    suspend inline fun< reified T> postWithAuth(
        path: String,
        body: Any,
        tokenStorage: TokenStorage
    ): Result<T>{

        return try {
            val token = tokenStorage.getTokens()?.accessToken
                ?: return Result.failure(Exception("Not Logged In"))

            var response = client.post("$BASE_URL$path"){
                bearerAuth(token)
                contentType(ContentType.Application.Json)
                setBody(body)
            }

            //TODO analisar why it gives me 401 when transfering when it should referesh automatically
            // todo 0 descobri que ele bate 2x no transfer ao inves de ser uma vez no transfer (dar 401) depois no refresh
            if(response.status == HttpStatusCode.Unauthorized){
                val refreshed = refreshTokens(tokenStorage)
                if (refreshed == null){
                    tokenStorage.clearTokens()
                    return Result.failure(Exception("Session Expired"))
                }

                response = client.post("$BASE_URL$path") {
                    bearerAuth(refreshed.accessToken)
                    contentType(ContentType.Application.Json)
                    setBody(body)
                }
            }

            if (!response.status.isSuccess()){
                return Result.failure(Exception("Request Failed ${response.status}"))
            }

            Result.success(response.body())

        }catch (e: Exception){
            Result.failure(e)
        }
        
    }



    // TODO ver aqui o pq dele estar a bater duas vezes no mesmo endpoint e negar da segunda vez, apesar de ja ter token valido
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
