package com.example.dicodingstory.data

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val message: String) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()

    companion object {
        fun <T> success(data: T): ApiResult<T> = Success(data)
        fun error(message: String): ApiResult<Nothing> = Error(message)
        fun loading(): ApiResult<Nothing> = Loading
    }
}
