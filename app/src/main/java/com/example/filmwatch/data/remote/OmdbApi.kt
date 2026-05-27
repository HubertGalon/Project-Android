package com.example.filmwatch.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface OmdbApi {
    @GET("/")
    suspend fun searchFilms(
        @Query("apikey") apiKey: String,
        @Query("s") query: String
    ): OmdbSearchResponse

    @GET("/")
    suspend fun getFilmDetails(
        @Query("apikey") apiKey: String,
        @Query("i") imdbId: String,
        @Query("plot") plot: String = "full"
    ): OmdbDetailResponse
}
