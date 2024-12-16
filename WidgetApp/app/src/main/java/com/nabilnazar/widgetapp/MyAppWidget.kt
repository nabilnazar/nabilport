package com.nabilnazar.widgetapp




import android.annotation.SuppressLint
import android.content.Context
import android.os.SystemClock
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

class MyAppWidget : GlanceAppWidget() {

    val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-pro-latest",
        apiKey = apikey
    )

    @SuppressLint("RestrictedApi", "SimpleDateFormat")
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Load data needed to render the AppWidget.
        // Use `withContext` to switch to another thread for long running
        // operations.

        val quote = fetchMotivationalQuote()

        provideContent {
            // create your AppWidget here

                Column(
                    modifier = GlanceModifier.padding(8.dp)
                ) {
                    val currentDate = SimpleDateFormat("EEEE, MMMM d, yyyy").format(Date())
                    val systemUptime = SystemClock.elapsedRealtime() / 1000

                    Text(
                          text = "Date: $currentDate",
                        style = TextStyle(color = ColorProvider(R.color.teal_700))
                    )

                    val formattedTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(Date())
                    Text(
                        text = formattedTime,
                        style = TextStyle(color = ColorProvider(R.color.teal_700))
                    )
                    Text(text = "Uptime: $systemUptime seconds",
                        style = TextStyle(color = ColorProvider(R.color.teal_700)))

                    Text(
                        text = quote,
                        style = TextStyle(color = ColorProvider(R.color.white))
                    )

                }

        }
    }



    private suspend fun fetchMotivationalQuote(): String {
        return withContext(Dispatchers.IO) {
            try {
                // Call your AI content generation function here
                val response = generativeModel.generateContent("Provide a short motivational quote.")
                response.text ?: "Stay positive and keep going!"
            } catch (e: Exception) {
                "Failed to fetch quote. Keep smiling!"
            }
        }
    }
}
    @Composable
    private fun MyContent() {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Where to?", modifier = GlanceModifier.padding(12.dp))
            Row(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    text = "Home",
                    onClick = actionStartActivity<MainActivity>()
                )
                Button(
                    text = "Work",
                    onClick = actionStartActivity<MainActivity>()
                )
            }
        }
    }

