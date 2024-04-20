package com.example.platepal.camera

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import com.example.platepal.ui.viewmodel.OnePostViewModel
import com.example.platepal.ui.viewmodel.OneRecipeViewModel
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
            Log.d("TakePictureWrapper", "photo path ${localPhotoFile.absolutePath}")
            return localPhotoFile
        }
        fun takePictureOnePost(
                        pictureName: String,
                        context: Context,
                        viewModel: OnePostViewModel,
                        takePictureLauncher : ActivityResultLauncher<Uri>
        ) {
            viewModel.pictureNameByUser = pictureName
            val uuid = generateFileName()
            viewModel.setPictureUUID(uuid)
            val localPhotoFile = fileNameToFile(uuid)
            val uri = FileProvider.getUriForFile(
                context, context.applicationInfo.packageName, localPhotoFile)
            Log.d(TAG, "photo uri $uri")
            takePictureLauncher.launch(uri)
        }

        fun takePictureOneRecipe(
            pictureName: String,
            context: Context,
            viewModel: OneRecipeViewModel,
            takePictureLauncher : ActivityResultLauncher<Uri>
        ) {
            viewModel.pictureNameByUser = pictureName
            val uuid = generateFileName()
            viewModel.setPictureUUID(uuid)
            val localPhotoFile = fileNameToFile(uuid)
            val uri = FileProvider.getUriForFile(
                context, context.applicationInfo.packageName, localPhotoFile)
            Log.d(TAG, "photo uri $uri")
            takePictureLauncher.launch(uri)
        }
    }
}