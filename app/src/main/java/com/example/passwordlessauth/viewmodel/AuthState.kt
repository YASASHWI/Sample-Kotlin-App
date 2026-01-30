package com.example.passwordlessauth.viewmodel

sealed class AuthState {
    object LoggedOut : AuthState()
    
    data class OtpSent(
        val email: String,
        val error: String? = null
    ) : AuthState()
    
    data class LoggedIn(
        val email: String,
        val sessionStartTime: Long
    ) : AuthState()
}
