package ca.unb.mobiledev.campuseventlist.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Singleton object to provide Retrofit instance
object RetrofitClient {
    
    private const val BASE_URL = "https://api.haulradar.com/"
    
    // Logging interceptor to see API requests/responses in logcat
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    // OkHttp client with logging
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
    
    // Retrofit instance with Gson converter
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    // API service instance
    val apiService: SchoolApiService by lazy {
        retrofit.create(SchoolApiService::class.java)
    }
}

