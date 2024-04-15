package com.example.platepal.ui

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.platepal.R
import com.example.platepal.databinding.DiscoverFragmentBinding
import edu.cs371m.reddit.glide.Glide
import androidx.navigation.fragment.findNavController

private const val TAG = "MainActivity"

class DiscoverFragment: Fragment() {

    private var _binding: DiscoverFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    //private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = DiscoverFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")
        viewModel.setTitle("PlatePal")

        //bind adapter
        val adapter = RecipeAdapter(viewModel){
            val action = DiscoverFragmentDirections.actionDiscoverToOnePost(it)
            findNavController().navigate(action)
        }
        binding.discoverRv.adapter = adapter

        // grid layout for RecyclerView
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.discoverRv.layoutManager = layoutManager

        //populate recipe list
        viewModel.observeRecipeList().observe(viewLifecycleOwner) {
            adapter.submitList(it)
            viewModel.setRandomRecipe()
        }

        //populate spotlight
        viewModel.observeRandomSpotlightRecipe().observe(viewLifecycleOwner) { recipeSpotlight ->
            Log.d(TAG, "Observed random recipe: $recipeSpotlight")
            if (recipeSpotlight == null) {
                binding.spotlightText.visibility = View.GONE
                binding.spotlightCardView.visibility = View.GONE
            } else {
                binding.spotlightText.visibility = View.VISIBLE
                binding.spotlightCardView.visibility = View.VISIBLE
                binding.spotlightRecipeTitle.text = recipeSpotlight.title
                Glide.glideFetch(recipeSpotlight.image, recipeSpotlight.image, binding.spotlightRecipeImage)

                //spotlight favorites
                viewModel.isFavoriteRecipe(recipeSpotlight)?.let{
                    if (it) binding.spotlightHeart.setImageResource(R.drawable.ic_heart_filled)
                    else binding.spotlightHeart.setImageResource(R.drawable.ic_heart_empty)
                }

                binding.spotlightHeart.setOnClickListener{
                    Log.d(javaClass.simpleName, "heart clicklistener")
                    viewModel.isFavoriteRecipe(recipeSpotlight)?.let{
                        if(it){
                            viewModel.setFavoriteRecipe(recipeSpotlight, false)
                            binding.spotlightHeart.setImageResource(R.drawable.ic_heart_empty)
                            Log.d(javaClass.simpleName, "set heart to empty")
                        } else{
                            viewModel.setFavoriteRecipe(recipeSpotlight, true)
                            binding.spotlightHeart.setImageResource(R.drawable.ic_heart_filled)
                            Log.d(javaClass.simpleName, "set heart to filled")
                        }
                    }
                }

                binding.spotlightRecipeImage.setOnClickListener{
                    val action = DiscoverFragmentDirections.actionDiscoverToOnePost(recipeSpotlight)
                    findNavController().navigate(action)
                }
            }
        }

        //click into search page
        binding.discoverActionSearch.setOnClickListener{
            val action = DiscoverFragmentDirections.actionDiscoverToSearch()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}