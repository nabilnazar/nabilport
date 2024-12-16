package com.nabilnazar.audiorecorderapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ContentValues
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import android.app.Notification
import android.os.ParcelFileDescriptor

class AudioRecordingService : Service() {
    private var mediaRecorder: MediaRecorder? = null
    private var stopRecordingCallback: (() -> Unit)? = null
    private val CHANNEL_ID = "AudioRecordingChannel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "START_RECORDING" -> {
                startRecording()
                startForeground(1, createNotification()) // Start service in foreground
            }
            "STOP_RECORDING" -> {
                stopRecording()
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun startRecording() {
        try {
            val fileName = "audio_record_${System.currentTimeMillis()}.mp3"

            // Prepare content values for MediaStore
            val contentValues = ContentValues().apply {
                put(MediaStore.Audio.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp3")
                put(MediaStore.Audio.Media.RELATIVE_PATH, "Music/AudioRecorderApp")
                put(MediaStore.Audio.Media.IS_PENDING, 1) // Mark file as "in progress"
            }

            val resolver = contentResolver
            val audioUri = resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues)

            audioUri?.let { uri ->
                // Open ParcelFileDescriptor to get the file descriptor
                val pfd: ParcelFileDescriptor? = resolver.openFileDescriptor(uri, "w") // Write mode

                pfd?.fileDescriptor?.let { fd ->
                    mediaRecorder = MediaRecorder().apply {
                        setAudioSource(MediaRecorder.AudioSource.MIC)
                        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                        setOutputFile(fd) // Use file descriptor
                        prepare()
                        start()
                    }

                    Log.d("AudioService", "Recording started: $fileName")

                    // Callback to mark the file as complete after stopping
                    stopRecordingCallback = {
                        contentValues.put(MediaStore.Audio.Media.IS_PENDING, 0)
                        resolver.update(uri, contentValues, null, null)
                    }
                }
            } ?: Log.e("AudioService", "Failed to create audio file URI")
        } catch (e: Exception) {
            Log.e("AudioService", "Error starting recording", e)
        }
    }

    private fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            stopRecordingCallback?.invoke() // Finalize the recording file
            Log.d("AudioService", "Recording stopped successfully")
        } catch (e: Exception) {
            Log.e("AudioService", "Error stopping recording", e)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Recording Audio")
            .setContentText("Audio recording is in progress...")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Audio Recording Service",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)

    }

    override fun onBind(intent: Intent?): IBinder? = null
}
