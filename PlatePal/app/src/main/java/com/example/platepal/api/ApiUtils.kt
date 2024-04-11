package com.example.platepal.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.net.HttpURLConnection

object ApiUtils {
    // Utility singleton object
    fun buildUrl(host: String): HttpUrl {
        return HttpUrl.Builder()
            .scheme("https")
            .host(host)
            .build()
    }

    fun buildClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                this.level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()
    }

    fun getMoshiConverterFactory(): Moshi {
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }
}

data class ApiResponse<Any>(
    val httpCode: Int = HttpURLConnection.HTTP_OK,
    val body: Any? = null,
    val errorMessage: String? = null
)