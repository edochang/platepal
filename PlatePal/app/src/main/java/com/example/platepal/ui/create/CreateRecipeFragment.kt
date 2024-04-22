package com.example.platepal.ui.create

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.platepal.MainActivity
import com.example.platepal.R
import com.example.platepal.camera.TakePictureWrapper
import com.example.platepal.data.CreateRecipeValidations
import com.example.platepal.data.RecipeMeta
import com.example.platepal.databinding.CreateRecipeFragmentBinding
import com.example.platepal.ui.viewmodel.MainViewModel
import com.example.platepal.ui.ViewPagerAdapter
import com.example.platepal.ui.viewmodel.OneRecipeViewModel
import com.example.platepal.ui.viewmodel.UserViewModel
import com.google.android.material.tabs.TabLayoutMediator

class CreateRecipeFragment : Fragment() {
    companion object {
        const val TAG = "CreateFragment"
    }

    private var _binding: CreateRecipeFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private val oneRecipeViewModel: OneRecipeViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var mainActivity: MainActivity

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            oneRecipeViewModel.getPhotoFile()?.let {
                if (it.exists()) {
                    oneRecipeViewModel.pictureReplace()
                }
            }
            setPhoto()
        } else {
            oneRecipeViewModel.getPhotoFile()?.let {
                oneRecipeViewModel.setPictureUUID(it.nameWithoutExtension)
            } ?: oneRecipeViewModel.pictureReset()
        }
    }

    private fun saveAndNavigate(recipeMeta: RecipeMeta) {
        mainActivity.initUserCreatedRecipeList()
        val action = CreateRecipeFragmentDirections.actionCreateRecipeToOneRecipe(recipeMeta)
        findNavController().navigate(action, navOptions {
            popUpTo(R.id.discoverFragment)
        })
    }

    private fun setPhoto() {
        val pictureUUID = oneRecipeViewModel.getPictureUUID()
        oneRecipeViewModel.setPhotoFile(TakePictureWrapper.fileNameToFile(pictureUUID))
        oneRecipeViewModel.fetchLocalPostPhoto(binding.cameraButton)
    }

    private fun showValidationSnackbarMessage(validation: CreateRecipeValidations) {
        val message = when (validation) {
            CreateRecipeValidations.TITLE -> "Recipe Title cannot be blank or just white space characters."
            CreateRecipeValidations.INGREDIENTS -> "Ingredients cannot be blank."
            CreateRecipeValidations.DIRECTIONS -> "Directions cannot be blank."
        }

        MainActivity.showSnackbarMessage(
            binding.root,
            message
        )
    }

    private fun takePicture(user: String) {
        val pictureName = "Dish Post by $user"
        context?.let {
            TakePictureWrapper.takePictureOneRecipe(
                pictureName,
                it,
                oneRecipeViewModel,
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
        _binding = CreateRecipeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")
        mainActivity = (requireActivity() as MainActivity)
        viewModel.setTitle("PlatePal")

        val user = userViewModel.getAuthUUID()
        val fragmentsList = arrayListOf(Ingredients(), Directions(), Notes())

        binding.apply {
            viewPager.adapter = ViewPagerAdapter(
                fragmentsList,
                this@CreateRecipeFragment.childFragmentManager,
                lifecycle
            )

            TabLayoutMediator(tabView, viewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = "Ingredients"
                    1 -> tab.text = "Directions"
                    2 -> tab.text = "Notes"
                }
            }.attach()
        }

        binding.cameraButton.setOnClickListener {
            takePicture(user)
        }

        binding.createSave.setOnClickListener {
            val recipeTitle = binding.createRecipeTitle
            if (recipeTitle.text.isBlank()) {
                showValidationSnackbarMessage(CreateRecipeValidations.TITLE)
                return@setOnClickListener
            }

            val ingredientsBinding = oneRecipeViewModel.getIngredientFragmentBinding()
            val directionsBinding = oneRecipeViewModel.getDirectionsFragmentBinding()
            val notesBinding = oneRecipeViewModel.getNotesFragmentBinding()

            Log.d(
                TAG,
                "Check binding instance: $ingredientsBinding, $directionsBinding, $notesBinding"
            )

            if (ingredientsBinding != null) {
                if (ingredientsBinding.ingredientsEditText.text.isBlank()) {
                    showValidationSnackbarMessage(CreateRecipeValidations.INGREDIENTS)
                    return@setOnClickListener
                }
            } else {
                showValidationSnackbarMessage(CreateRecipeValidations.INGREDIENTS)
                return@setOnClickListener
            }

            if (directionsBinding != null) {
                if (directionsBinding.directionsEditText.text.isBlank()) {
                    showValidationSnackbarMessage(CreateRecipeValidations.DIRECTIONS)
                    return@setOnClickListener
                }
            } else {
                showValidationSnackbarMessage(CreateRecipeValidations.DIRECTIONS)
                return@setOnClickListener
            }

            var photoUUID = oneRecipeViewModel.getPictureUUID()
            if (photoUUID == "") {
                photoUUID = "no_recipe_photo.jpg"
            }

            // Create Recipe
            val ingredients = Html.toHtml(
                ingredientsBinding.ingredientsEditText.text,
                Html.FROM_HTML_MODE_COMPACT
            )
            val directions =
                Html.toHtml(directionsBinding.directionsEditText.text, Html.FROM_HTML_MODE_COMPACT)
            val notes = notesBinding?.notesEditText?.text?.let {
                Html.toHtml(it, Html.FROM_HTML_MODE_COMPACT)
            } ?: ""

            if (oneRecipeViewModel.getPhotoFile() != null) {
                oneRecipeViewModel.saveRecipePhoto() {
                    oneRecipeViewModel.createRecipe(
                        recipeTitle.text.toString(),
                        photoUUID,
                        ingredients,
                        directions,
                        notes,
                        user
                    ) {
                        saveAndNavigate(it)
                    }
                }
            } else {
                oneRecipeViewModel.createRecipe(
                    recipeTitle.text.toString(),
                    photoUUID,
                    ingredients,
                    directions,
                    notes,
                    user
                ) {
                    saveAndNavigate(it)
                }
            }
        }

        binding.createCancel.setOnClickListener {
            oneRecipeViewModel.pictureReset()
            findNavController().navigateUp()
        }

        binding.platepalButton.setOnClickListener {
            Toast.makeText(requireContext(),
                "Tell me a recipe that you want me to create by giving it a recipe name.",
                Toast.LENGTH_LONG).show()
        }
    }

}