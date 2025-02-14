import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nabilnazar.deck76pulltorefresh.Dog
import com.nabilnazar.deck76pulltorefresh.DogResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class DogViewModel : ViewModel() {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    var dogs by mutableStateOf<List<Dog>>(emptyList())
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    init {
        fetchDogs()
    }

    fun fetchDogs() {
        viewModelScope.launch {
            isRefreshing = true
            delay(1000)
            val response = try {
                Log.d("DogViewModel", "Fetching dogs...")
                client.get("https://dogapi.dog/api/v2/breeds").body<DogResponse>()
            } catch (e: Exception) {
                Log.e("DogViewModel", "Error fetching dogs: ${e.message}", e)
                null
            }
            dogs = response?.data ?: emptyList()
            Log.d("DogViewModel", "Fetched ${dogs.size} dogs.")
            isRefreshing = false
        }
    }
}
