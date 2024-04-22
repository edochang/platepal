package com.example.platepal.api

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface GeminiApi {
    @POST("/v1/models/gemini-pro:generateContent?key=$APIKEY")
    suspend fun requestAIRecipe(@Body geminiContent: kotlin.Any): ContentResponse

    data class ContentResponse(val contents: List<kotlin.Any>)

    companion object {
        private const val APIKEY: String = com.example.platepal.BuildConfig.GEMINI_API_KEY

        fun create(): SpoonacularApi {
            val moshi = ApiUtils.getMoshiConverterFactory()

            val host = "generativelanguage.googleapis.com"
            val url = ApiUtils.buildUrl(host)
            val client = ApiUtils.buildClient()
            return Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(SpoonacularApi::class.java)
        }
    }
}