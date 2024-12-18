package com.nabilnazar.videocallingapp

import android.graphics.drawable.Icon
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import io.getstream.video.android.compose.permission.LaunchCallPermissions
import io.getstream.video.android.compose.theme.StreamColors
import io.getstream.video.android.compose.theme.StreamDimens
import io.getstream.video.android.compose.theme.StreamShapes
import io.getstream.video.android.compose.theme.StreamTypography
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.compose.theme.VideoTheme.colors
import io.getstream.video.android.compose.theme.VideoTheme.dimens
import io.getstream.video.android.compose.ui.components.call.CallAppBar
import io.getstream.video.android.compose.ui.components.call.activecall.CallContent
import io.getstream.video.android.compose.ui.components.call.controls.ControlActions
import io.getstream.video.android.compose.ui.components.call.controls.actions.FlipCameraAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.LeaveCallAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.ToggleCameraAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.ToggleMicrophoneAction
import io.getstream.video.android.compose.ui.components.call.renderer.FloatingParticipantVideo
import io.getstream.video.android.compose.ui.components.call.renderer.ParticipantVideo
import io.getstream.video.android.compose.ui.components.video.VideoRenderer
import io.getstream.video.android.core.GEO
import io.getstream.video.android.core.RealtimeConnection
import io.getstream.video.android.core.StreamVideoBuilder
import io.getstream.video.android.core.call.state.CallAction
import io.getstream.video.android.core.call.state.CustomAction
import io.getstream.video.android.model.User
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiKey = "mmhfdzb5evj2"
        val userToken =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL3Byb250by5nZXRzdHJlYW0uaW8iLCJzdWIiOiJ1c2VyL0FheWxhX1NlY3VyYSIsInVzZXJfaWQiOiJBYXlsYV9TZWN1cmEiLCJ2YWxpZGl0eV9pbl9zZWNvbmRzIjo2MDQ4MDAsImlhdCI6MTczNDQyOTIyNywiZXhwIjoxNzM1MDM0MDI3fQ.8d1Ij7RBystyu52tvo4BCcUjWQjm6EHgSXjngaFwl4g"
        val userId = "Aayla_Secura"
        val callId = "RNE9VZcfgREP"

        // Create a user.
        val user = User(
            id = userId, // any string
            name = "Tutorial", // name and image are used in the UI
            image = "https://bit.ly/2TIt8NR",
        )

        // Initialize StreamVideo. For a production app, add the client to your Application class or di module.
        val client = StreamVideoBuilder(
            context = applicationContext,
            apiKey = apiKey,
            geo = GEO.GlobalEdgeNetwork,
            user = user,
            token = userToken,
        ).build()

        setContent {
            // Request permissions and join a call, which type is `default` and  
            val call = client.call(type = "default", id = callId)
            LaunchCallPermissions(
                call = call,
                onAllPermissionsGranted = {
                    // All permissions are granted so that we can join the call.
                    val result = call.join(create = true)
                    result.onError {
                        Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
                    }
                }
            )

            // Apply VideoTheme
            VideoTheme {
                val isCameraEnabled by call.camera.isEnabled.collectAsState()
                val isMicrophoneEnabled by call.microphone.isEnabled.collectAsState()
                var isCallEnded by remember { mutableStateOf(false) } // Track call status

                if (isCallEnded) {
                    CallEnded() // Show CallEnded Composable
                } else {
                    CallContent(
                        appBarContent = {
                            CallAppBar(
                                call = call,
                                trailingContent = { }
                            )
                        },
                        modifier = Modifier.background(color = Color.White),
                        call = call,
                        controlsContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                contentAlignment = Alignment.Center
                            ) {
                                ControlActions(
                                    call = call,
                                    actions = listOf(
                                        {
                                            ToggleCameraAction(
                                                modifier = Modifier.size(52.dp),
                                                isCameraEnabled = isCameraEnabled,
                                                onCallAction = { call.camera.setEnabled(it.isEnabled) }
                                            )
                                        },
                                        {
                                            ToggleMicrophoneAction(
                                                modifier = Modifier.size(52.dp),
                                                isMicrophoneEnabled = isMicrophoneEnabled,
                                                onCallAction = { call.microphone.setEnabled(it.isEnabled) }
                                            )
                                        },
                                        {
                                            FlipCameraAction(
                                                modifier = Modifier.size(52.dp),
                                                onCallAction = { call.camera.flip() }
                                            )
                                        },
                                        {
                                            LeaveCallAction(
                                                modifier = Modifier.size(52.dp),
                                                onCallAction = {
                                                    call.leave() // Leave the call
                                                    isCallEnded =
                                                        true // Set the state to show CallEnded screen
                                                }
                                            )
                                        },
                                    )
                                )
                            }
                        }
                    )
                }
            }
        }

    }
}

@Composable
fun CallEnded() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Call Ended",
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}
