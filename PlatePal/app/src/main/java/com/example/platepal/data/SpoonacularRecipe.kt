package com.example.platepal.data

import com.squareup.moshi.Json

/* The data class representing the Recipe data object provided by the Spoonacular Api:
    https://spoonacular.com/food-api/docs#Search-Recipes-Complex
 */
data class SpoonacularRecipe(
    @Json(name="id")
    val id: Int,
    @Json(name="title")
    val title: String,
    @Json(name="image")
    val image: String,
    @Json(name="imageType")
    val imageType: String
)