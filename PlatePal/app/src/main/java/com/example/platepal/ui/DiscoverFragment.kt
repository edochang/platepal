package com.example.platepal.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.platepal.R
import com.example.platepal.databinding.DiscoverFragmentBinding
import com.example.platepal.glide.Glide
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.example.platepal.MainActivity
import com.example.platepal.ui.viewmodel.MainViewModel
import com.example.platepal.ui.viewmodel.UserViewModel

class DiscoverFragment : Fragment() {
    companion object {
        const val TAG = "DiscoverFragment"
    }

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
        val mainActivity = (requireActivity() as MainActivity)

        Log.d(TAG, "current profile photo UUID is ${userViewModel.getProfilePhotoUUID()}")
        Log.d(
            TAG,
            "current profile photo FILE exists?${
                userViewModel.getProfilePhotoFile()?.exists()
            }: ${userViewModel.getProfilePhotoFile()}"
        )

        //bind adapter to show Popular list
        val popularAdapter = RecipeAdapter(viewModel, userViewModel) {
            val action = DiscoverFragmentDirections.actionDiscoverToOnePost(it)
            findNavController().navigate(action)
        }
        binding.discoverRv.adapter = popularAdapter
        val popularLayoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.discoverRv.layoutManager = popularLayoutManager
        val snapHelperPopular = LinearSnapHelper()
        snapHelperPopular.attachToRecyclerView(binding.discoverRv)

        viewModel.observeRecipeList().observe(viewLifecycleOwner) {
            popularAdapter.submitList(it)
            viewModel.setRandomRecipe()
            Log.d(TAG, "popular list size is ${it.size}")
        }

        //bind adapter to show User Created List
        viewModel.observeUserCreatedRecipeList().observe(viewLifecycleOwner){
            if (it.isNotEmpty()){
                binding.userCreatedText.visibility = View.VISIBLE
                binding.userCreatedRv.visibility = View.VISIBLE
                val userCreatedAdapter = RecipeAdapter(viewModel, userViewModel) {
                    val action = DiscoverFragmentDirections.actionDiscoverToOnePost(it)
                    findNavController().navigate(action)
                }
                binding.userCreatedRv.adapter = userCreatedAdapter
                val userCreatedLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                binding.userCreatedRv.layoutManager = userCreatedLayoutManager

                /*
                //snaphelper does not work well when there are only 3 recipes
                //given the size of our recipe cards
                if(it.size > 3){
                    val snapHelperUser = LinearSnapHelper()
                    snapHelperUser.attachToRecyclerView(binding.userCreatedRv)
                }
                 */
                userCreatedAdapter.submitList(it)
                Log.d(TAG, "user created list size is ${it.size}")
            }else{
                binding.userCreatedText.visibility = View.GONE
                binding.userCreatedRv.visibility = View.GONE
            }

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
                Glide.glideFetch(
                    recipeSpotlight.image,
                    recipeSpotlight.image,
                    binding.spotlightRecipeImage
                )

                //spotlight favorites
                userViewModel.isFavoriteRecipe(recipeSpotlight)?.let {
                    if (it) binding.spotlightHeart.setImageResource(R.drawable.ic_heart_filled)
                    else binding.spotlightHeart.setImageResource(R.drawable.ic_heart_empty)
                }

                binding.spotlightHeart.setOnClickListener {
                    //Log.d(javaClass.simpleName, "heart clicklistener")
                    userViewModel.isFavoriteRecipe(recipeSpotlight)?.let {
                        if (it) {
                            userViewModel.setFavoriteRecipe(recipeSpotlight, false)
                            binding.spotlightHeart.setImageResource(R.drawable.ic_heart_empty)
                            //Log.d(javaClass.simpleName, "set heart to empty")
                        } else {
                            userViewModel.setFavoriteRecipe(recipeSpotlight, true)
                            binding.spotlightHeart.setImageResource(R.drawable.ic_heart_filled)
                            //Log.d(javaClass.simpleName, "set heart to filled")
                        }
                    }
                }

                binding.spotlightRecipeImage.setOnClickListener {
                    val action = DiscoverFragmentDirections.actionDiscoverToOnePost(recipeSpotlight)
                    findNavController().navigate(action)
                }
            }
        }

        //click into search page
        binding.discoverActionSearch.setOnClickListener {
            val action =
                DiscoverFragmentDirections.actionDiscoverToSearch(MainActivity.SEARCH_FROM_ADDR_DISCOVER)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}