package com.example.platepal.ui.recipe

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.platepal.MainActivity
import com.example.platepal.R
import com.example.platepal.data.RecipeMeta
import com.example.platepal.databinding.OneRecipeFragmentBinding
import com.example.platepal.ui.viewmodel.MainViewModel
import com.example.platepal.ui.ViewPagerAdapter
import com.example.platepal.ui.viewmodel.OneRecipeViewModel
import com.example.platepal.ui.viewmodel.UserViewModel
import com.google.android.material.tabs.TabLayoutMediator

private const val TAG = "OneRecipeFragment"

class OneRecipeFragment : Fragment() {

    private var _binding: OneRecipeFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private val oneRecipeViewModel: OneRecipeViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val args: OneRecipeFragmentArgs by navArgs()
    private lateinit var mainActivity: MainActivity

    private fun getRecipeInfo() {
        mainActivity.progressBarOn()
        Log.d(TAG, "Retrieving recipe info from Repo...")
        oneRecipeViewModel.fetchReposRecipeInfo {
            Log.d(TAG, "Recipe info retrieval listener invoked.")
            // mainActivity.progressBarOff() // Note: This is done on the observer below.
        }
    }

    private fun setupFavorites(recipe: RecipeMeta) {
        userViewModel.isFavoriteRecipe(recipe)?.let {
            if (it) binding.oneRecipeHeart.setImageResource(R.drawable.ic_heart_filled)
            else binding.oneRecipeHeart.setImageResource(R.drawable.ic_heart_empty)
        }

        binding.oneRecipeHeart.setOnClickListener {
            Log.d(javaClass.simpleName, "heart clicklistener")
            userViewModel.isFavoriteRecipe(recipe)?.let {
                if (it) {
                    userViewModel.setFavoriteRecipe(recipe, false)
                    binding.oneRecipeHeart.setImageResource(R.drawable.ic_heart_empty)
                    Log.d(javaClass.simpleName, "set heart to empty")
                } else {
                    userViewModel.setFavoriteRecipe(recipe, true)
                    binding.oneRecipeHeart.setImageResource(R.drawable.ic_heart_filled)
                    Log.d(javaClass.simpleName, "set heart to filled")
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = OneRecipeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Log.d(javaClass.simpleName, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        mainActivity = (requireActivity() as MainActivity)

        viewModel.setTitle("Recipe")
        val recipe = args.recipe

        oneRecipeViewModel.setRecipe(recipe)
        oneRecipeViewModel.setRecipeSourceId(recipe.sourceId)

        getRecipeInfo()

        // Set main information
        binding.oneRecipeTitle.text = recipe.title
        viewModel.fetchRecipePhoto(recipe.image, recipe.createdBy, binding.oneRecipeImage)

        // Favorites
        setupFavorites(recipe)

        val fragmentsList = arrayListOf(OneRecipeIngredients(), OneRecipeDirections(), OneRecipeNotes())

        binding.apply {
            viewPager.adapter = ViewPagerAdapter(
                fragmentsList,
                this@OneRecipeFragment.childFragmentManager,
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

        oneRecipeViewModel.observeRecipeInfo().observe(viewLifecycleOwner) {
            mainActivity.progressBarOff()
        }

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}