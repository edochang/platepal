package com.example.platepal.repository

import com.example.platepal.api.GeminiApi
import com.example.platepal.data.GeminiContent
import com.example.platepal.data.GeminiPart
import com.example.platepal.data.GeminiParts
import com.example.platepal.data.GeminiRepoRecipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

class GeminiRepository(
    private val geminiApi: GeminiApi
) {
    suspend fun getPlatePalRecipe(recipeName: String): GeminiRepoRecipe {
        val body = GeminiContent(
            GeminiParts(
                listOf(
                    GeminiPart(
                        "Give me a recipe for $recipeName.  your response must " +
                                "contain and be formatted with a list of ingredients, a list of " +
                                "instructions, and a summary of the recipe"
                    )
                )
            ),
            null
        )

        return withContext(Dispatchers.IO) {
            val contents = geminiApi.requestAIRecipe(body)
            val recipe = GeminiRepoRecipe(
                "",
                "",
                "",
                null
            )
            return@withContext recipe
        }
    }
}