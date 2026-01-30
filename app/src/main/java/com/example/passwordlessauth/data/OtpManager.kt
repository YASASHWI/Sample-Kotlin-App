package com.example.passwordlessauth.data

import com.example.passwordlessauth.analytics.AnalyticsLogger
import kotlin.random.Random

data class OtpEntry(
    val code: String,
    val timestamp: Long,
    var attempts: Int = 0
)

sealed class OtpResult {
    object Success : OtpResult()
    object Incorrect : OtpResult()
    object Expired : OtpResult()
    object TooManyAttempts : OtpResult()
}

object OtpManager {
    private const val OTP_LENGTH = 6
    private const val EXPIRY_MS = 60_000L // 60 seconds
    private const val MAX_ATTEMPTS = 3

    private val otpStorage = mutableMapOf<String, OtpEntry>()

    fun generateOtp(email: String): String {
        // Generate 6 digit OTP
        val otp = List(OTP_LENGTH) { Random.nextInt(0, 10) }.joinToString("")
        
        // Store user OTP with current timestamp
        otpStorage[email] = OtpEntry(
            code = otp,
            timestamp = System.currentTimeMillis(),
            attempts = 0
        )
        
        AnalyticsLogger.logOtpGenerated(email)
        return otp
    }

    fun validateOtp(email: String, inputOtp: String): OtpResult {
        val entry = otpStorage[email] ?: return OtpResult.Incorrect

        // Check attempts first (Defensive)
        if (entry.attempts >= MAX_ATTEMPTS) {
            AnalyticsLogger.logOtpValidationFailure("too_many_attempts")
            return OtpResult.TooManyAttempts
        }

        // Check expiry
        val currentTime = System.currentTimeMillis()
        if (currentTime - entry.timestamp > EXPIRY_MS) {
            AnalyticsLogger.logOtpValidationFailure("expired")
            return OtpResult.Expired
        }

        // Check validity
        if (entry.code == inputOtp) {
            // Calculate time taken for success
            val duration = currentTime - entry.timestamp
            AnalyticsLogger.logOtpValidationSuccess(duration)
            otpStorage.remove(email) // Clear OTP after success
            return OtpResult.Success
        } else {
            entry.attempts++
            AnalyticsLogger.logOtpValidationFailure("incorrect")
            
            // If this made it hit the max attempts, we should probably signal "last attempt failed"
            // But the requirement says "Maximum validation attempts: 3".
            // So on the 3rd fail, next time they try it's TooManyAttempts.
            // Or should the 3rd fail ITSELF return TooManyAttempts? 
            // Usually, checking first is safer.
            
            return OtpResult.Incorrect
        }
    }
}
