package com.abcarrell.sampleactivity.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abcarrell.sampleactivity.SampleActivityApp
import com.abcarrell.sampleactivity.ui.theme.SampleActivityTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.compose.koinViewModel

@ExperimentalCoroutinesApi
@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SampleActivityApp {
                SampleActivityTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        val viewModel = koinViewModel<MainViewModel>()
                        val navController = rememberNavController()
                        val modifier = Modifier.padding(innerPadding)
                        NavHost(navController, startDestination = "home") {
                            composable("home") {
                                viewModel.updateQueryString("")
                                MainScreen(
                                    onQuotes = { navController.navigate("details") },
                                    onSearch = { navController.navigate("search") },
                                    onImage = { navController.navigate("image") },
                                    modifier = modifier
                                )
                            }
                            composable("details") {
                                QuotesScreen(viewModel, modifier = modifier)
                            }
                            composable("search") {
                                SearchScreen(viewModel, modifier = modifier)
                            }
                            composable("image") { }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(onQuotes: () -> Unit, onSearch: () -> Unit, onImage: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Button(onClick = onQuotes, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Quotes") }
        Button(onClick = onSearch, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Search") }
        Button(onClick = onImage, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Image") }
    }
}

@ExperimentalCoroutinesApi
@ExperimentalMaterial3Api
@Composable
fun QuotesScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val quotes = viewModel.quotesState.collectAsStateWithLifecycle()
    PullToRefreshBox(
        isRefreshing = quotes.value.isRefreshing,
        onRefresh = { viewModel.refreshData() },
        modifier = modifier,
    ) {
        QuotesStateDisplay(quotes.value)
    }
}

@ExperimentalCoroutinesApi
@Composable
fun SearchScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    var searchText by rememberSaveable { mutableStateOf("") }
    val quotes = viewModel.quotesState.collectAsStateWithLifecycle()
    Column(modifier = modifier) {
        TextField(
            value = searchText,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp),
            onValueChange = {
                searchText = it
                viewModel.updateQueryString(searchText)
            }
        )
        QuotesStateDisplay(quotes.value)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SampleActivityTheme {
        Greeting("Android")
    }
}
