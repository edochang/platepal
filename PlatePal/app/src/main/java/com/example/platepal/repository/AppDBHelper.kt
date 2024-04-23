package com.example.platepal.repository

import android.util.Log
import com.example.platepal.data.AppMeta
import com.example.platepal.data.PostMeta

class AppDBHelper: DBHelper<AppMeta>(
    "ApplicationMeta"
) {
    companion object {
        const val TAG = "ApplicationMeta"
    }

    override val limit: Long = 1L

    fun getOrCreateAppMeta(resultListener: (AppMeta?) -> Unit) {
        val query = db.collection(rootCollection)

        super.getAndLimitDocuments(query, AppMeta::class.java) {
            if (it.isNotEmpty()) {
                resultListener(it.first())
            } else {
                val appMeta = AppMeta(
                    0L,
                    0
                )

                createDocument(appMeta) { id ->
                    super.getAndLimitDocuments(query, AppMeta::class.java) { appMetaDoc ->
                        resultListener(appMetaDoc.first())
                    }
                }
            }
        }
    }

    fun realTimeReadAppMeta(resultListener: (AppMeta) -> Unit) {
        val query = db.collection(rootCollection)
        query.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                Log.d(TAG, "Current documents size: ${snapshot.size()}")
                snapshot.documents.first().toObject(AppMeta::class.java)?.let { resultListener(it) }
            } else {
                Log.d(TAG, "Current documents: null")
            }
        }
    }
}