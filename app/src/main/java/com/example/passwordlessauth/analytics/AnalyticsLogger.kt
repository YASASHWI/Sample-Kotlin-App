package com.example.passwordlessauth.analytics

import timber.log.Timber

object AnalyticsLogger {

    fun logOtpGenerated(email: String) {
        val domain = email.substringAfter('@')
        Timber.d("Event: otp_generated, email_domain: $domain")
    }

    fun logOtpValidationSuccess(attemptDurationMs: Long) {
        Timber.d("Event: otp_validation_success, attempt_duration_ms: $attemptDurationMs")
    }

    fun logOtpValidationFailure(reason: String) {
        Timber.d("Event: otp_validation_failure, reason: $reason")
    }

    fun logLogout(sessionDurationSeconds: Long) {
        Timber.d("Event: logout, session_duration_seconds: $sessionDurationSeconds")
    }
}
