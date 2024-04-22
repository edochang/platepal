package com.example.platepal.repository

import com.example.platepal.data.RecipeInfoMeta
import com.example.platepal.data.RecipeMeta

class RecipeInfoDBHelper: DBHelper<RecipeInfoMeta>(
    "RecipeInfo"
) {
    fun getRecipeInfo(sourceId: String, resultListener: (List<RecipeInfoMeta>) -> Unit) {
        val query = db.collection(rootCollection)
            .whereEqualTo("sourceId", sourceId)
        getDocuments(query, RecipeInfoMeta::class.java, resultListener)
    }

    fun createAndRetrieveDocument(
        meta: RecipeInfoMeta,
        resultListener: (List<RecipeInfoMeta>) -> Unit
    ) {
        super.createDocument(meta) {}
        getRecipeInfo(meta.sourceId, resultListener)
    }
}