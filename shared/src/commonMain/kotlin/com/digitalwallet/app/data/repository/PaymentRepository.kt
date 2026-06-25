package com.digitalwallet.app.data.repository

import com.digitalwallet.app.data.local.TokenStorage
import com.digitalwallet.app.data.model.TransactionResponse
import com.digitalwallet.app.data.model.TransferRequest
import com.digitalwallet.app.data.model.UserResponse
import com.digitalwallet.app.network.ApiClient


class PaymentRepository(
    private val tokenStorage: TokenStorage,
) {

    suspend fun findUserByEmail(email: String): Result<UserResponse>{
        return ApiClient.getWithAuth(
            path = "/api/users/by-email?email=$email", tokenStorage = tokenStorage
        )
    }

    suspend fun transfer(
        receiverKeycloak: String,
        amount: Double,
        description: String?
    ):Result<TransactionResponse>{
        return ApiClient.postWithAuth(
            path = "/api/payments/transfer",
            body = TransferRequest(receiverKeycloak, amount, description),
            tokenStorage = tokenStorage
        )
    }

    suspend fun getHistory(): Result<List<TransactionResponse>>{
        return ApiClient.getWithAuth(
            path = "/api/payments/history",
            tokenStorage
        )
    }

}