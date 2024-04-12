package com.example.platepal.ui

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
import com.example.platepal.data.SpoonacularRecipe
import com.example.platepal.databinding.DiscoverFragmentBinding
import edu.cs371m.reddit.glide.Glide
import androidx.navigation.fragment.findNavController
import com.example.platepal.ui.DiscoverFragmentDirections

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

        //populate initial list
        adapter.submitList(viewModel.getCopyOfRecipeList())

        //populate spotlight
        val single = viewModel.getRandomRecipe()
        binding.spotlightRecipeTitle.text = single.title
        Glide.glideFetch(single.image, single.image, binding.spotlightRecipeImage)

        //spotlight favorites
        viewModel.isFavoriteRecipe(single)?.let{
            if (it) binding.spotlightHeart.setImageResource(R.drawable.ic_heart_filled)
            else binding.spotlightHeart.setImageResource(R.drawable.ic_heart_empty)
        }

        binding.spotlightHeart.setOnClickListener{
            Log.d(javaClass.simpleName, "heart clicklistener")
            viewModel.isFavoriteRecipe(single)?.let{
                if(it){
                    viewModel.setFavoriteRecipe(single, false)
                    binding.spotlightHeart.setImageResource(R.drawable.ic_heart_empty)
                    Log.d(javaClass.simpleName, "set heart to empty")
                } else{
                    viewModel.setFavoriteRecipe(single, true)
                    binding.spotlightHeart.setImageResource(R.drawable.ic_heart_filled)
                    Log.d(javaClass.simpleName, "set heart to filled")
                }
            }
        }

        //click into search page
        binding.discoverActionSearch.setOnClickListener{
            val action = DiscoverFragmentDirections.actionDiscoverToSearch()
            findNavController().navigate(action)
        }

        binding.spotlightRecipeImage.setOnClickListener{
            val action = DiscoverFragmentDirections.actionDiscoverToOnePost(single)
            findNavController().navigate(action)
        }

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}