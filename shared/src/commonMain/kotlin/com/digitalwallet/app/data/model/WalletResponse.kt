package com.digitalwallet.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class WalletResponse(
    val id: Long = 0,
    val keycloakId: String = "",
    val status: String = "ACTIVE",
    val balance: Double = 0.0
)

@Serializable
data class LedgerEntry(
    val id: Long = 0,
    val type: String = "DEBIT",
    val amount: Double = 0.0,
    val balanceAfter: Double = 0.0,
    val description: String? = null,
    val createdAt: String = ""
)
