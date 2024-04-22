package com.example.platepal.data

import com.squareup.moshi.Json
import java.io.Serializable

data class GeminiContent(
    @Json(name="contents")
    val contents: GeminiParts,
    @Json(name="role")
    val role: String?
): Serializable