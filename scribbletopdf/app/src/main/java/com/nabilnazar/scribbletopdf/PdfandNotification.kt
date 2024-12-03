package com.nabilnazar.scribbletopdf


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.compose.ui.geometry.Offset
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import java.io.IOException


private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001


@SuppressLint("NewApi")
fun generatePdfFromCanvas(context: Context, paths: List<List<Offset>>) {
    try {
        // Step 1: Create a PDF document
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(1080, 1920, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        // Step 2: Draw the paths on the PDF canvas
        val paint = Paint().apply {
            color = android.graphics.Color.BLACK
            strokeWidth = 8f
            style = Paint.Style.STROKE
        }

        paths.forEach { path ->
            if (path.isNotEmpty()) {
                val androidPath = android.graphics.Path().apply {
                    moveTo(path.first().x, path.first().y)
                    path.drop(1).forEach { point ->
                        lineTo(point.x, point.y)
                    }
                }
                canvas.drawPath(androidPath, paint)
            }
        }

        pdfDocument.finishPage(page)

        // Step 3: Save the PDF file using MediaStore API
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, "scribble_${System.currentTimeMillis()}.pdf")
            put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
            put(MediaStore.Downloads.RELATIVE_PATH, "Download/Scribbles/")
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        if (uri != null) {
            resolver.openOutputStream(uri).use { output ->
                pdfDocument.writeTo(output)
            }
            pdfDocument.close()

            // Step 4: Display a notification
            showNotification(context, "PDF Created", "Saved at: ${uri.path}", uri)
        } else {
            throw IOException("Failed to create MediaStore entry")
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}




fun showNotification(context: Context, title: String, message: String, fileUri: Uri) {
    // Check and request permission before proceeding
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        checkAndRequestNotificationPermission(context)
        return
    }

    val channelId = "pdf_creation_channel"
    val channelName = "PDF Notifications"


        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifications for PDF creation"
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)


    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_menu_save)
        .setContentTitle(title)
        .setContentText(message)
        .setStyle(NotificationCompat.BigTextStyle().bigText("Click to view the file."))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    val openPdfIntent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(fileUri, "application/pdf")
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY
    }

    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        openPdfIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    builder.setContentIntent(pendingIntent)

    with(NotificationManagerCompat.from(context)) {
        notify(System.currentTimeMillis().toInt(), builder.build())
    }
}




private fun checkAndRequestNotificationPermission(context: Context) {
    // Check if the permission is required (Android 13+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission not granted, show a request dialog

            ActivityCompat.requestPermissions(
                (context as Activity),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        }
    }
}
