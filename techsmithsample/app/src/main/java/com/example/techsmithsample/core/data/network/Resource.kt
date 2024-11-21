package com.example.techsmithsample.core.data.network

data class ErrorResponse(
    val error: String?,
    val errorCode: Int
)

sealed class Resource<T>(
    val data: T? = null,
    val error: ErrorResponse? = null
) {
    class Loading<T>(data: T?) : Resource<T>(data = data)
    class Success<T>(data: T?) : Resource<T>(data = data)
    class Error<T>(data: T? = null, error: ErrorResponse? = null) :
        Resource<T>(data = data, error = error)
}