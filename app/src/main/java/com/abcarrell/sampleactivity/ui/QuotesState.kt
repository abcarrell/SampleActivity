package com.abcarrell.sampleactivity.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abcarrell.sampleactivity.data.Quote

sealed interface QuotesState {
    val isRefreshing: Boolean
    @Composable
    fun Display()

    data object Loading : QuotesState {
        override val isRefreshing: Boolean = false
        @Composable
        override fun Display() {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }

    data class Retrieved(
        val quotes: List<Quote> = listOf(),
        val queryInput: QueryInput = QueryInput(),
        override val isRefreshing: Boolean = false
    ) : QuotesState {
        @Composable
        override fun Display() {
            LazyColumn {
                items(quotes) { item ->
                    QuoteItem(quote = item.quote, author = item.author)
                }
            }
        }

        @Composable
        fun QuoteItem(quote: String, author: String) {
            Card(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
            ) {
                Text("$quote \n - $author", modifier = Modifier.padding(6.dp, 0.dp))
            }
        }
    }

    data class Error(
        val exception: Throwable,
        override val isRefreshing: Boolean = false
    ) : QuotesState {
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

@Composable
fun QuotesStateDisplay(state: QuotesState) {
    state.Display()
}
