package com.example.platepal.repository

import com.example.platepal.api.SpoonacularApi
import com.example.platepal.data.SpoonacularRecipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SpoonacularRecipeRepository(
    private val spoonacularApi: SpoonacularApi
) {
    suspend fun getRecipes(): List<SpoonacularRecipe> {
        return withContext(Dispatchers.IO) {
            val response = spoonacularApi.getPopularRecipes(com.example.platepal.BuildConfig.SPOONACULAR_API_KEY)
            response.results
        }
    }
}