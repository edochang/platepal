package com.example.platepal.repository

import android.net.Uri
import android.util.Log
import com.example.platepal.data.StorageDirectory
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import java.io.File

class Storage {
    private val recipePhotoStorage: StorageReference =
        FirebaseStorage.getInstance().reference.child("recipe_img")
    private val postPhotoStorage: StorageReference =
        FirebaseStorage.getInstance().reference.child("post_img")

    fun uploadImage(localFile: File, uuid: String, directory: StorageDirectory, uploadSuccess:(Long)->Unit) {
        Log.d(javaClass.simpleName, ">>>>>>>> Uploading Image!")
        val metadata = StorageMetadata.Builder()
            .setContentType("image/jpg")
            .build()

        val file = Uri.fromFile(localFile)
        val imageRef = when(directory) {
            StorageDirectory.RECIPE -> recipePhotoStorage.child(uuid)
            StorageDirectory.POST -> postPhotoStorage.child(uuid)
        }

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

    fun deleteImage(pictureUUID: String, directory: StorageDirectory) {
        val imageRef = uuid2StorageReference(pictureUUID, directory)
        Log.d(javaClass.simpleName, "Image Path to be removed: ${imageRef.path}")
        imageRef.delete()
            .addOnSuccessListener {
                Log.d(javaClass.simpleName, "$pictureUUID has been successfully deleted!")
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "$pictureUUID has failed to be deleted!")
            }
    }

    fun uuid2StorageReference(uuid: String, directory: StorageDirectory): StorageReference {
        return when(directory) {
            StorageDirectory.RECIPE -> recipePhotoStorage.child(uuid)
            StorageDirectory.POST -> postPhotoStorage.child(uuid)
        }
    }



    // For user profile
    private val profilePhotoStorage: StorageReference =
        FirebaseStorage.getInstance().reference.child("profile_img")

    fun uploadProfileImage(localFile: File, uuid: String, uploadSuccess:()->Unit) {
        Log.d(javaClass.simpleName, ">>>>>>>> Uploading Image!")
        val metadata = StorageMetadata.Builder()
            .setContentType("image/jpg")
            .build()

        val file = Uri.fromFile(localFile)
        val imageRef = profilePhotoStorage.child(uuid)

        val uploadTask = imageRef.putFile(file, metadata)

        // Register observers to listen for when the download is done or if it fails
        uploadTask
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "Storage.uploadProfileIMage Upload FAILED $uuid")
            }
            .addOnSuccessListener {
                Log.d(javaClass.simpleName, "Storage.uploadProfileIMage Upload succeeded $uuid")
                uploadSuccess()
            }
    }

    fun deleteProfileImage(pictureUUID: String) {
        val imageRef = uuid2StorageReferenceProfile(pictureUUID)
        Log.d(javaClass.simpleName, "Image Path to be removed: ${imageRef.path}")
        imageRef.delete()
            .addOnSuccessListener {
                Log.d(javaClass.simpleName, "$pictureUUID has been successfully deleted!")
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "$pictureUUID has failed to be deleted!")
            }
    }

    fun uuid2StorageReferenceProfile(uuid: String): StorageReference {
        return profilePhotoStorage.child(uuid)
    }


}