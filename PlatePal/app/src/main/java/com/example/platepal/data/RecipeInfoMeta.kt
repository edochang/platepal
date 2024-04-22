package com.example.platepal.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable

/* Data class that represents the RecipeMeta Document Collection in Firestore
 */
data class RecipeInfoMeta (
    var sourceId: String = "",
    var ingredients: String = "",
    var ingredientList: List<Any> = emptyList(),
    var directions: String = "",
    var notes: String = "",
    var createdBy: String = "",
    @ServerTimestamp
    val timeStamp: Timestamp? = null,
    @DocumentId
    var firestoreId: String = ""
): Serializable