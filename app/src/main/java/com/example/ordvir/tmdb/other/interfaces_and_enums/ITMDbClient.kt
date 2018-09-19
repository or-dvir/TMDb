package com.example.ordvir.tmdb.other.interfaces_and_enums

import com.example.ordvir.tmdb.other.ServerResponseCast
import com.example.ordvir.tmdb.other.ServerResponseConfiguration
import com.example.ordvir.tmdb.other.ServerResponseRuntime
import com.example.ordvir.tmdb.other.ServerResponseTopRated
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ITMDbClient
{
    companion object
    {
        private const val API_KEY = "4e0be2c22f7268edffde97481d49064a"
    }

    @GET("movie/top_rated?api_key=$API_KEY")
    fun getTopRated(@Query("page") pageNum: Int): Call<ServerResponseTopRated>

    @GET("movie/{movieId}?api_key=$API_KEY")
    fun getRuntime(@Path("movieId") movieId: Long): Call<ServerResponseRuntime>

    @GET("movie/{movieId}/credits?api_key=$API_KEY")
    fun getCast(@Path("movieId") movieId: Long): Call<ServerResponseCast>

    @GET("configuration?api_key=$API_KEY")
    fun getConfiguration(): Call<ServerResponseConfiguration>
}
