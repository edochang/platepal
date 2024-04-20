package com.example.platepal.data

import com.squareup.moshi.Json
import java.io.Serializable


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
): Serializable {
    override fun equals(other: Any?) : Boolean =
        if (other is SpoonacularRecipe) {
            id == other.id
        } else {
            false
        }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + image.hashCode()
        result = 31 * result + imageType.hashCode()
        return result
    }
}