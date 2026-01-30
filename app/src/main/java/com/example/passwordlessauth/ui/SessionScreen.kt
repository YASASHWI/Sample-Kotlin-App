package com.example.passwordlessauth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SessionScreen(
    email: String,
    sessionStartTime: Long,
    onLogoutClick: () -> Unit
) {
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000)
        }
    }

    val durationSeconds = (currentTime - sessionStartTime) / 1000
    val minutes = durationSeconds / 60
    val seconds = durationSeconds % 60
    val formattedDuration = String.format("%02d:%02d", minutes, seconds)
    
    val date = Date(sessionStartTime)
    val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val formattedStartTime = formatter.format(date)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome, $email",
            style = MaterialTheme.typography.titleMedium
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
             Column(
                 modifier = Modifier.padding(16.dp),
                 horizontalAlignment = Alignment.CenterHorizontally
             ) {
                 Text(
                     text = "Session Active",
                     style = MaterialTheme.typography.labelLarge,
                     color = MaterialTheme.colorScheme.primary
                 )
                 Spacer(modifier = Modifier.height(8.dp))
                 Text(
                     text = formattedDuration,
                     style = MaterialTheme.typography.displayMedium,
                     fontWeight = FontWeight.Bold
                 )
                 Spacer(modifier = Modifier.height(8.dp))
                 Text(
                     text = "Started at $formattedStartTime",
                     style = MaterialTheme.typography.bodySmall
                 )
             }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onLogoutClick,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Logout")
        }
    }
}
