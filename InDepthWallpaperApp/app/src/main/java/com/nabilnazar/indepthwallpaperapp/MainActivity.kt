package com.nabilnazar.indepthwallpaperapp

import android.app.WallpaperManager
import android.graphics.*
import android.graphics.BitmapShader
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import coil.request.ImageRequest
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.nabilnazar.indepthwallpaperapp.ui.theme.InDepthWallpaperAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.div
import kotlin.text.format
import kotlin.text.toFloat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var imageUri by remember { mutableStateOf<Uri?>(null) }
            var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
            val launcher =
                rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                    imageUri = uri
                }

            InDepthWallpaperAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.fillMaxSize()) {
                        Button(onClick = { launcher.launch("image/*") }) {
                            Text("Select Image")
                        }

                        Button(onClick = {
                            if (imageBitmap != null) {
                                startUpdatingWallpaper(imageBitmap!!)
                            }
                        }) {
                            Text("Set as Wallpaper")
                        }

                        LaunchedEffect(imageUri) {
                            if (imageUri != null) {
                                val request = ImageRequest.Builder(this@MainActivity)
                                    .data(imageUri)
                                    .build()
                                val drawable =
                                    coil.ImageLoader(this@MainActivity).execute(request).drawable
                                imageBitmap = drawable?.toBitmap()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startUpdatingWallpaper(imageBitmap: Bitmap) {
        lifecycleScope.launch {
            while (true) {
                val updatedBitmap = detectFacesAndDrawClock(imageBitmap)
                setLockScreenWallpaper(updatedBitmap)
                delay(1000) // Update every second
            }
        }
    }

    private suspend fun setLockScreenWallpaper(bitmap: Bitmap) {
        val wallpaperManager = WallpaperManager.getInstance(this)
        withContext(Dispatchers.IO) {
            wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
        }
    }

    private suspend fun detectFacesAndDrawClock(originalBitmap: Bitmap): Bitmap =
        withContext(Dispatchers.Default) {
            val faceDetector = FaceDetection.getClient()
            val inputImage = InputImage.fromBitmap(originalBitmap, 0)
            val faces = faceDetector.process(inputImage).await()

            // Create a mutable bitmap to work on
            val mutableBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
            val canvas = Canvas(mutableBitmap)

            // 1. Blur the entire image first
            val blurPaint = Paint().apply {
                maskFilter = BlurMaskFilter(20f, BlurMaskFilter.Blur.NORMAL)
            }
            canvas.drawBitmap(originalBitmap.copy(Bitmap.Config.ARGB_8888, true), 0f, 0f, blurPaint)

            // 2. Draw the clock behind the face
            val clockPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.GREEN
                textSize = 200f
                typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
            }

            for (face in faces) {
                val bounds = face.boundingBox

                // Draw the clock text behind the face
                val clockX = bounds.centerX().toFloat()
                val clockY = (bounds.centerY() - bounds.height() /3).toFloat()
                val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                canvas.drawText(time, clockX, clockY, clockPaint)
            }

            // 3. Draw the face regions normally without masking or clearing
            for (face in faces) {
                val bounds = face.boundingBox
                val facePaint = Paint(Paint.ANTI_ALIAS_FLAG)

                // Draw the face region (we won't use masking here, just paint the face region)
                val faceRegion = Bitmap.createBitmap(
                    originalBitmap,
                    bounds.left.coerceAtLeast(0),
                    bounds.top.coerceAtLeast(0),
                    bounds.width().coerceAtMost(originalBitmap.width - bounds.left),
                    bounds.height().coerceAtMost(originalBitmap.height - bounds.top)
                )
                canvas.drawBitmap(faceRegion, bounds.left.toFloat(), bounds.top.toFloat(), facePaint)
            }

            // Return the modified bitmap
            mutableBitmap
        }







}
