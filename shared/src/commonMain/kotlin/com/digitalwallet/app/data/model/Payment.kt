package com.digitalwallet.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TransferRequest(
    val receiverKeycloakId: String,
    val amount: Double,
    val description: String?
){}

@Serializable
data class TransactionResponse(
    val id: Long,
    val senderKeycloakId: String,
    val receiverKeycloakId: String,
    val amount: Double,
    val description: String?,
    val status: String,
    val failureReason: String? = null,
    val createdAt: String
)
{}