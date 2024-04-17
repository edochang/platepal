package com.example.platepal.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import java.io.File

class Storage {
    private val recipePhotoStorage: StorageReference =
        FirebaseStorage.getInstance().reference.child("recipe_img")

    fun uploadImage(localFile: File, uuid: String, uploadSuccess:(Long)->Unit) {
        Log.d(javaClass.simpleName, ">>>>>>>> Uploading Image!")
        val metadata = StorageMetadata.Builder()
            .setContentType("image/jpg")
            .build()

        val file = Uri.fromFile(localFile)
        val imageRef = recipePhotoStorage.child(uuid)

        val uploadTask = imageRef.putFile(file, metadata)

        // Register observers to listen for when the download is done or if it fails
        uploadTask
            .addOnFailureListener {
                // Handle unsuccessful uploads
                if (localFile.delete()) {
                    Log.d(javaClass.simpleName, "Upload FAILED $uuid, file deleted")
                } else {
                    Log.d(javaClass.simpleName, "Upload FAILED $uuid, file delete FAILED")
                }
                uploadSuccess(-1L)
            }
            .addOnSuccessListener {
                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                if (localFile.delete()) {
                    Log.d(javaClass.simpleName, "Upload succeeded $uuid, file deleted")
                } else {
                    Log.d(javaClass.simpleName, "Upload succeeded $uuid, file delete FAILED")
                }
                val sizeBytes = it.metadata?.sizeBytes ?: -1L
                uploadSuccess(sizeBytes)
            }
    }

    fun deleteImage(pictureUUID: String) {
        val imageRef = uuid2StorageReference(pictureUUID)
        Log.d(javaClass.simpleName, "Image Path to be removed: ${imageRef.path}")
        imageRef.delete()
            .addOnSuccessListener {
                Log.d(javaClass.simpleName, "$pictureUUID has been successfully deleted!")
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "$pictureUUID has failed to be deleted!")
            }
    }

    fun uuid2StorageReference(uuid: String): StorageReference {
        return recipePhotoStorage.child(uuid)
    }
}