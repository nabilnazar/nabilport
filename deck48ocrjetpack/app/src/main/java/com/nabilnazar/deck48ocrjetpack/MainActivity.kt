package com.nabilnazar.deck48ocrjetpack

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.nabilnazar.deck48ocrjetpack.ui.theme.Deck48ocrjetpackTheme
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()

        setContent {
            Deck48ocrjetpackTheme {
                OCRApp()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}

@Composable
fun OCRApp() {
    var recognizedText by remember { mutableStateOf("") }
    var isScanning by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraWithOCR(
            isScanning = isScanning,
            onTextRecognized = { text ->
                recognizedText = text
            }
        )
        // Overlay for recognized text and controls
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Recognized text display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = recognizedText,
                    style = androidx.compose.ui.text.TextStyle(
                        color = Color.White,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }
            // Control button
            Button(
                onClick = { isScanning = !isScanning },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(if (isScanning) "Pause Scanning" else "Resume Scanning")
            }
        }
    }
}

@Composable
fun CameraWithOCR(isScanning: Boolean, onTextRecognized: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var previewView: PreviewView? by remember { mutableStateOf(null) }

    // Use rememberUpdatedState to update the analyzer when isScanning changes
    val imageAnalyzer by rememberUpdatedState(
        newValue = createAnalyzer(isScanning, onTextRecognized)
    )

    // DisposableEffect ensures that the camera is unbind when the composable leaves the composition
    DisposableEffect(cameraProviderFuture, isScanning) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build()
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        val analysisUseCase = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        try {
            cameraProvider.unbindAll()
            analysisUseCase.setAnalyzer(ContextCompat.getMainExecutor(context), imageAnalyzer)
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                analysisUseCase
            )
            preview.surfaceProvider = previewView?.surfaceProvider
        } catch (e: Exception) {
            Log.e("CameraWithOCR", "Use case binding failed", e)
        }

        onDispose {
            cameraProvider.unbindAll()
        }
    }

    AndroidView(
        factory = { context ->
            PreviewView(context).also { previewView = it }
        },
        modifier = Modifier.fillMaxSize()
    )
}

private fun createAnalyzer(
    isScanning: Boolean,
    onTextRecognized: (String) -> Unit
): ImageAnalysis.Analyzer {
    return ImageAnalysis.Analyzer { imageProxy ->
        if (isScanning) {
            processImageProxy(imageProxy, onTextRecognized)
        } else {
            imageProxy.close() // Release the frame when scanning is paused
        }
    }
}

@OptIn(ExperimentalGetImage::class)
private fun processImageProxy(imageProxy: ImageProxy, onTextRecognized: (String) -> Unit) {
    val mediaImage = imageProxy.image ?: return
    val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    recognizer.process(inputImage)
        .addOnSuccessListener { visionText ->
            onTextRecognized(visionText.text)
        }
        .addOnFailureListener { e ->
            Log.e("OCR", "Text recognition failed", e)
        }
        .addOnCompleteListener {
            imageProxy.close()
        }
}