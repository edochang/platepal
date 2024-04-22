package com.example.platepal.data

import com.squareup.moshi.Json
import java.io.Serializable

data class GeminiPart(
    @Json(name="text")
    val text: String
): Serializable