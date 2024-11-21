package com.example.techsmithsample.core.data.network

import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

abstract class SafeApiRequest {

    suspend fun <T : Any> safeApiRequest(call: suspend () -> Response<T>): Resource<T> {
        val response = call.invoke()
        if (response.isSuccessful) {
            return Resource.Success(response.body()!!)
        } else {
            val error = response.errorBody()?.string()
            val message = StringBuilder()
            error.let {
                try {
                    message.append(JSONObject(it ?: "Error"))
                } catch (e: JSONException) {
                    message.append(e.message)
                    e.printStackTrace()
                } catch (e: Exception) {
                    message.append(e.message)
                    e.printStackTrace()
                }
            }
            return Resource.Error(
                error = ErrorResponse(
                    message.toString(),
                    errorCode = response.code()
                )
            )
        }
    }
}