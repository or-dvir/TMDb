package com.example.ordvir.tmdb.other

import com.example.ordvir.tmdb.model.CastMember
import com.example.ordvir.tmdb.model.Movie
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class ServerResponseTopRated(@JsonProperty("results")
                             val results: List<Movie>? = null)

@JsonIgnoreProperties(ignoreUnknown = true)
class ServerResponseRuntime(@JsonProperty("runtime")
                            val runtime: Int? = null)

@JsonIgnoreProperties(ignoreUnknown = true)
class ServerResponseCast(@JsonProperty("cast")
                         val cast: List<CastMember>? = null)

@JsonIgnoreProperties(ignoreUnknown = true)
class ServerResponseConfiguration(@JsonProperty("images")
                                  val imageConfig: ImageConfig? = null)

@JsonIgnoreProperties(ignoreUnknown = true)
class ImageConfig(@JsonProperty("base_url")
                  val base_url: String? = null)