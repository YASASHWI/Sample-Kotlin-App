package com.example.passwordlessauth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passwordlessauth.analytics.AnalyticsLogger
import com.example.passwordlessauth.data.OtpManager
import com.example.passwordlessauth.data.OtpResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<AuthState>(AuthState.LoggedOut)
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    fun onEmailEntered(email: String) {
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // In a real app we might show an error before sending, 
            // but here we just proceed to generate OTP if valid-ish or stick to logic.
            // Let's assume input validation happens in UI or we return error here.
            // For simplicity, let's just generate if not blank.
            return
        }
        
        val otp = OtpManager.generateOtp(email)
        // For debugging purposes, since we don't have a backend to email it:
        println("DEBUG: OTP for $email is $otp") 
        
        _uiState.value = AuthState.OtpSent(email = email)
    }

    fun onResendOtp(email: String) {
        val otp = OtpManager.generateOtp(email)
        println("DEBUG: Resend OTP for $email is $otp")
        _uiState.value = AuthState.OtpSent(email = email, error = "OTP Resent")
    }

    fun onVerifyOtp(email: String, code: String) {
        if (code.length != 6) {
             _uiState.value = AuthState.OtpSent(email, error = "OTP must be 6 digits")
             return
        }

        when (val result = OtpManager.validateOtp(email, code)) {
            is OtpResult.Success -> {
                _uiState.value = AuthState.LoggedIn(
                    email = email,
                    sessionStartTime = System.currentTimeMillis()
                )
            }
            is OtpResult.Incorrect -> {
                _uiState.value = AuthState.OtpSent(email, error = "Incorrect OTP")
            }
            is OtpResult.Expired -> {
                _uiState.value = AuthState.OtpSent(email, error = "OTP Expired. Please resend.")
            }
            is OtpResult.TooManyAttempts -> {
                _uiState.value = AuthState.OtpSent(email, error = "Too many attempts. Please resend.")
            }
        }
    }

    fun onLogout() {
        val currentState = _uiState.value
        if (currentState is AuthState.LoggedIn) {
            val duration = (System.currentTimeMillis() - currentState.sessionStartTime) / 1000
            AnalyticsLogger.logLogout(duration)
        }
        _uiState.value = AuthState.LoggedOut
    }
}
