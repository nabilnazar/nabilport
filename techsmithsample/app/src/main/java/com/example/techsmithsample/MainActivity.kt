package com.example.techsmithsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.techsmithsample.presentation.ui.ContactScreen
import com.example.techsmithsample.presentation.ui.theme.TechSmithSampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TechSmithSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val names = mapOf(Pair("dsdsds","76868686"), Pair("nabil","9388283324"),Pair("ponnum","7034193748"),Pair("husain","9090909"),)

                        ContactScreen(names)

                }
            }
        }
    }
}
