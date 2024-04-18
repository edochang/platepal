package com.example.platepal.ui.community

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.platepal.MainActivity
import com.example.platepal.data.CreatePostValidations
import com.example.platepal.data.CreateRecipeValidations
import com.example.platepal.data.RecipeMeta
import com.example.platepal.databinding.CommunityCreateFragmentBinding
import com.example.platepal.ui.recipe.OneRecipeFragmentArgs
import com.example.platepal.ui.viewmodel.OnePostViewModel

class CreateOnePostFragment: Fragment() {
    private var _binding: CommunityCreateFragmentBinding? = null
    private val binding get() = _binding!!
    private val onePostViewModel: OnePostViewModel by activityViewModels()
    private val args: CreateOnePostFragmentArgs by navArgs()

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

        val recipeMeta: RecipeMeta? = args.recipe

        binding.postCloseButton.setOnClickListener {
            findNavController().navigateUp()
        }

        recipeMeta?.let {
            binding.postRecipe.text = recipeMeta.title
        }

        binding.postRecipe.setOnClickListener {
            val action = CreateOnePostFragmentDirections.actionCreateOnePostToSearch(MainActivity.SEARCH_FROM_ADDR_ONEPOST)
            findNavController().navigate(action)
        }

        binding.postSaveButton.setOnClickListener {
            if (recipeMeta == null) {
                showValidationSnackbarMessage(CreatePostValidations.RECIPE)
                return@setOnClickListener
            }
        }
    }
}