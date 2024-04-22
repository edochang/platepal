package com.example.platepal.data

import com.squareup.moshi.Json
import java.io.Serializable

data class GeminiParts(
    @Json(name="parts")
    val parts: List<GeminiPart>
): Serializable