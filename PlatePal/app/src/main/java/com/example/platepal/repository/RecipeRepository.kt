package com.example.platepal.repository

import com.example.platepal.data.SpoonacularRecipe

interface RecipeRepository {

    /* Gets recipes from the Spoonacular API.
        Returns:
            List<SpoonacularRecipe>: List of popular recipes
     */
    suspend fun getSpoonacularRecipes(): List<SpoonacularRecipe>

    //suspend fun getRecipes(): List<SpoonacularRecipe>
}