    package com.example.dicodingstory.ui.auth.utils


    import com.example.dicodingstory.utils.ApiConsumer
    import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
    import okhttp3.OkHttpClient

    import retrofit2.Retrofit
    import retrofit2.converter.gson.GsonConverterFactory
    import java.util.concurrent.TimeUnit

    object ApiService {
        private const val BASE_URL = "https://story-api.dicoding.dev/v1/"

        fun getService() : ApiConsumer {

            val client: OkHttpClient = OkHttpClient.Builder()
                .connectTimeout(60,TimeUnit.SECONDS)
                .readTimeout(60,TimeUnit.SECONDS)
                .writeTimeout(60,TimeUnit.SECONDS)
                .build()

            val builder : Retrofit.Builder = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())

            val retrofit:Retrofit = builder.build()
            return retrofit.create(ApiConsumer::class.java)
        }
    }