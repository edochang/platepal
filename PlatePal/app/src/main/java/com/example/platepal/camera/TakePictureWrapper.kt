package com.example.platepal.camera

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import com.example.platepal.ui.viewmodel.MainViewModel
import java.io.File
import java.util.UUID

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
            Log.d("TakePictureWrapper", "photo path ${localPhotoFile.absolutePath}")
            return localPhotoFile
        }
        fun takePicture(userName: String,
                        context: Context,
                        viewModel: MainViewModel,
                        takePictureLauncher : ActivityResultLauncher<Uri>
        ) {
            viewModel.pictureNameByUser = userName
            val uuid = generateFileName()
            // We need to remember the picture's file name for the callback,
            // so put it in the view model
            viewModel.takePictureUUID(uuid)
            val localPhotoFile = fileNameToFile(uuid)
            val uri = FileProvider.getUriForFile(
                context, context.applicationInfo.packageName, localPhotoFile)
            Log.d("TakePictureWrapper", "photo uri $uri")
            takePictureLauncher.launch(uri)
        }
    }
}