package com.example.platepal.repository

import com.example.platepal.api.SpoonacularApi
import com.example.platepal.data.SpoonacularRecipe
import com.example.platepal.data.SpoonacularRecipeInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SpoonacularRecipeRepository(
    private val spoonacularApi: SpoonacularApi
) {
    suspend fun getRecipes(): List<SpoonacularRecipe> {
        return withContext(Dispatchers.IO) {
            val response = spoonacularApi.getPopularRecipes()
            response.results
        }
    }

    suspend fun getRecipeInfo(spoonacularRecipeId: String): SpoonacularRecipeInfo {
        return withContext(Dispatchers.IO) {
            val response = spoonacularApi.getRecipeInfo(spoonacularRecipeId)
            response
        }
    }
}