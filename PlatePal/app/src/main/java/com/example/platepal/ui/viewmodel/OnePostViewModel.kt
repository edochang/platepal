package com.example.platepal.ui.viewmodel

import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import com.example.platepal.data.PostMeta
import com.example.platepal.data.RecipeMeta
import com.example.platepal.data.StorageDirectory
import com.example.platepal.repository.PostsDBHelper
import com.example.platepal.repository.Storage
import com.example.platepal.glide.Glide
import java.io.File

class OnePostViewModel : ViewModel() {
    companion object {
        const val TAG = "OnePostViewModel"
    }

    // DBHelpers
    private val postsDBHelper = PostsDBHelper()

    // Repositories
    private val storage = Storage()

    // Photo Metadata
    var pictureNameByUser = "" // String provided by the user
    private var pictureUUID = ""
    private var photoFile: File? = null

    var recipeMeta: RecipeMeta? = null

    // Getter
    fun getPictureUUID(): String {
        return pictureUUID
    }

    fun getPhotoFile(): File? {
        return photoFile
    }

    // Setter
    fun setPhotoFile(file: File) {
        Log.d(TAG, "photoFile set and exists ($file.exists()}: $file")
        photoFile = file
    }

    fun setPictureUUID(uuid: String) {
        pictureUUID = uuid
    }

    // Public functions
    fun deletePicture() {
        storage.deleteImage(pictureUUID, StorageDirectory.POST)
        pictureReset()
    }

    fun fetchPostPhoto(image: String, imageView: ImageView) {
        Glide.fetchFromStorage(
            storage.uuid2StorageReference(image, StorageDirectory.POST),
            imageView
        )
    }

    fun fetchLocalPostPhoto(imageView: ImageView) {
        photoFile?.let {
            Log.d(TAG, "Glide Local Fetch")
            Glide.fetchFromLocal(it, imageView)
        }
    }

    fun savePost(postMeta: PostMeta) {
        val pFile = photoFile
        pFile?.let {
            Log.d(javaClass.simpleName, "photoFile name: ${pFile.nameWithoutExtension}")
            storage.uploadImage(pFile, pFile.nameWithoutExtension, StorageDirectory.POST) {
                if (it > 0L) {
                    Log.d(TAG, "sizeBytes returned: $it")
                    postsDBHelper.createDocument(postMeta) { }
                } else {
                    Log.d(TAG, "Failed to upload image!")
                }
                pictureReset()
            }
        }
    }

    fun pictureReplace() {
        photoFile?.let {
            if (it.delete()) {
                Log.d(javaClass.simpleName, "Local file deleted for replacement.")
                photoFile = null
            } else {
                Log.d(javaClass.simpleName, "Local file delete FAILED for replacement.")
            }
        }
    }

    fun pictureReset() {
        pictureUUID = ""
        pictureNameByUser = ""
        photoFile?.let {
            if (it.delete()) {
                Log.d(javaClass.simpleName, "Local file deleted.")
                photoFile = null
            } else {
                Log.d(javaClass.simpleName, "Local file delete FAILED")
            }
        }
        recipeMeta = null
    }

    // Private helper functions
}