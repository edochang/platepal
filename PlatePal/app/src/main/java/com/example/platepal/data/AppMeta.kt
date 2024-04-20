package com.example.platepal.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable

data class AppMeta(
    var lastSpoonacularSearchApiCallTimestampSec: Long = 0L,
    var lastSpoonacularSearchApiCallTimestampNanosec: Int = 0,
    @ServerTimestamp
    val timeStamp: Timestamp? = null,
    @DocumentId
    var firestoreId: String = ""
): Serializable { }