package com.example.platepal.ui.viewmodel

import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.platepal.data.RecipeMeta
import com.example.platepal.data.UserMeta
import com.example.platepal.repository.Storage
import com.example.platepal.repository.UserDBHelper
import com.google.firebase.auth.FirebaseAuth
import com.example.platepal.glide.Glide
import java.io.File

class UserViewModel : ViewModel() {
    companion object {
        const val TAG = "UserViewModel"
    }

    private val dbHelper = UserDBHelper()
    private var dbFavList = MutableLiveData<List<RecipeMeta>>().apply {
        postValue(mutableListOf())
    }

    // Profile Photo
    private val storage = Storage()
    private var profilePhotoUUID = ""
    private var profilePhotoFile: File? = null
    private var previousUUID = ""

    // User Meta
    var userMeta: UserMeta? = null
    var userMetaList = mutableListOf<UserMeta>().apply {
        dbHelper.realTimeReadUserMeta {
            this.addAll((it.toSet() - this.toSet()).toMutableList())
            Log.d(TAG, "this userMetaList: $this")
            //Log.d(TAG, "it userMetaList: $it")
        }
    }

    // Getter
    fun getAuthDisplayName(): String {
        val displayName = FirebaseAuth.getInstance().currentUser?.displayName ?: ""
        Log.d(TAG, "User Display Name: $displayName")
        return displayName
    }

    fun getAuthEmail(): String {
        return FirebaseAuth.getInstance().currentUser?.email ?: ""
    }

    fun getAuthUUID(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: ""
    }


    //for user profile picture
    fun getProfilePhotoUUID(): String {
        return profilePhotoUUID
    }

    fun getProfilePhotoFile(): File? {
        return profilePhotoFile
    }

    fun getPreviousUUID(): String {
        return previousUUID
    }

    // Setter
    fun setProfilePhotoFile(file: File) {
        profilePhotoFile = file
        Log.d(TAG, "profile picture file set and exists (${file.exists()}: $file")
    }

    fun setProfilePhotoUUID(uuid: String) {
        profilePhotoUUID = uuid
    }

    fun setPreviousUUID(uuid: String) {
        previousUUID = uuid
        Log.d(TAG, "previous uuid has been set to $uuid")
    }

    fun resetPreviousUUID() {
        previousUUID = ""
        Log.d(TAG, "previous uuid has been reset")
    }

    fun fetchUserMeta(uuid: String) {
        dbHelper.getUserMetaDocuments {
            userMeta = it
        }
    }

    fun fetchProfilePhoto(uuid: String, imageView: ImageView) {
        Glide.fetchFromStorageForProfile(storage.uuid2StorageReferenceProfile(uuid), imageView)
        Log.d(TAG, "fetch STORAGE profile...bind using glide")
    }

    fun fetchLocalProfilePhoto(imageView: ImageView) {
        profilePhotoFile?.let {
            Glide.fetchFromLocalForProfile(it, imageView)
        }
        Log.d(TAG, "fetch LOCAL profile...bind using glide")
    }

    fun pictureReplace() {
        profilePhotoFile?.let {
            if (it.delete()) {
                Log.d(javaClass.simpleName, "Local file deleted for replacement.")
                profilePhotoFile = null
            } else {
                Log.d(javaClass.simpleName, "Local file delete FAILED for replacement.")
            }
        }
    }

    fun pictureReset() {
        profilePhotoUUID = ""
        profilePhotoFile?.let {
            if (it.delete()) {
                Log.d(javaClass.simpleName, "Local file deleted.")
                profilePhotoFile = null
            } else {
                Log.d(javaClass.simpleName, "Local file delete FAILED")
            }
        }
    }

    fun profilePhotoSuccess() {
        profilePhotoFile?.let {
            storage.uploadProfileImage(it, profilePhotoUUID) {
                //profilePhotoUUID = ""
                Log.d(TAG, "profilePhotoSuccess - profile photo upload to storage")
            }
        }

    }

    fun deletePreviousProfile(previousUUID: String) {
        storage.deleteProfileImage(previousUUID)
    }

    //create new user
    fun createUserMeta(name: String, email: String, uid: String) {
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
    fun setFavoriteRecipe(recipe: RecipeMeta, isFavorite: Boolean) {
        if (isFavorite) addFavRecipe(recipe)
        else removeFavRecipe(recipe)
    }

    fun isFavoriteRecipe(recipe: RecipeMeta): Boolean? {
        return dbFavList.value?.contains(recipe)
    }

    fun fetchInitialFavRecipes(callback: () -> Unit) {
        dbHelper.fetchInitialFavRecipes {
            dbFavList.postValue(it)
            callback.invoke()
            Log.d(TAG, "dbFavList postvalue")
        }
    }

    fun observeDbFavList(): LiveData<List<RecipeMeta>> {
        return dbFavList
    }

    private fun addFavRecipe(recipe: RecipeMeta) {
        dbHelper.addFavRecipe(recipe) {
            dbFavList.postValue(it)
        }
    }

    private fun removeFavRecipe(recipe: RecipeMeta) {
        dbHelper.removeFavRecipe(recipe) {
            dbFavList.postValue(it)
        }
    }
}