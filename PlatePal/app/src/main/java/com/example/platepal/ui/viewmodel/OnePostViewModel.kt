package com.example.platepal.ui.viewmodel

import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import com.example.platepal.camera.TakePictureWrapper
import com.example.platepal.repository.Storage
import edu.cs371m.reddit.glide.Glide

private const val TAG = "OnePostViewModel"

class OnePostViewModel: ViewModel() {
    // DBHelpers


    // Repositories
    private val storage = Storage()

    // Photo Metadata
    var pictureNameByUser = "" // String provided by the user
    private var pictureUUID = ""
    private var pictureSize = 0L

    // Getter
    fun getPictureUUID(): String {
        return pictureUUID
    }

    // Public helper functions
    fun deletePicture() {
        storage.deleteImage(pictureUUID)
        pictureReset()
    }

    fun fetchPostPhoto(image: String, imageView: ImageView) {
        Glide.fetchFromStorage(storage.uuid2StorageReference(image), imageView)
    }

    fun pictureSuccess(resultListener: (size: Long) -> Unit) {
        val photoFile = TakePictureWrapper.fileNameToFile(pictureUUID)
        // XXX Write me while preserving referential integrity
        val pictureName = pictureNameByUser
        Log.d(javaClass.simpleName, "photoFile name: ${photoFile.nameWithoutExtension}")
        storage.uploadImage(photoFile, photoFile.nameWithoutExtension) {
            if (it > 0L) {
                Log.d(TAG, "sizeBytes returned: $it")
                pictureSize = it
                resultListener(it)
            } else {
                Log.d(TAG, "Failed to upload image!")
            }
        }
    }

    fun pictureReset() {
        pictureUUID = ""
        pictureNameByUser = ""
        pictureSize = 0L
    }

    fun takePictureUUID(uuid: String) {
        pictureUUID = uuid
    }

    // Private helper functions
}