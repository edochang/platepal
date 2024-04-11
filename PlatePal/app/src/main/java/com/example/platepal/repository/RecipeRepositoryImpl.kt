package com.example.platepal.repository

import com.example.platepal.api.SpoonacularApi
import com.example.platepal.data.SpoonacularRecipe
//import com.example.platepal.db.RecipesDBHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*

class RecipeRepositoryImpl(
    private val spoonacularApi: SpoonacularApi
): RecipeRepository {
    private val recipesDBHelper = RecipesDBHelper()

    override suspend fun getSpoonacularRecipes(): List<SpoonacularRecipe> {
        return withContext(Dispatchers.IO) {
            val response = spoonacularApi.getPopularRecipes(com.example.platepal.BuildConfig.SPOONACULAR_API_KEY)
            response.results
        }
    }
}

 */