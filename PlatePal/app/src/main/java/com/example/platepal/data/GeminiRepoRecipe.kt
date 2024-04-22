package com.example.platepal.data

import com.squareup.moshi.Json
import java.io.Serializable

data class GeminiRepoRecipe(
    val recipeName: String = "",
    val recipeIngredients: String = "",
    val recipeInstructions: String = "",
    val recipeNotes: String?,
): Serializable