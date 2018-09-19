package com.example.ordvir.tmdb.other

import com.example.ordvir.tmdb.other.interfaces_and_enums.ITMDbClient
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

object SMyRetrofit
{
    val tmdbClient: ITMDbClient

    init
    {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .client(OkHttpClient.Builder().build())
                .build()

        tmdbClient = retrofit.create<ITMDbClient>(ITMDbClient::class.java)
    }
}