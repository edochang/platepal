package com.example.platepal.ui.community

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.platepal.MainActivity
import com.example.platepal.R
import com.example.platepal.camera.TakePictureWrapper
import com.example.platepal.data.CreatePostValidations
import com.example.platepal.data.PostMeta
import com.example.platepal.data.RecipeMeta
import com.example.platepal.databinding.CommunityCreateFragmentBinding
import com.example.platepal.ui.viewmodel.OnePostViewModel
import com.example.platepal.ui.viewmodel.UserViewModel

class CreateOnePostFragment : Fragment() {
    companion object {
        const val TAG = "CreateOnePostFragment"
    }

    private var _binding: CommunityCreateFragmentBinding? = null
    private val binding get() = _binding!!
    private val onePostViewModel: OnePostViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            onePostViewModel.getPhotoFile()?.let {
                if (it.exists()) {
                    onePostViewModel.pictureReplace()
                }
            }
            setPhoto()
        } else {
            onePostViewModel.getPhotoFile()?.let {
                onePostViewModel.setPictureUUID(it.nameWithoutExtension)
            } ?: onePostViewModel.pictureReset()
        }
    }

    private fun setPhoto() {
        val pictureUUID = onePostViewModel.getPictureUUID()
        onePostViewModel.setPhotoFile(TakePictureWrapper.fileNameToFile(pictureUUID))
        onePostViewModel.fetchLocalPostPhoto(binding.postImage)
    }

    private fun showValidationSnackbarMessage(validation: CreatePostValidations) {
        val message = when (validation) {
            CreatePostValidations.RECIPE -> "You must select a recipe to post your creation of it."
            CreatePostValidations.PICTURE -> "You must upload a photo of your plate."
        }

        MainActivity.showSnackbarMessage(
            binding.root,
            message
        )
    }

    private fun takePicture(user: String) {
        val pictureName = "Dish Post by $user"
        context?.let {
            TakePictureWrapper.takePictureOnePost(
                pictureName,
                it,
                onePostViewModel,
                cameraLauncher
            )
        } ?: Log.e(TAG, "Failed to launch Camera")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = CommunityCreateFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")

        val recipeMeta: RecipeMeta? = onePostViewModel.recipeMeta
        val trigger = requireArguments().getString("trigger")
        val user = userViewModel.getAuthUUID()
        val username = userViewModel.userMeta?.fullName ?: userViewModel.getAuthEmail()

        binding.postUserName.text = username
        userViewModel.userMeta?.pictureUUID?.let {
            if (it.isNotEmpty()) {
                binding.postUserProfilePicture.setImageResource(R.drawable.transparent)
                binding.postUserProfilePicture.setBackgroundColor(Color.Transparent.hashCode())
                binding.postUserProfilePicture.imageTintList = null
                userViewModel.fetchProfilePhoto(it, binding.postUserProfilePicture)
            }
        }

        Log.d(TAG, "recipeMeta:$recipeMeta")
        recipeMeta?.let {
            binding.postRecipe.text = recipeMeta.title
        }

        if (onePostViewModel.getPictureUUID().isNotEmpty()) {
            setPhoto()
        }

        binding.postCloseButton.setOnClickListener {
            onePostViewModel.pictureReset()
            findNavController().navigateUp()
        }

        binding.postRecipe.setOnClickListener {
            val action =
                CreateOnePostFragmentDirections.actionCreateOnePostToSearch(MainActivity.SEARCH_FROM_ADDR_ONEPOST)
            findNavController().navigate(action)
        }

        binding.postCameraButton.setOnClickListener {
            takePicture(user)
        }

        if (trigger == MainActivity.ONEPOST_TRIGGER_CAMERA) {
            takePicture(user)
        }

        binding.postSaveButton.setOnClickListener {
            if (recipeMeta == null) {
                showValidationSnackbarMessage(CreatePostValidations.RECIPE)
                return@setOnClickListener
            }

            val pFile = onePostViewModel.getPhotoFile()
            if (pFile == null) {
                showValidationSnackbarMessage(CreatePostValidations.PICTURE)
                return@setOnClickListener
            } else {
                if (!pFile.exists()) {
                    showValidationSnackbarMessage(CreatePostValidations.PICTURE)
                    return@setOnClickListener
                }
            }

            // Create metadata and store picture in storage
            val postMeta = PostMeta(
                recipeMeta.sourceId,
                recipeMeta.title,
                binding.postEditText.text.toString(),
                onePostViewModel.pictureNameByUser,
                pFile.nameWithoutExtension,
                "image/jpg",
                user
            )

            onePostViewModel.savePost(postMeta)
            findNavController().navigateUp()
        }
    }
}