package com.example.platepal.ui.viewmodel

import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.platepal.camera.TakePictureWrapper
import com.example.platepal.data.RecipeMeta
import com.example.platepal.data.UserMeta
import com.example.platepal.repository.DBHelper
import com.example.platepal.repository.Storage
import com.example.platepal.repository.UserDBHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.cs371m.reddit.glide.Glide
import java.io.File

private const val TAG = "UserViewModel"

class UserViewModel: ViewModel() {

    private val dbHelper = UserDBHelper()
    private var dbFavList = MutableLiveData<List<RecipeMeta>>().apply{
        postValue(mutableListOf())
    }

    // Profile Photo
    private val storage = Storage()
    private var profilePhotoUUID = ""
    private var profilePhotoFile: File? = null

    //for user profile picture
    // Getter
    fun getProfilePhotoUUID(): String{
        return profilePhotoUUID
    }

    fun getProfilePhotoFile(): File? {
        return profilePhotoFile
    }

    // Setter
    fun setProfilePhotoFile(file: File) {
        profilePhotoFile = file
        Log.d(TAG, "profile picture file set and exists (${file.exists()}: $file")
    }
    fun setProfilePhotoUUID(uuid: String) {
        profilePhotoUUID = uuid
    }

    fun fetchProfilePhoto(uuid: String, imageView: ImageView) {
        Glide.fetchFromStorage(storage.uuid2StorageReferenceProfile(uuid), imageView)
        Log.d(TAG, "fetch STORAGE profile...bind using glide")
    }

    fun fetchLocalProfilePhoto(imageView: ImageView) {
        profilePhotoFile?.let {
            Glide.fetchFromLocal(it, imageView)
        }
        Log.d(TAG, "fetch LOCAL profile...bind using glide")
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

    //old
    fun profilePhotoSuccess() {
        profilePhotoFile?.let{
            storage.uploadProfileImage(it, profilePhotoUUID) {
                //profilePhotoUUID = ""
                Log.d(TAG, "profilePhotoSuccess - profile photo upload to storage")
            }
        }

        /*
        val photoFile = TakePictureWrapper.fileNameToFile(profilePhotoUUID)
        storage.uploadProfileImage(photoFile, profilePhotoUUID) {
            //profilePhotoUUID = ""
            Log.d(TAG, "profile photo upload to storage successfully")
        }

         */
    }
    fun profilePhotoFailure() {
        // Note, the camera intent will only create the file if the user hits accept
        // so I've never seen this called
        profilePhotoUUID = ""
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