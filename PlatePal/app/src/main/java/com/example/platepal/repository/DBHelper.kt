package com.example.platepal.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

private const val TAG = "DBHelper"

abstract class DBHelper<Any : kotlin.Any> (
        protected val rootCollection: String
) {
    protected val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    open val limit: Long = 100L

    open fun getDocuments(
        query: Query,
        data: Class<Any>,
        resultListener: (List<Any>) -> Unit
    ) {
        query
            .get()
            .addOnSuccessListener { result ->
                Log.d(TAG, "Documents fetch ${result!!.documents.size}")
                resultListener(result.documents.mapNotNull {
                    it.toObject(data)
                })
            }
            .addOnFailureListener {
                Log.d(TAG, "Documents fetch FAILED ", it)
                resultListener(listOf())
            }
    }

    open fun getAndLimitDocuments(
        query: Query,
        data: Class<Any>,
        resultListener: (List<Any>) -> Unit
    ) {
        query
            .limit(limit)
            .get()
            .addOnSuccessListener { result ->
                Log.d(TAG, "Documents fetch ${result!!.documents.size}")
                resultListener(result.documents.mapNotNull {
                    it.toObject(data)
                })
            }
            .addOnFailureListener {
                Log.w(TAG, "Documents fetch FAILED ", it)
                resultListener(listOf())
            }
    }

    open fun createDocument(
        meta: Any,
        resultListener: (id: String)->Unit
    ) {
        db.collection(rootCollection)
            .add(meta)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written with ID: ${it.id}")
                resultListener(it.id)
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document.", e) }
    }

    open fun updateDocument(
        documentId: String,
        data: Map<String, kotlin.Any>,
        resultListener: () -> Unit
    ) {
        db.collection(rootCollection).document(documentId).update(data)
            .addOnSuccessListener {
                Log.d(TAG, "Document successfully updated with ID: $documentId")
                resultListener()
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document ($documentId).", e) }
    }

    open fun removeDocument(
        documentId: String,
        resultListener: (List<Class<Any>>)->Unit
    ) {
        db.collection(rootCollection).document(documentId)
            .delete()
            .addOnSuccessListener {
                Log.d(
                    TAG,
                    "DocumentSnapshot($documentId) successfully deleted."
                )
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document: $documentId", e) }
    }

    open fun batchCreateDocuments(data: List<kotlin.Any>, resultListener: () -> Unit) {
        val batch = db.batch()
        Log.d(TAG, "batch data.size: ${data.size}")
        data.forEach {
            val documentRef = db.collection(rootCollection).document()
            batch.set(documentRef, it)
        }

        batch.commit()
            .addOnSuccessListener {
                Log.d(TAG, "Batch documents successfully created.")
                resultListener.invoke()
            }
            .addOnFailureListener {
                e ->
                Log.w(TAG, "Error batch documents failed to be created.", e)
            }
    }
}