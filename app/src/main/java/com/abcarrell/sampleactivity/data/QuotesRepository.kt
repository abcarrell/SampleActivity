package com.abcarrell.sampleactivity.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import retrofit2.Response

interface QuotesRepository {
    suspend fun fetchQuotes()

    fun observeQuotes(): Flow<Result<List<Quote>>>
}

class QuotesRepositoryImpl(
    private val quoteApi: QuoteApi,
    private val responseMapper: DataMapper<Response<QuoteResponse>, Result<QuoteResponse>>,
    private val coroutineDispatcher: CoroutineDispatcher
) : QuotesRepository {
    private val _quotes = MutableStateFlow<Result<List<Quote>>>(Result.success(listOf()))

    override suspend fun fetchQuotes() {
        withContext(coroutineDispatcher) {
            quoteApi.getAllQuotes().run(responseMapper).map { it.quotes }.let { quotes ->
                _quotes.update { quotes }
            }
        }
    }

    override fun observeQuotes(): Flow<Result<List<Quote>>> = _quotes
}
