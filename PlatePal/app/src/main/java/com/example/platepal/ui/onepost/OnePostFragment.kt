package com.example.platepal.ui.onepost

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
import com.example.platepal.databinding.OnePostFragmentBinding
import com.example.platepal.ui.viewmodel.MainViewModel
import com.example.platepal.ui.ViewPagerAdapter
import com.example.platepal.ui.viewmodel.OneRecipeViewModel
import com.google.android.material.tabs.TabLayoutMediator
import edu.cs371m.reddit.glide.Glide

private const val TAG = "OnePostFragment"

class OnePostFragment : Fragment() {

    private var _binding: OnePostFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private val oneRecipeViewModel: OneRecipeViewModel by activityViewModels()
    private val args: OnePostFragmentArgs by navArgs()
    private lateinit var mainActivity: MainActivity

    private fun getRecipeInfo() {
        //mainActivity.progressBarOn()
        Log.d(TAG, "Retrieving recipe info from Repo...")
        oneRecipeViewModel.fetchReposRecipeInfo {
            Log.d(TAG, "Recipe info retrieval listener invoked.")
            //mainActivity.progressBarOff()
        }
    }

    private fun setupFavorites(recipe: RecipeMeta) {
        viewModel.isFavoriteRecipe(recipe)?.let{
            if(it) binding.onePostHeart.setImageResource(R.drawable.ic_heart_filled)
            else binding.onePostHeart.setImageResource(R.drawable.ic_heart_empty)
        }

        binding.onePostHeart.setOnClickListener{
            Log.d(javaClass.simpleName, "heart clicklistener")
            viewModel.isFavoriteRecipe(recipe)?.let{
                if(it) {
                    viewModel.setFavoriteRecipe(recipe, false)
                    binding.onePostHeart.setImageResource(R.drawable.ic_heart_empty)
                    Log.d(javaClass.simpleName, "set heart to empty")
                } else {
                    viewModel.setFavoriteRecipe(recipe, true)
                    binding.onePostHeart.setImageResource(R.drawable.ic_heart_filled)
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
        _binding = OnePostFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Log.d(javaClass.simpleName, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        mainActivity = (requireActivity() as MainActivity)

        viewModel.setTitle("Recipe")
        val recipe = args.recipe
        oneRecipeViewModel.setRecipeSourceId(recipe.sourceId)

        getRecipeInfo()

        // Set main information
        binding.onePostTitle.text = recipe.title
        Glide.glideFetch(recipe.image, recipe.image, binding.onePostImage)

        // Favorites
        setupFavorites(recipe)

        val fragmentsList = arrayListOf(PostIngredients(), PostDirections(), PostNotes())

        binding.apply {
            viewPager.adapter = ViewPagerAdapter(
                fragmentsList,
                this@OnePostFragment.childFragmentManager,
                lifecycle)

            TabLayoutMediator(tabView, viewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = "Ingredients"
                    1 -> tab.text = "Directions"
                    2 -> tab.text = "Notes"
                }
            }.attach()
        }

    }

        override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}