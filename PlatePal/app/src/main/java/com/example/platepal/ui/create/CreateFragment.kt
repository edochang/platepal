package com.example.platepal.ui.create

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.platepal.MainActivity
import com.example.platepal.data.CreateRecipeValidations
import com.example.platepal.databinding.CreateFragmentBinding
import com.example.platepal.ui.DiscoverFragmentDirections
import com.example.platepal.ui.viewmodel.MainViewModel
import com.example.platepal.ui.ViewPagerAdapter
import com.example.platepal.ui.viewmodel.OneRecipeViewModel
import com.google.android.material.tabs.TabLayoutMediator

private const val TAG = "CreateFragment"

class CreateFragment : Fragment() {
    private var _binding: CreateFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private val oneRecipeViewModel: OneRecipeViewModel by activityViewModels()
    private lateinit var mainActivity: MainActivity

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = CreateFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")
        mainActivity = (requireActivity() as MainActivity)
        viewModel.setTitle("PlatePal")

        val fragmentsList = arrayListOf(Ingredients(), Directions(), Notes())

        binding.apply {
            viewPager.adapter = ViewPagerAdapter(fragmentsList, this@CreateFragment.childFragmentManager, lifecycle)

            TabLayoutMediator(tabView, viewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = "Ingredients"
                    1 -> tab.text = "Directions"
                    2 -> tab.text = "Notes"
                }
            }.attach()
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

            Log.d(TAG, "Check binding instance: $ingredientsBinding, $directionsBinding, $notesBinding")

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

            val photo_uuid = "no_recipe_photo.jpg"

            // Create Recipe
            oneRecipeViewModel.createRecipe(
                recipeTitle.text.toString(),
                photo_uuid,
                ingredientsBinding.ingredientsEditText.text.toString(),
                directionsBinding.directionsEditText.text.toString(),
                notesBinding?.notesEditText?.text.toString() ?: "",
                "FakeUser"
            ) {
                mainActivity.initRecipeList()

                val action = CreateFragmentDirections.actionCreateRecipeToOnePost(it)
                findNavController().navigate(action)
            }
        }
    }

}