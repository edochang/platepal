package com.example.platepal.repository

import com.example.platepal.api.SpoonacularTestApi
import com.example.platepal.data.SpoonacularRecipe
import retrofit2.HttpException

class RecipeRepositoryTestImpl(
    private val spoonacularTestApis: SpoonacularTestApi
): RecipeRepository {
    override suspend fun getSpoonacularRecipes(): List<SpoonacularRecipe> {
        return try {
            spoonacularTestApis.getPopularRecipes("TEST_API_KEY").results
        } catch (e: HttpException) {
            emptyList()
        }
    }
}