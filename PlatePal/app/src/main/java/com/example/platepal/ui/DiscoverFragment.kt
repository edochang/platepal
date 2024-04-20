package com.example.platepal.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.platepal.R
import com.example.platepal.databinding.DiscoverFragmentBinding
import edu.cs371m.reddit.glide.Glide
import androidx.navigation.fragment.findNavController
import com.example.platepal.MainActivity
import com.example.platepal.ui.viewmodel.MainViewModel
import com.example.platepal.ui.viewmodel.UserViewModel

private const val TAG = "DiscoverFragment"

class DiscoverFragment: Fragment() {

    private var _binding: DiscoverFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

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
        //Log.d(javaClass.simpleName, "onViewCreated")
        viewModel.setTitle("PlatePal")

        Log.d(TAG, "current profile UUID is ${userViewModel.getProfilePhotoUUID()}")
        Log.d(TAG, "current profile FILE exists?${userViewModel.getProfilePhotoFile()?.exists()}: ${userViewModel.getProfilePhotoFile()}")

        //bind adapter
        val adapter = RecipeAdapter(viewModel, userViewModel){
            val action = DiscoverFragmentDirections.actionDiscoverToOnePost(it)
            findNavController().navigate(action)
        }
        binding.discoverRv.adapter = adapter

        // grid layout for RecyclerView
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.discoverRv.layoutManager = layoutManager

        //userViewModel.fetchInitialFavRecipes{
          //  Log.d(TAG, "favorite recipe list listener invoked")
        //}


        //populate recipe list
        viewModel.observeRecipeList().observe(viewLifecycleOwner) {
            adapter.submitList(it)
            viewModel.setRandomRecipe()
        }

        //populate spotlight
        viewModel.observeRandomSpotlightRecipe().observe(viewLifecycleOwner) { recipeSpotlight ->
            //Log.d(TAG, "Observed random recipe: $recipeSpotlight")
            if (recipeSpotlight == null) {
                binding.spotlightText.visibility = View.GONE
                binding.spotlightCardView.visibility = View.GONE
            } else {
                binding.spotlightText.visibility = View.VISIBLE
                binding.spotlightCardView.visibility = View.VISIBLE
                binding.spotlightRecipeTitle.text = recipeSpotlight.title
                Glide.glideFetch(recipeSpotlight.image, recipeSpotlight.image, binding.spotlightRecipeImage)

                //spotlight favorites
                userViewModel.isFavoriteRecipe(recipeSpotlight)?.let{
                    if (it) binding.spotlightHeart.setImageResource(R.drawable.ic_heart_filled)
                    else binding.spotlightHeart.setImageResource(R.drawable.ic_heart_empty)
                }

                binding.spotlightHeart.setOnClickListener{
                    //Log.d(javaClass.simpleName, "heart clicklistener")
                    userViewModel.isFavoriteRecipe(recipeSpotlight)?.let{
                        if(it){
                            userViewModel.setFavoriteRecipe(recipeSpotlight, false)
                            binding.spotlightHeart.setImageResource(R.drawable.ic_heart_empty)
                            //Log.d(javaClass.simpleName, "set heart to empty")
                        } else{
                            userViewModel.setFavoriteRecipe(recipeSpotlight, true)
                            binding.spotlightHeart.setImageResource(R.drawable.ic_heart_filled)
                            //Log.d(javaClass.simpleName, "set heart to filled")
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
            val action = DiscoverFragmentDirections.actionDiscoverToSearch(MainActivity.SEARCH_FROM_ADDR_DISCOVER)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}