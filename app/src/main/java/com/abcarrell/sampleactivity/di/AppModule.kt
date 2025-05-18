package com.abcarrell.sampleactivity.di

import com.abcarrell.sampleactivity.data.QuoteApi
import com.abcarrell.sampleactivity.data.QuoteResponse
import com.abcarrell.sampleactivity.data.QuotesRepository
import com.abcarrell.sampleactivity.data.QuotesRepositoryImpl
import com.abcarrell.sampleactivity.data.responseDataMapper
import com.abcarrell.sampleactivity.ui.MainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@ExperimentalCoroutinesApi
fun appModule() = module {
    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) })
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(QuoteApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
            .create(QuoteApi::class.java)
    }

    single<QuotesRepository> {
        QuotesRepositoryImpl(
            get(),
            get(qualifier = qualifier<AppModule.QuoteResponseDataMapper>())
        )
    }

    single(qualifier = qualifier<AppModule.QuoteResponseDataMapper>()) {
        responseDataMapper<QuoteResponse>()
    }

    viewModel<MainViewModel> { MainViewModel(get()) }
}

object AppModule {
    object QuoteResponseDataMapper
}
