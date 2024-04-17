package com.example.platepal.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.platepal.data.RecipeMeta
import com.example.platepal.data.UserMeta
import com.example.platepal.repository.DBHelper
import com.example.platepal.repository.UserDBHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private const val TAG = "UserViewModel"

class UserViewModel: ViewModel() {

    private val dbHelper = UserDBHelper()
    private var dbFavList = MutableLiveData<List<RecipeMeta>>().apply{
        //postValue(mutableListOf())
    }

    //create new user
    fun createUserMeta(name: String, email: String, uid: String){
        val userMeta = UserMeta(
            fullName = name,
            email = email,
            uid = uid,
        )
        dbHelper.createUser(userMeta)
        Log.d(TAG, "user created successfully")
    }


    //Managing User Favorites
    // getter - favorite
    fun getFavList(): MutableList<RecipeMeta>? {
        return dbFavList.value?.toMutableList()
    }

    //setters -favorite, using db
    fun setFavoriteRecipe(recipe: RecipeMeta, isFavorite: Boolean){
        if (isFavorite) addFavRecipe(recipe)
        else removeFavRecipe(recipe)
    }

    fun isFavoriteRecipe(recipe: RecipeMeta): Boolean? {
        return dbFavList.value?.contains(recipe)
    }

    fun fetchInitialFavRecipes(callback: ()-> Unit){
        dbHelper.fetchInitialFavRecipes {
            dbFavList.postValue(it)
            callback.invoke()
            Log.d(TAG, "dbFavList postvalue")
        }
    }

    fun observeDbFavList(): LiveData<List<RecipeMeta>>{
        return dbFavList
    }

    private fun addFavRecipe(recipe: RecipeMeta) {
        dbHelper.addFavRecipe(recipe){
            dbFavList.postValue(it)
        }
    }

    private fun removeFavRecipe(recipe: RecipeMeta) {
        dbHelper.removeFavRecipe(recipe){
            dbFavList.postValue(it)
        }
    }
}