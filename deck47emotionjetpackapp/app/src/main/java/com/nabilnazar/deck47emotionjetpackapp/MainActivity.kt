package com.nabilnazar.deck47emotionjetpackapp

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory

import android.graphics.YuvImage
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.nabilnazar.deck47emotionjetpackapp.ui.theme.Deck47emotionjetpackappTheme
 import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
    private lateinit var tflite: Interpreter
    private var lastProcessedTime: Long = 0 // Tracks last frame processing time
    private val frameIntervalMs = 1000 // Minimum interval between frame analyses

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        tflite = loadModel("model.tflite")

        logModelDetails()

        if (isCameraPermissionGranted()) {
            startCamera()
        } else {
            requestCameraPermission()
        }
    }

    private fun loadModel(modelName: String): Interpreter {
        val model = assets.open(modelName).use { inputStream ->
            val modelBytes = inputStream.readBytes()
            ByteBuffer.allocateDirect(modelBytes.size).apply {
                order(ByteOrder.nativeOrder())
                put(modelBytes)
                rewind()
            }
        }
        return Interpreter(model)
    }

    private fun logModelDetails() {
        val inputTensor = tflite.getInputTensor(0)
        val outputTensor = tflite.getOutputTensor(0)

        Log.d("ModelInput", "Shape: ${inputTensor.shape().contentToString()}")
        Log.d("ModelInput", "DataType: ${inputTensor.dataType()}")
        Log.d("ModelOutput", "Shape: ${outputTensor.shape().contentToString()}")
        Log.d("ModelOutput", "DataType: ${outputTensor.dataType()}")
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_CODE_PERMISSIONS
        )
    }

    private fun startCamera() {
        setContent {
            Deck47emotionjetpackappTheme {
                val emotion = remember { mutableStateOf("") }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        CameraPreviewWithOverlay(
                            modifier = Modifier.fillMaxSize(),
                            onFrameAnalyzed = { imageProxy ->
                                processFrame(imageProxy, emotion)
                            },
                            emotion = emotion
                        )
                    }
                }
            }
        }
    }

    private fun processFrame(imageProxy: ImageProxy, emotion: MutableState<String>) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastProcessedTime >= frameIntervalMs) {
            lastProcessedTime = currentTime
            try {
                if (!imageProxy.isClosed()) {
                    val bitmap = imageProxy.toBitmap()
                    emotion.value = analyzeEmotion(bitmap)
                }
            } catch (e: Exception) {
                Log.e("FrameProcessing", "Error: ${e.message}", e)
            } finally {
                imageProxy.close()
            }
        } else {
            imageProxy.close() // Close unprocessed frames to free resources
            Log.d("FrameProcessing", "Skipped frame to maintain interval")
        }
    }

    private fun analyzeEmotion(bitmap: Bitmap): String {
        val inputBuffer = preprocessBitmap(bitmap)
        val output = Array(1) { FloatArray(7) }
        tflite.run(inputBuffer, output)

        val emotionLabels = listOf("Anger", "Contempt", "Disgust", "Fear", "Happy", "Sadness", "Surprise")
        val maxIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
        val maxConfidence = if (maxIndex != -1) output[0][maxIndex] else 0f

        Log.d("EmotionAnalysis", "MaxIndex: $maxIndex, MaxConfidence: $maxConfidence")
        if (maxIndex != -1) {
            Log.d("EmotionAnalysis", "Emotion Label: ${emotionLabels[maxIndex]}")
        } else {
            Log.d("EmotionAnalysis", "No clear emotion detected")
        }

        return if (maxIndex != -1 && maxConfidence >= 0.2f) {
            emotionLabels[maxIndex]
        } else {
            "No clear emotion detected"
        }
    }

    private fun preprocessBitmap(bitmap: Bitmap): ByteBuffer {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 64, 64, true)
        val buffer = ByteBuffer.allocateDirect(64 * 64 * 3 * 4).apply { order(ByteOrder.nativeOrder()) }
        for (y in 0 until 64) {
            for (x in 0 until 64) {
                val pixel = resizedBitmap.getPixel(x, y)
                buffer.putFloat((pixel shr 16 and 0xFF) / 255.0f)
                buffer.putFloat((pixel shr 8 and 0xFF) / 255.0f)
                buffer.putFloat((pixel and 0xFF) / 255.0f)
            }
        }
        buffer.rewind()
        return buffer
    }

    @OptIn(ExperimentalGetImage::class)
    private fun ImageProxy.toBitmap(): Bitmap {
        val image = image ?: return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

        val planes = image.planes
        val yBuffer = planes[0].buffer
        val uBuffer = planes[1].buffer
        val vBuffer = planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(
            nv21,
            android.graphics.ImageFormat.NV21,
            image.width,
            image.height,
            null
        )

        val out = java.io.ByteArrayOutputStream()
        yuvImage.compressToJpeg(android.graphics.Rect(0, 0, image.width, image.height), 100, out)
        val jpegBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.size)
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}

@Composable
fun CameraPreviewWithOverlay(
    modifier: Modifier,
    onFrameAnalyzed: (ImageProxy) -> Unit,
    emotion: MutableState<String>
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var previewView: PreviewView? by remember { mutableStateOf(null) }

    Box(modifier = modifier) {
        AndroidView(
            factory = { ctx -> PreviewView(ctx).apply { previewView = this } },
            modifier = Modifier.fillMaxSize()
        )

        Box(
        modifier = Modifier
        .align(Alignment.Center)
        .background(Color.Black.copy(alpha = 0.5f)) // Apply background to Box
        ) {
    Text(
        text = emotion.value.ifEmpty { "Face not detected" },
        modifier = Modifier.padding(8.dp), // Add padding for better visibility
        color = Color.White,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    )
}
    }

    LaunchedEffect(cameraProviderFuture) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build()
        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .apply {
                setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                    onFrameAnalyzed(imageProxy)
                }
            }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalyzer
            )
            preview.surfaceProvider = previewView?.surfaceProvider
        } catch (exc: Exception) {
            Log.e("CameraPreview", "Use case binding failed", exc)
        }
    }
}

fun ImageProxy.isClosed(): Boolean {
    return try {
        planes[0].buffer
        false
    } catch (e: IllegalStateException) {
        true
    }
}
