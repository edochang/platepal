package com.example.platepal.repository

import com.example.platepal.data.RecipeInfoMeta
import com.example.platepal.data.RecipeMeta

private const val TAG = "RecipesDBHelper"

class RecipesDBHelper: DBHelper<RecipeMeta>(
    "Recipes"
) {
    fun getRecipes(resultListener: (List<RecipeMeta>) -> Unit) {
        val query = db.collection(rootCollection)
        getDocuments(query, RecipeMeta::class.java, resultListener)
    }

    fun getRecipe(documentId: String, resultListener: (List<RecipeMeta>) -> Unit) {
        val query = db.collection(rootCollection)
            .whereEqualTo("documentId", documentId)
        getDocuments(query, RecipeMeta::class.java, resultListener)
    }

    fun createAndRetrieveDocumentId(
        meta: RecipeMeta,
        resultListener: (id: String) -> Unit
    ) {
        super.createDocument(meta) { id ->
            resultListener(id)
        }
    }
}