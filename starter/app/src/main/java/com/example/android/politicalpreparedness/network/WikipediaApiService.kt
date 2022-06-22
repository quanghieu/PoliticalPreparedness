package com.example.android.politicalpreparedness.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

private const val BASE_URL = "https://en.wikipedia.org/"

private val retrofit = Retrofit.Builder()
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface WikipediaApiService {
    @GET("w/api.php?action=query&format=json&pithumbsize=100&prop=pageimages")
    suspend fun getImageUrl(@Query("titles", encoded = true) title: String) : String
}

object wikipediaAPI {
    val wikipediaApiService : WikipediaApiService by lazy {
        retrofit.create(WikipediaApiService::class.java)
    }
}
