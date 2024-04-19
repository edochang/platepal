package com.example.platepal.camera

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import com.example.platepal.ui.viewmodel.MainViewModel
import com.example.platepal.ui.viewmodel.UserViewModel
import java.io.File
import java.util.UUID

private const val TAG = "TakePictureWrapper"
class TakePictureWrapper {
    companion object {
        private fun generateFileName(): String {
            return UUID.randomUUID().toString()
        }
        fun fileNameToFile(uuid: String): File {
            // Create the File where the photo should go
            val storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES)
            // Annoyingly, file name must end with .jpg extension
            val localPhotoFile = File(storageDir, "${uuid}.jpg")
            Log.d(TAG, "Before Camera launch: photo path ${localPhotoFile.absolutePath}")
            return localPhotoFile
        }

        fun takeProfilePicture(context: Context,
                               viewModel: UserViewModel,
                               takePictureLauncher : ActivityResultLauncher<Uri>
        ) {
            val uuid = generateFileName()
            Log.d(TAG, "Before Camera launch: This is the profile pic UUID: $uuid") // needs to be saved in the user meta
            // We need to remember the picture's file name for the callback,
            // so put it in the view model
            viewModel.setProfilePhotoUUID(uuid)

            //val localPhotoFile = fileNameToFile(uuid)
            viewModel.setProfilePhotoFile(fileNameToFile(uuid))
            val uri = FileProvider.getUriForFile(
                //context, context.applicationInfo.packageName, localPhotoFile)
                context, context.applicationInfo.packageName, viewModel.getProfilePhotoFile()!!)
            Log.d(TAG, "Before Camera launch: profile photo uri $uri")
            takePictureLauncher.launch(uri)
        }

    }
}