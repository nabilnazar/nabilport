package com.nabilnazar.deck53biometricauth

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class MainActivity : FragmentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BiometricPromptSampleApp(this)
        }
    }
}

@Composable
fun BiometricPromptSampleApp(activity: MainActivity) {
    val context = LocalContext.current
    val biometricManager = BiometricManager.from(context)
    val authenticationStatus = remember { mutableStateOf("Not Authenticated") }

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Biometric Authentication")
        .setSubtitle("Authenticate using biometrics")
        .setDescription("Use your fingerprint or face to access the app")
        .setNegativeButtonText("Cancel")
        .build()

    val executor = ContextCompat.getMainExecutor(context)
    val biometricPrompt = BiometricPrompt(
        activity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                authenticationStatus.value = "Authentication Succeeded"
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                authenticationStatus.value = "Authentication Failed"
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                authenticationStatus.value = "Error: $errString"
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Status: ${authenticationStatus.value}")
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) ==
                    BiometricManager.BIOMETRIC_SUCCESS
                ) {
                    biometricPrompt.authenticate(promptInfo)
                } else {
                    authenticationStatus.value = "Biometric authentication not supported/enabled"
                }
            }
        ) {
            Text(text = "Authenticate")
        }
    }
}
