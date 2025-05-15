package com.abcarrell.sampleactivity.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Response

interface QuotesRepository {
    suspend fun getQuotes(): Result<List<Quote>>

    suspend fun fetchQuotes()

    fun observeQuotes(): Flow<Result<List<Quote>>>
}

class QuotesRepositoryImpl(
    private val quoteApi: QuoteApi,
    private val dataMapper: DataMapper<Response<QuoteResponse>, Result<QuoteResponse>>
) : QuotesRepository {
    override suspend fun getQuotes(): Result<List<Quote>> {
        return quoteApi.getAllQuotes().run(dataMapper).map { it.quotes }
    }

    private val _quotes = MutableStateFlow<Result<List<Quote>>>(Result.success(listOf()))

    override suspend fun fetchQuotes() {
        quoteApi.getAllQuotes().run(dataMapper).map { it.quotes }.let { quotes ->
            _quotes.update { quotes }
        }
    }

    override fun observeQuotes(): Flow<Result<List<Quote>>> {
        return _quotes
    }
}
