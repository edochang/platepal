package com.example.platepal.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.platepal.data.PostMeta
import com.example.platepal.data.RecipeMeta
import com.example.platepal.data.UserMeta
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class UserDBHelper : DBHelper<UserMeta>(
    rootCollection = "Users"
) {
    companion object {
        const val TAG = "UserDBHelper"
    }

    //private val user = FirebaseAuth.getInstance().currentUser
    private val subCollection = "FavRecipes"
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val userID = currentUser?.uid

    fun createUser(user: UserMeta) {
        db.collection(rootCollection)
            .add(user)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written with ID: $it.id")
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document.", e) }
    }

    private fun getCurrentUserDocID(resultListener: (id: String) -> Unit) {
        db.collection(rootCollection)
            .whereEqualTo("uid", userID)
            .get()
            .addOnSuccessListener { result ->
                Log.d(TAG, "doc id retrieval success: ${result.documents[0].id}")
                resultListener(result.documents[0].id)
            }
            .addOnFailureListener {
                Log.d(TAG, "doc id retrieval failed")
                resultListener("")
            }
    }

    private fun dbFetchFavRecipes(callback: (list: List<RecipeMeta>) -> Unit) {
        getCurrentUserDocID { userDocID ->
            db.collection(rootCollection)
                .document(userDocID)
                .collection(subCollection)
                .get()
                .addOnSuccessListener { result ->
                    Log.d(TAG, "all favorite recipes fetch ${result!!.documents.size}")
                    // done on a background thread
                    callback(result.documents.mapNotNull {
                        it.toObject(RecipeMeta::class.java)
                    })
                }
                .addOnFailureListener {
                    Log.d(TAG, "all favorite recipes fetc FAILED ", it)
                }
        }
    }

    fun fetchInitialFavRecipes(callback: (list: List<RecipeMeta>) -> Unit) {
        dbFetchFavRecipes(callback)
    }

    fun addFavRecipe(
        recipe: RecipeMeta,
        callback: (list: List<RecipeMeta>) -> Unit
    ) {
        getCurrentUserDocID { userDocID ->
            db.collection(rootCollection)
                .document(userDocID)
                .collection(subCollection)
                .document(recipe.firestoreId)
                .set(recipe)
                .addOnSuccessListener {
                    Log.d(
                        TAG, "recipe added with doc id: ${recipe.firestoreId}"
                    )
                    dbFetchFavRecipes(callback)
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "Recipe addition FAILED")
                    Log.w(TAG, "Error ", e)
                }
        }
    }


    fun removeFavRecipe(
        recipe: RecipeMeta,
        callback: (list: List<RecipeMeta>) -> Unit
    ) {
        getCurrentUserDocID { userDocID ->
            db.collection(rootCollection)
                .document(userDocID)
                .collection(subCollection)
                .document(recipe.firestoreId)
                .delete()

                .addOnSuccessListener {
                    Log.d(
                        TAG,
                        "Recipe deleted sucessfully id: ${recipe.firestoreId}"
                    )
                    dbFetchFavRecipes(callback)
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "Recipe deleting FAILED")
                    Log.w(TAG, "Error ", e)
                }
        }
    }

    fun getUserMetaDocuments(
        resultListener: (UserMeta) -> Unit
    ) {
        val query = db.collection(rootCollection)
            .whereEqualTo("uid", userID)
        super.getDocuments(query, UserMeta::class.java) {
            resultListener(it.first())
        }
    }

    fun realTimeReadUserMeta(resultListener: (List<UserMeta>) -> Unit) {
        val query = db.collection(rootCollection)
        query.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                Log.d(TAG, "Current documents size: ${snapshot.size()}")
                resultListener(snapshot.documents.mapNotNull {
                    it.toObject(UserMeta::class.java)
                })
            } else {
                Log.d(TAG, "Current documents: null")
            }
        }
    }
}