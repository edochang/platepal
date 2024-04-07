package com.example.platepal.api

import com.squareup.moshi.Json

data class SpoonacularRecipe(
    @Json(name="id")
    val id: String,
    @Json(name="title")
    val title: String,
    @Json(name="image")
    val image: String,
    @Json(name="imageType")
    val imageType: String
)