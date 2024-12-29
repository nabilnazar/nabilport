package com.nabilnazar.deck45iosalertdialog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nabilnazar.deck45iosalertdialog.ui.theme.Deck45iosalertdialogTheme
import com.x3rocode.xblurlib.BlurDialog
import com.x3rocode.xblurlib.xBlur

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Deck45iosalertdialogTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        Content()
                    }
                }
            }
        }
    }

    @Composable
    fun DialogContent(onClose: () -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Blur Dialog",
                fontSize = 20.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 10.dp)
            )
            Text(
                text = "I think this is so cool!",
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 20.dp)
            )
            TextButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 10.dp)
            ) {
                Text(
                    text = "Close",
                    fontSize = 20.sp,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    @Composable
    fun Content() {
        var openDialog by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Button(
                    onClick = { openDialog = true },
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .xBlur(context = LocalContext.current)
                ) {
                    Text(text = "Open Dialog")
                }

            }

            if (openDialog) {
                BlurDialog(
                    content = { DialogContent { openDialog = false } },
                    blurRadius = 50,
                    onDismiss = { openDialog = false },
                    size = IntOffset(280, 160),
                    shape = RoundedCornerShape(30.dp),
                    backgroundColor = Color.White,
                    backgroundColorAlpha = 0.5f,
                    isRealtime = false,
                    dialogDimAmount = 0.3f
                )
            }
        }
    }
}
