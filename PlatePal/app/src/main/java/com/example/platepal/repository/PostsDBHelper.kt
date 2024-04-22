package com.example.platepal.repository

import android.util.Log
import com.example.platepal.data.PostMeta
import com.example.platepal.data.RecipeMeta
import com.google.firebase.firestore.Query

class PostsDBHelper: DBHelper<PostMeta>(
    "Posts"
) {
    companion object {
        const val TAG = "PostsDBHelper"
    }
    fun realTimeReadPosts(resultListener: (List<PostMeta>) -> Unit) {
        val query = db.collection(rootCollection)
            .orderBy("timeStamp", Query.Direction.DESCENDING)
        query.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                Log.d(TAG, "Current documents size: ${snapshot.size()}")
                resultListener(snapshot.documents.mapNotNull {
                    it.toObject(PostMeta::class.java)
                })
            } else {
                Log.d(TAG, "Current documents: null")
            }
        }
    }
}