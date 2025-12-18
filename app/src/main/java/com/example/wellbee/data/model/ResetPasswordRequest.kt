package com.example.wellbee.data.model

data class ResetPasswordRequest(
    val email: String,
    val newPassword: String
)