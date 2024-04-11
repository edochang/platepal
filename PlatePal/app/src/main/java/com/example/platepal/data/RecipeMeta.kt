package com.example.platepal.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/* Data class that represents the PhotoMeta Document Collection in Firestore
    title: title of Recipe
    externalId: external Id of this recipe.  If sourced internally by user this attribute can be
        blank
    timeStamp: Written on the Firestore server
    firestorageId: firestoreId is generated by Firestore, used as primary key
 */
data class RecipeMeta (
    var id: String = "",
    var title: String = "",
    @ServerTimestamp
    val timeStamp: Timestamp? = null,
    @DocumentId
    var firestoreId: String = ""
)