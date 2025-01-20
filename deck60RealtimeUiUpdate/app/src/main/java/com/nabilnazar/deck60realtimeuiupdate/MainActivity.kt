package com.nabilnazar.deck60realtimeuiupdate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase
import com.nabilnazar.deck60realtimeuiupdate.ui.theme.Deck60RealtimeUiUpdateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        val database = FirebaseDatabase.getInstance()
        val statusRef = database.getReference("order_status")

        setContent {
            Deck60RealtimeUiUpdateTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var status by remember { mutableStateOf("Loading...") }

                    // Listen for Firebase database changes
                    LaunchedEffect(Unit) {
                        statusRef.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
                            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                                status = snapshot.getValue(String::class.java) ?: "Unknown"
                            }

                            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                                status = "Error: ${error.message}"
                            }
                        })
                    }

                    StatusScreen(status = status)
                }
            }
        }
    }
}

@Composable
fun StatusScreen(status: String) {
    val backgroundColor = when (status.lowercase()) {
        "waiting" -> Color.Red
        "preparing" -> Color.Yellow
        "done" -> Color.Green
        else -> MaterialTheme.colorScheme.surface // Default background color from theme
    }

    val textColor = when (backgroundColor) {
        Color.Red, Color.Green, Color.Yellow -> MaterialTheme.colorScheme.onPrimary // Contrast text for colored backgrounds
        else -> MaterialTheme.colorScheme.onSurface // Text color for theme's surface
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = androidx.compose.ui.Alignment.Center // Align content to center
    ) {
        Text(
            text = status,
            color = textColor,
            textAlign = TextAlign.Center,
            fontSize = 24.sp
        )
    }
}


