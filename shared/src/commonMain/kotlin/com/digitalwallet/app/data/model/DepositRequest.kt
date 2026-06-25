package com.digitalwallet.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DepositRequest(
    val amount: Double,
    val description: String? = null
)
