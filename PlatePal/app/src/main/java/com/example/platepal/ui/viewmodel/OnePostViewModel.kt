package com.example.platepal.ui.viewmodel

import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import com.example.platepal.camera.TakePictureWrapper
import com.example.platepal.repository.Storage
import edu.cs371m.reddit.glide.Glide
import java.io.File

private const val TAG = "OnePostViewModel"

class OnePostViewModel: ViewModel() {
    // DBHelpers


    // Repositories
    private val storage = Storage()

    // Photo Metadata
    var pictureNameByUser = "" // String provided by the user
    private var pictureUUID = ""
    private var pictureSize = 0L
    private var photoFile: File? = null

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

    // Public helper functions
    fun deletePicture() {
        storage.deleteImage(pictureUUID)
        pictureReset()
    }

    fun fetchPostPhoto(image: String, imageView: ImageView) {
        Glide.fetchFromStorage(storage.uuid2StorageReference(image), imageView)
    }

    fun fetchLocalPostPhoto(imageView: ImageView) {
        photoFile?.let {
            Log.d(TAG, "Glide Local Fetch")
            Glide.fetchFromLocal(it, imageView)
        }
    }

    fun savePost(resultListener: (size: Long) -> Unit) {
        val pFile = photoFile
        pFile?.let {
            Log.d(javaClass.simpleName, "photoFile name: ${pFile.nameWithoutExtension}")
            storage.uploadImage(pFile, pFile.nameWithoutExtension) {
                if (it > 0L) {
                    Log.d(TAG, "sizeBytes returned: $it")
                    pictureSize = it
                    resultListener(it)
                } else {
                    Log.d(TAG, "Failed to upload image!")
                }
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
        pictureSize = 0L
        photoFile?.let {
            if (it.delete()) {
                Log.d(javaClass.simpleName, "Local file deleted.")
                photoFile = null
            } else {
                Log.d(javaClass.simpleName, "Local file delete FAILED")
            }
        }
    }

    // Private helper functions
}