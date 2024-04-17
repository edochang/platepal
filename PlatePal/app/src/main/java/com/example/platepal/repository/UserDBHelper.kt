package com.example.platepal.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.platepal.data.RecipeMeta
import com.example.platepal.data.UserMeta
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

private const val TAG = "UserDBHelper"

class UserDBHelper: DBHelper<UserMeta>(
    rootCollection = "Users"
){
    //private val user = FirebaseAuth.getInstance().currentUser
    private val subCollection = "FavRecipes"
    private val userID = FirebaseAuth.getInstance().currentUser?.uid

    fun createUser(user: UserMeta) {
        db.collection(rootCollection)
            .add(user)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written with ID: $it.id")
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document.", e) }
    }

    private fun getCurrentUserDocID(resultListener: (id: String)->Unit){
        db.collection(rootCollection)
            .whereEqualTo("uid", userID)
            .get()
            .addOnSuccessListener {result->
                Log.d(TAG, "doc id retrieval success: ${result.documents[0].id}")
                resultListener(result.documents[0].id)
            }
            .addOnFailureListener {
                Log.d(TAG, "doc id retrieval failed")
                resultListener("")
            }
    }

    private fun dbFetchFavRecipes(favList: MutableLiveData<List<RecipeMeta>>){
        getCurrentUserDocID { userDocID ->
            db.collection(rootCollection)
                .document(userDocID)
                .collection(subCollection)
                .get()
                .addOnSuccessListener { result ->
                    Log.d(TAG, "all favorite recipes fetch ${result!!.documents.size}")
                    // done on a background thread
                    favList.postValue(result.documents.mapNotNull {
                        it.toObject(RecipeMeta::class.java)
                    })
                }
                .addOnFailureListener {
                    Log.d(TAG, "all favorite recipes fetc FAILED ", it)
                }
        }
    }

    fun fetchInitialFavRecipes(favList: MutableLiveData<List<RecipeMeta>>) {
        dbFetchFavRecipes(favList)
    }

    fun addFavRecipe(recipe: RecipeMeta,
                     favList: MutableLiveData<List<RecipeMeta>>) {
        getCurrentUserDocID {userDocID ->
            db.collection(rootCollection)
                .document(userDocID)
                .collection(subCollection)
                .document(recipe.firestoreId)
                .set(recipe)
                .addOnSuccessListener {
                    Log.d(TAG, "recipe added with doc id: ${recipe.firestoreId}"
                    )
                    dbFetchFavRecipes(favList)
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "Recipe addition FAILED")
                    Log.w(TAG, "Error ", e)
                }
        }
    }


    fun removeFavRecipe(recipe: RecipeMeta,
                        favList: MutableLiveData<List<RecipeMeta>>){
        getCurrentUserDocID { userDocID ->
            db.collection(rootCollection)
                .document(userDocID)
                .collection(subCollection)
                .document(recipe.firestoreId)
                .delete()

                .addOnSuccessListener {
                    Log.d(TAG,
                        "Recipe deleted sucessfully id: ${recipe.firestoreId}"
                    )
                    dbFetchFavRecipes(favList)
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "Recipe deleting FAILED")
                    Log.w(TAG, "Error ", e)
                }
        }
    }


}