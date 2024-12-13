package com.example.dicodingstory.utils

import com.example.dicodingstory.ui.auth.data.AuthResponse
import com.example.dicodingstory.ui.auth.data.SignInBody
import com.example.dicodingstory.ui.auth.data.SignUpBody
import com.example.dicodingstory.data.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface ApiConsumer {

    @POST("register")
    suspend fun registerUser(@Body body: SignUpBody): Response<AuthResponse>

    @POST("login")
    suspend fun loginUser(@Body body: SignInBody): Response<AuthResponse>

    @GET("stories")
    suspend fun getStories(@Header("Authorization") token: String): Response<StoryResponse>

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?,
        @Header("Authorization") token: String
    ): Response<StoryResponse>

    // API untuk menambah story tanpa autentikasi (guest)
    @Multipart
    @POST("stories/guest")
    suspend fun addStoryGuest(
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): Response<StoryResponse>
}

