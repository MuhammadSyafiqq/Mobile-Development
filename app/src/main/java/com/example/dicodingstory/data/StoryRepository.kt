package com.example.dicodingstory.data

import com.example.dicodingstory.utils.ApiConsumer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(private val apiService: ApiConsumer) {

    suspend fun addStory(
        token: String,
        description: RequestBody,
        photo: MultipartBody.Part,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ): ApiResult<Any> {
        return try {
            val response = apiService.addStory(
                description = description,
                photo = photo,
                lat = lat,
                lon = lon,
                token = token
            )

            if (response.isSuccessful) {
                response.body()?.let {
                    ApiResult.Success(it)
                } ?: ApiResult.Error("Empty response")
            } else {
                ApiResult.Error(response.message() ?: "Unknown error")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Network error")
        }
    }

    suspend fun addStoryGuest(
        description: RequestBody,
        photo: MultipartBody.Part,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ): ApiResult<Any> {
        return try {
            val response = apiService.addStoryGuest(
                description = description,
                photo = photo,
                lat = lat,
                lon = lon
            )

            if (response.isSuccessful) {
                response.body()?.let {
                    ApiResult.Success(it)
                } ?: ApiResult.Error("Empty response")
            } else {
                ApiResult.Error(response.message() ?: "Unknown error")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Network error")
        }
    }

    fun getStories(token: String): Flow<ApiResult<StoryResponse>> = flow {
        try {
            emit(ApiResult.Loading)
            val response = apiService.getStories("Bearer $token")
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    emit(ApiResult.Success(body))
                } else {
                    emit(ApiResult.Error("Empty response body"))
                }
            } else {
                emit(ApiResult.Error("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message ?: "Unknown error"))
        }
    }
}
