package com.example.filmwatch.data.remote

import com.example.filmwatch.util.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {
    val omdbApi: OmdbApi by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.OMDB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OmdbApi::class.java)
    }
}
