package com.abcarrell.sampleactivity.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abcarrell.sampleactivity.data.Quote
import com.abcarrell.sampleactivity.data.QuotesRepository
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@ExperimentalCoroutinesApi
class MainViewModel(private val quotesRepository: QuotesRepository) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _queryInput: MutableStateFlow<QueryInput> by lazy { MutableStateFlow(QueryInput()) }

    private val _refreshData =
        MutableSharedFlow<Unit>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private val _quotes: Flow<Result<List<Quote>>> = flow {
        emit(Unit)
        emitAll(_refreshData)
    }.flatMapLatest {
        flow {
            delay(1.seconds)
            emit(quotesRepository.getQuotes())
        }
    }

    val quotesState = combine(_quotes, _queryInput) { result, queryInput ->
        result.fold({ list ->
            val filteredList = list.filter { quote ->
                quote.author.contains(queryInput.input.orEmpty(), ignoreCase = true)
            }
            QuotesState.Retrieved(filteredList, queryInput)
        }, { QuotesState.Error(it) }).also { _isRefreshing.update { false } }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(TIMEOUT, REPLAY_EXPIRES - TIMEOUT),
        QuotesState.Loading
    )

    fun updateQueryString(input: String) {
        _queryInput.update { it.copy(input = input) }
    }

    fun refreshData() {
        _isRefreshing.update { true }
        _refreshData.tryEmit(Unit)
    }

    companion object {
        private val TIMEOUT = 1.seconds
        private val REPLAY_EXPIRES = 10.seconds
    }
}

data class QueryInput(
    val input: String? = null
)
