package com.example.platepal.api

import retrofit2.http.GET
import retrofit2.http.Query

interface SpoonacularTestApi {
    @GET("/recipes/complexSearch?sort=popularity&number=10")
    suspend fun getPopularRecipes(
        @Query("apiKey") apiKey: String
    ): SpoonacularApi.RecipeResponse
}