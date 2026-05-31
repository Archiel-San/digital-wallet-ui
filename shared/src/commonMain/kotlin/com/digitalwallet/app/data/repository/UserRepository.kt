package com.digitalwallet.app.data.repository

import com.digitalwallet.app.data.local.TokenStorage
import com.digitalwallet.app.data.model.UserResponse
import com.digitalwallet.app.network.ApiClient

class UserRepository(
    private val tokenStorage: TokenStorage,
){
    suspend fun getMe(): Result<UserResponse> {
        return ApiClient.getWithAuth("/api/users/me", tokenStorage)
    }
}
