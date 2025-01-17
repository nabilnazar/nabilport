package com.nabilnazar.deck56aiassistance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nabilnazar.deck56aiassistance.ui.theme.Deck56AiAssistanceTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val viewModel = ChatViewModel()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Deck56AiAssistanceTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(viewModel)
                }
            }
        }
    }
}


@Composable
fun MainScreen(viewModel: ChatViewModel) {
    val chatHistory by viewModel.chatHistory.collectAsState()
    var prompt by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextField(
            value = prompt,
            onValueChange = { prompt = it },
            placeholder = { Text("Enter your prompt") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { viewModel.sendPrompt(prompt) },
            modifier = Modifier.align(Alignment.End).padding(top = 8.dp)
        ) {
            Text("Send")
        }
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(chatHistory.size) { message ->
                Text("${chatHistory[message].role}: ${chatHistory[message].content}")
            }
        }
    }
}


class ChatViewModel : ViewModel() {
    private val _chatHistory = MutableStateFlow<List<Message>>(emptyList())
    val chatHistory: StateFlow<List<Message>> = _chatHistory

    fun sendPrompt(prompt: String) {
        viewModelScope.launch {
            val response = sendToOpenAI(prompt)
            val updatedHistory = _chatHistory.value + listOf(
                Message("User", prompt),
                Message("Assistant", response)
            )
            _chatHistory.value = updatedHistory
        }
    }

    private suspend fun sendToOpenAI(prompt: String): String {

        return networkSendToOpenAI(prompt)
    }
}


//fun setAlarm(context: Context, hour: Int, minute: Int) {
//    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//    val intent = Intent(context,AlarmReceiver::class.java)
//    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
//    val calendar = Calendar.getInstance().apply {
//        set(Calendar.HOUR_OF_DAY, hour)
//        set(Calendar.MINUTE, minute)
//    }
//    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
//}
