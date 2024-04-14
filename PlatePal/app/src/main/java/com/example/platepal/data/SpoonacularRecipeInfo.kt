package com.example.platepal.data

import com.squareup.moshi.Json
import java.io.Serializable

/* The data class representing the Recipe data object provided by the Spoonacular Api:
    https://spoonacular.com/food-api/docs#Get-Recipe-Information
 */
data class SpoonacularRecipeInfo(
    @Json(name="id")
    val id: Int,
    @Json(name="servings")
    val servings: Int,
    @Json(name="readyInMinutes")
    val readyInMinutes: Int,
    @Json(name="sourceName")
    val sourceName: String,
    @Json(name="sourceUrl")
    val sourceUrl: String,
    @Json(name="summary")
    val summary: String,
    @Json(name="extendedIngredients")
    val ingredients: List<SpoonacularIngredient>,
    @Json(name="instructions")
    val instructions: String,
    @Json(name="analyzedInstructions")
    val analyzedInstructions: List<SpoonacularSteps>
): Serializable