package com.abcarrell.sampleactivity.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abcarrell.sampleactivity.data.Quote
import com.abcarrell.sampleactivity.data.QuotesRepository
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@ExperimentalCoroutinesApi
class MainViewModel(private val quotesRepository: QuotesRepository) : ViewModel() {
    private val isRefreshingFlow by lazy { MutableStateFlow(false) }

    private val quotesResultFlow: Flow<Result<List<Quote>>> = flow {
        emit(true)
        emitAll(isRefreshingFlow.filter { it })
    }.flatMapLatest {
        flow {
            quotesRepository.fetchQuotes()
            delay(1.seconds)
            isRefreshingFlow.update { false }
            emitAll(quotesRepository.observeQuotes())
        }
    }

    private val queryInputFlow: MutableStateFlow<QueryInput> by lazy { MutableStateFlow(QueryInput()) }

    val quotesState =
        combine(isRefreshingFlow, quotesResultFlow, queryInputFlow) { isRefreshing, quotesResult, queryInput ->
            quotesResult.fold({ list ->
                val filteredList = list.filter { quote ->
                    quote.author.contains(queryInput.input.orEmpty(), ignoreCase = true)
                }
                QuotesState.Retrieved(filteredList, queryInput, isRefreshing)
            }, { e ->
                QuotesState.Error(e, isRefreshing)
            })
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            QuotesState.Loading
        )

    fun updateQueryString(input: String) {
        queryInputFlow.update { it.copy(input = input) }
    }

    fun refreshData() {
        isRefreshingFlow.update { true }
    }
}

data class QueryInput(
    val input: String? = null
)
