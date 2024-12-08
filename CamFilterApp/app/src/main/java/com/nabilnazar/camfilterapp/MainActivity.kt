package com.nabilnazar.camfilterapp

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ColorMatrix
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.nabilnazar.camfilterapp.ui.theme.CamFilterAppTheme
import java.io.File


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CamFilterAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        CameraApp(LocalContext.current)
                }
            }
        }
    }
}


@Composable
fun CameraPreview(modifier: Modifier = Modifier, onCapture: (Bitmap) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context) }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    ) { view ->
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also { it.setSurfaceProvider(view.surfaceProvider) }
            val imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)

            // Capture button functionality
            onCaptureRequest(imageCapture, context, onCapture)
        }, ContextCompat.getMainExecutor(context))
    }
}

fun onCaptureRequest(imageCapture: ImageCapture, context: Context, onCapture: (Bitmap) -> Unit) {
    val outputDirectory = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "CameraApp")
    if (!outputDirectory.exists()) outputDirectory.mkdirs()
    val photoFile = File(outputDirectory, "IMG_${System.currentTimeMillis()}.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                onCapture(bitmap)
            }

            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(context, "Capture failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    )
}

@Composable
fun FilterRow(onFilterSelected: (ColorMatrix?) -> Unit) {
    val filters = listOf(
        null, // Original
        createGrayscaleMatrix(), // Grayscale
        createSepiaMatrix() //sepia
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(filters) { filter ->
            FilterButton(filter = filter) { onFilterSelected(filter) }
        }
    }
}

@Composable
fun FilterButton(filter: ColorMatrix?, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .background(MaterialTheme.colorScheme.primary, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (filter == null) "Original" else "Filter",
            color = Color.White,
            style = MaterialTheme.typography.labelSmall
        )
    }
}


fun createGrayscaleMatrix(): ColorMatrix {
    val grayscaleMatrix = floatArrayOf(
        0.33f, 0.33f, 0.33f, 0f, 0f,
        0.33f, 0.33f, 0.33f, 0f, 0f,
        0.33f, 0.33f, 0.33f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )
    return ColorMatrix(grayscaleMatrix)
}

fun createSepiaMatrix(): ColorMatrix {
    val sepiaMatrix = floatArrayOf(
        0.393f, 0.769f, 0.189f, 0f, 0f,
        0.349f, 0.686f, 0.168f, 0f, 0f,
        0.272f, 0.534f, 0.131f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )
    return ColorMatrix(sepiaMatrix)
}


@Composable
fun CameraApp(current: Context) {
    // State for the selected filter and captured image
    var selectedFilter: ColorMatrix? by remember { mutableStateOf(null) }
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Camera preview section
        Box(modifier = Modifier.weight(1f)) {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                onCapture = { bitmap -> capturedImage = bitmap }
            )

            // If there is a captured image, apply the selected filter and display it
            capturedImage?.let { image ->
                Image(
                    bitmap = image.asImageBitmap(),
                    contentDescription = "Captured Image",
                    colorFilter = selectedFilter?.let { ColorFilter.colorMatrix(it) },
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop // Adjust content scale as needed
                )
            }
        }

        // Filter row to allow the user to select filters
        FilterRow { filter -> selectedFilter = filter }

        // Button to save the captured image to the gallery
        Button(
            onClick = {
                if (capturedImage != null) {
                    // Save to gallery if the image is captured
                    saveImageToGallery(context = current, bitmap = capturedImage!!)
                } else {
                    // Show a message or handle the case when no image is captured
                    Toast.makeText(current, "No image captured to save.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)
        ) {
            Text("Save to Gallery")
        }
    }
}




fun saveImageToGallery(context: Context, bitmap: Bitmap) {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "captured_image_${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    uri?.let {
        resolver.openOutputStream(it)?.use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
    }
}

