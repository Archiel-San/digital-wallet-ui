package com.digitalwallet.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)

@Serializable
data class LoginRequest(
    val email:String,
    val password: String
)