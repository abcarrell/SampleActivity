package com.abcarrell.sampleactivity.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.abcarrell.sampleactivity.data.Quote

sealed interface QuotesState {
    val isRefreshing: Boolean
    @Composable
    fun Display()

    data object Loading : QuotesState {
        override val isRefreshing: Boolean
            get() = true
        @Composable
        override fun Display() {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }

    data class Retrieved(
        val quotes: List<Quote> = listOf(),
        val queryInput: QueryInput = QueryInput()
    ) : QuotesState {
        override val isRefreshing: Boolean
            get() = false
        @Composable
        override fun Display() {
            QuotesList(quotes)
        }
    }

    data class Error(
        val exception: Throwable
    ) : QuotesState {
        override val isRefreshing: Boolean
            get() = false
        @Composable
        override fun Display() {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = exception.message ?: "Unknown error occurred",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
