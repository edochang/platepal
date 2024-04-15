package com.example.platepal.data

import com.squareup.moshi.Json
import java.io.Serializable

data class SpoonacularSteps(
    @Json(name="steps")
    val steps: List<SpoonacularAnalyzedInstruction>,
): Serializable