package com.example.filmwatch.data.remote

import com.google.gson.annotations.SerializedName

data class OmdbSearchResponse(
    @SerializedName("Search") val search: List<OmdbSearchItemDto>? = null,
    @SerializedName("Response") val response: String,
    @SerializedName("Error") val error: String? = null
)

data class OmdbSearchItemDto(
    @SerializedName("Title") val title: String,
    @SerializedName("Year") val year: String,
    @SerializedName("imdbID") val imdbId: String,
    @SerializedName("Poster") val poster: String
)

data class OmdbDetailResponse(
    @SerializedName("Title") val title: String? = null,
    @SerializedName("Year") val year: String? = null,
    @SerializedName("Genre") val genre: String? = null,
    @SerializedName("Director") val director: String? = null,
    @SerializedName("Plot") val plot: String? = null,
    @SerializedName("imdbRating") val imdbRating: String? = null,
    @SerializedName("Poster") val poster: String? = null,
    @SerializedName("imdbID") val imdbId: String? = null,
    @SerializedName("Response") val response: String,
    @SerializedName("Error") val error: String? = null
)
