package com.digitalwallet.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Long,
    val keycloakId: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String? = null
)
