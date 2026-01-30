package com.example.passwordlessauth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.passwordlessauth.ui.LoginScreen
import com.example.passwordlessauth.ui.OtpScreen
import com.example.passwordlessauth.ui.SessionScreen
import com.example.passwordlessauth.viewmodel.AuthState
import com.example.passwordlessauth.viewmodel.AuthViewModel
import timber.log.Timber

class MainActivity : ComponentActivity() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Log an Analytics event
        Timber.d("Main Activity Opened")

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val uiState by viewModel.uiState.collectAsState()

                    when (val state = uiState) {
                        is AuthState.LoggedOut -> {
                            LoginScreen(
                                onSendOtpClick = { email ->
                                    viewModel.onEmailEntered(email)
                                }
                            )
                        }

                        is AuthState.OtpSent -> {
                            OtpScreen(
                                email = state.email,
                                error = state.error,
                                onVerifyClick = { email, code ->
                                    viewModel.onVerifyOtp(email, code)
                                },
                                onResendClick = { email ->
                                    viewModel.onResendOtp(email)
                                }
                            )
                        }

                        is AuthState.LoggedIn -> {
                            SessionScreen(
                                email = state.email,
                                sessionStartTime = state.sessionStartTime,
                                onLogoutClick = {
                                    viewModel.onLogout()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
