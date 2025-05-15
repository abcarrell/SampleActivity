package com.abcarrell.sampleactivity

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.abcarrell.sampleactivity.di.provideAppModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.compose.KoinApplication

@ExperimentalCoroutinesApi
@Composable
fun SampleActivityApp(content: @Composable () -> Unit) {
    val context = LocalContext.current.applicationContext
    KoinApplication(application = {
        androidLogger()
        androidContext(context)
        modules(provideAppModule())
    }, content)
}
