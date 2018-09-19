package com.example.ordvir.tmdb.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
class CastMember(@JsonProperty("cast_id")
                 val id: Long = 0,
                 @JsonProperty("character")
                 val characterName: String? = null,
                 @JsonProperty("name")
                 val actorName: String? = null,
                 @JsonProperty("profile_path")
                 val profile_path: String? = null)
    : Serializable