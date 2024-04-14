package com.example.platepal.data

import com.squareup.moshi.Json
import java.io.Serializable

data class SpoonacularIngredient(
    @Json(name="id")
    val id: Int,
    @Json(name="original")
    val ingredient: String
): Serializable