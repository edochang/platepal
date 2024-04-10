package com.example.platepal.api

import com.example.platepal.data.SpoonacularRecipe
import retrofit2.http.GET
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Query

interface SpoonacularApi {
    @GET("/recipes/complexSearch?sort=popularity&number=10")
    suspend fun getPopularRecipes(
        @Query("apiKey") apiKey: String
    ): RecipeResponse

    data class RecipeResponse(val results: List<SpoonacularRecipe>)

    companion object {
        private fun create(): SpoonacularApi {
            val moshi = ApiUtils.getMoshiConverterFactory()

            val host = "api.spoonacular.com"
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