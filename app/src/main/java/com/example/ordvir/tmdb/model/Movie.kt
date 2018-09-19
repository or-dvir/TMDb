package com.example.ordvir.tmdb.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
class Movie(@JsonProperty("id")
            val id: Long = 0,
            @JsonProperty("title")
            val title: String? = null,
            @JsonProperty("overview")
            val overview: String? = null,
            @JsonProperty("release_date")
            val release_date: String? = null,
            @JsonProperty("vote_average")
            val vote_average: Float? = null,
            @JsonProperty("poster_path")
            val poster_path: String? = null,
            @JsonProperty("backdrop_path")
            val backdrop_path: String? = null)
    : Serializable
{
    fun getYear() = release_date?.split("-")?.get(0)
}