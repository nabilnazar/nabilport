package com.nabilnazar.deck56aiassistance

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json



private val httpClient = HttpClient{
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

@Serializable
data class OpenAIRequest(
    val model: String,
    val messages: List<Message>
)

@Serializable
data class OpenAIResponse(
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val message: Message
)

@Serializable
data class Message(
    val role: String,
    val content: String
)

private const val OPENAI_API_URL = "https://api.openai.com/v1/chat/completions"
private const val API_KEY = api_key // Replace with your OpenAI API key

 suspend fun networkSendToOpenAI(prompt: String): String {
    val requestPayload = OpenAIRequest(
        model = "gpt-3.5-turbo",
        messages = listOf(
            //Message("system", "You are a helpful assistant."),
            Message("user", prompt)
        )
    )

    return try {
        val response: HttpResponse = httpClient.post(OPENAI_API_URL) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $API_KEY")
                append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            }
            setBody(requestPayload)
        }
        println("Response: ${response.bodyAsText()}")
        if (response.status == HttpStatusCode.OK) {
            val responseBody: String = response.bodyAsText()
            val parsedResponse: OpenAIResponse = Json.decodeFromString(responseBody)
            parsedResponse.choices.firstOrNull()?.message?.content ?: "No response"

        } else {
            "Error: ${response.status}"
        }
    } catch (e: Exception) {
        "Error: ${e.message}"
    }
}
