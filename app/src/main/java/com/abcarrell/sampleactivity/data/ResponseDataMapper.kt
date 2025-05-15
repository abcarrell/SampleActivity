package com.abcarrell.sampleactivity.data

import java.io.IOException
import retrofit2.Response

fun interface DataMapper<S, T> : (S) -> T

fun <T> responseDataMapper() = DataMapper<Response<T>, Result<T>> { response ->
    runCatching {
        if (response.isSuccessful) checkNotNull(response.body()) { "body is null" }
        else throw with(response) {
            IOException("API Error ${code()}: ${errorBody()?.string() ?: message()}")
        }
    }
}
