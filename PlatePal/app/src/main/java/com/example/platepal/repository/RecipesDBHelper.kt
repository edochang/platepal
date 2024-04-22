package com.example.platepal.repository


import com.example.platepal.data.RecipeMeta

class RecipesDBHelper : DBHelper<RecipeMeta>(
    "Recipes"
) {
    companion object {
        const val TAG = "RecipesDBHelper"
    }

    fun getRecipes(resultListener: (List<RecipeMeta>) -> Unit) {
        val query = db.collection(rootCollection).whereEqualTo("imageType", "jpg")
        getDocuments(query, RecipeMeta::class.java, resultListener)
    }

    fun getUserCreatedRecipes(resultListener: (List<RecipeMeta>) -> Unit) {
        val query = db.collection(rootCollection).whereEqualTo("imageType", "image/jpg")
        getDocuments(query, RecipeMeta::class.java, resultListener)
    }

    fun getAllRecipes(resultListener: (List<RecipeMeta>) -> Unit) {
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