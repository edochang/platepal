package com.example.platepal.api

import com.example.platepal.data.SpoonacularRecipe
import com.example.platepal.data.SpoonacularRecipeInfo
import retrofit2.http.GET
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Path
import retrofit2.http.Query

interface SpoonacularApi {
    @GET("/recipes/complexSearch?apiKey=$APIKEY&sort=popularity&number=$RECIPE_LIMIT")
    suspend fun getPopularRecipes(): RecipeResponse

    @GET("/recipes/{spoonacularRecipeId}/information?apiKey=$APIKEY&includeNutrition=false&addWinePairing=false&addTasteData=false")
    suspend fun getRecipeInfo(
        @Path("spoonacularRecipeId") spoonacularRecipeId: String
    ): SpoonacularRecipeInfo

    data class RecipeResponse(val results: List<SpoonacularRecipe>)

    companion object {
        private const val RECIPE_LIMIT = 50
        private const val APIKEY: String = com.example.platepal.BuildConfig.SPOONACULAR_API_KEY

        fun create(): SpoonacularApi {
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