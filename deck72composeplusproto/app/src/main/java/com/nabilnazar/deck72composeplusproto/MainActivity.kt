package com.nabilnazar.deck72composeplusproto

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewModelScope
import com.nabilnazar.deck72composeplusproto.ui.theme.Deck72composeplusprotoTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoBuf

@Serializable
data class User(val id: Int, val name: String)

class MainViewModel : ViewModel() {

    // Fake data source using Kotlin Flow
    private val _userFlow = MutableStateFlow<List<User>>(emptyList())
    val userFlow: StateFlow<List<User>> = _userFlow.asStateFlow()

    init {
        viewModelScope.launch {
            // Simulate loading data
            _userFlow.emit(
                listOf(
                    User(1, "Alice"),
                    User(2, "Bob"),
                    User(3, "Charlie")
                )
            )
        }
    }

    // Serialize the data using ProtoBuf
    @OptIn(ExperimentalSerializationApi::class)
    fun serializeData(user: User): ByteArray? {
        return try {
            ProtoBuf.encodeToByteArray(User.serializer(), user)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Deserialize the data using ProtoBuf
    @OptIn(ExperimentalSerializationApi::class)
    fun deserializeData(byteArray: ByteArray?): User? {
        return try {
            byteArray?.let { ProtoBuf.decodeFromByteArray(User.serializer(), byteArray)}
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

class MainActivity : ComponentActivity() {


    val Context.dataStore by preferencesDataStore(name = "settings")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProvider(this)[MainViewModel::class.java]






        setContent {
            Deck72composeplusprotoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    UserListScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel
                    )
                    MainScreen(this)
                }
            }
        }
    }

}
@Composable
fun UserListScreen(modifier: Modifier = Modifier, viewModel: MainViewModel) {
    val userList by viewModel.userFlow.collectAsState(initial = emptyList())

    LazyColumn(modifier = modifier) {
        items(userList) { user ->
            val serializedData = viewModel.serializeData(user)
            val deserializedUser = viewModel.deserializeData(serializedData)

            BasicText(
                text = deserializedUser?.let {
                    "ID: ${it.id}, Name: ${it.name}"
                } ?: "Error decoding user data"
            )
        }
    }
}
