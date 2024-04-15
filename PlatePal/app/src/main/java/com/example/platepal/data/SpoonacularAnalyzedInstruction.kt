package com.example.platepal.data

import com.squareup.moshi.Json
import java.io.Serializable

data class SpoonacularAnalyzedInstruction(
    @Json(name="number")
    val number: Int,
    @Json(name="step")
    val step: String
): Serializable