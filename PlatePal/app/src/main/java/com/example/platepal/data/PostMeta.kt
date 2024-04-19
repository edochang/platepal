package com.example.platepal.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable

data class PostMeta (
    var recipeSourceId: String = "",
    var recipeTitle: String = "",
    var postMessage: String = "",
    var pictureLabel: String = "",
    var picture: String = "",
    var pictureType: String = "",
    var createdBy: String = "",
    @ServerTimestamp
    val timeStamp: Timestamp? = null,
    @DocumentId
    var firestoreId: String = ""
): Serializable {
    override fun equals(other: Any?) : Boolean =
        if (other is PostMeta) {
            firestoreId == other.firestoreId
        } else {
            false
        }

    override fun hashCode(): Int {
        var result = firestoreId.hashCode()
        result = 31 * result + recipeSourceId.hashCode()
        result = 31 * result + recipeTitle.hashCode()
        result = 31 * result + postMessage.hashCode()
        result = 31 * result + pictureLabel.hashCode()
        result = 31 * result + picture.hashCode()
        result = 31 * result + pictureType.hashCode()
        result = 31 * result + createdBy.hashCode()
        result = 31 * result + (timeStamp?.hashCode() ?: 0)
        return result
    }
}
