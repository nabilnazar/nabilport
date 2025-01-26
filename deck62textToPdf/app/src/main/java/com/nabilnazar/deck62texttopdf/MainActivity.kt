package com.nabilnazar.deck62texttopdf

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.itextpdf.text.*
import com.itextpdf.text.pdf.ColumnText
import com.itextpdf.text.pdf.PdfWriter
import com.nabilnazar.deck62texttopdf.ui.theme.Deck62textToPdfTheme
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(this, "Permission required to save PDF", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        setContent {
            Deck62textToPdfTheme {
                MainScreen()
            }
        }
    }

    @Composable
    fun MainScreen() {
        var textInput by remember { mutableStateOf(TextFieldValue()) }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    TextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        label = { Text("Enter Text") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f) // Ensures the TextField can scroll if needed
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { savePdfWithHeaderFooter(textInput.text) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Print PDF")
                    }
                }
            }
        )
    }

    private fun savePdfWithHeaderFooter(content: String) {
        try {
            val outputStream: OutputStream

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "GeneratedDocument.pdf")
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri == null) {
                    Toast.makeText(this, "Failed to create file", Toast.LENGTH_SHORT).show()
                    return
                }
                outputStream = resolver.openOutputStream(uri) ?: throw Exception("Failed to open output stream")
            } else {
                val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!directory.exists()) directory.mkdirs()
                val pdfFile = File(directory, "GeneratedDocument.pdf")
                outputStream = FileOutputStream(pdfFile)
            }

            createPdf(outputStream, content)

            Toast.makeText(this, "PDF saved to Downloads", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error generating PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun createPdf(outputStream: OutputStream, content: String) {
        val document = Document(PageSize.A4, 36f, 36f, 100f, 100f)
        try {
            val writer = PdfWriter.getInstance(document, outputStream)
            document.open()

            val canvas = writer.directContent

            // Header
            val headerImage = BitmapFactory.decodeResource(resources, R.drawable.header_logo)
                ?: throw IllegalStateException("Header logo not found")
            val headerStream = ByteArrayOutputStream()
            headerImage.compress(Bitmap.CompressFormat.PNG, 100, headerStream)
            val headerPdfImage = Image.getInstance(headerStream.toByteArray())
            headerPdfImage.scaleToFit(50f, 50f)
            headerPdfImage.setAbsolutePosition(36f, PageSize.A4.height - 60f)
            canvas.addImage(headerPdfImage)

            // Content
            if (content.isEmpty()) {
                val noDataParagraph = Paragraph("No content provided", Font(Font.FontFamily.HELVETICA, 12f))
                document.add(noDataParagraph)
            } else {
                val paragraph = Paragraph(content, Font(Font.FontFamily.HELVETICA, 12f))
                val columnText = ColumnText(canvas)
                columnText.addText(paragraph)

                val yStart = PageSize.A4.height - 120f
                val yEnd = 100f
                columnText.setSimpleColumn(50f, yEnd, PageSize.A4.width - 50f, yStart)

                while (ColumnText.hasMoreText(columnText.go())) {
                    document.newPage()
                    columnText.setSimpleColumn(50f, yEnd, PageSize.A4.width - 50f, yStart)
                }
            }

            // Footer
            val footerImage = BitmapFactory.decodeResource(resources, R.drawable.footer_logo)
                ?: throw IllegalStateException("Footer logo not found")
            val footerStream = ByteArrayOutputStream()
            footerImage.compress(Bitmap.CompressFormat.PNG, 100, footerStream)
            val footerPdfImage = Image.getInstance(footerStream.toByteArray())
            footerPdfImage.scaleToFit(50f, 50f)
            footerPdfImage.setAbsolutePosition(36f, 20f)
            canvas.addImage(footerPdfImage)

            val emailText = Paragraph("Email: support@z3automobile.com", Font(Font.FontFamily.HELVETICA, 12f))
            ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, emailText, 100f, 30f, 0f)

        } catch (e: Exception) {
            Log.d("createPdf", "Error creating PDF: ${e.message}")
            throw e
        } finally {
            document.close()
            outputStream.close()
        }
    }
}
