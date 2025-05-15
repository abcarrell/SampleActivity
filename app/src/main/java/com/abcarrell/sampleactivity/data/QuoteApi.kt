package com.abcarrell.sampleactivity.data

import retrofit2.Response
import retrofit2.http.GET

interface QuoteApi {
    @GET("quotes")
    suspend fun getAllQuotes(): Response<QuoteResponse>

    companion object {
        const val BASE_URL = "https://dummyjson.com/"
    }
}
