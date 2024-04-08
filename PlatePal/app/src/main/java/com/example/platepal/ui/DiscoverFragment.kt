package com.example.platepal.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.platepal.R
import com.example.platepal.api.Repository
import com.example.platepal.databinding.DiscoverFragmentBinding
import edu.cs371m.reddit.glide.Glide

class DiscoverFragment: Fragment() {
    private var _binding: DiscoverFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = DiscoverFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun initRVGrid(binding: DiscoverFragmentBinding) {

        val adapter = RecipeGridAdapter(viewModel)
        binding.discoverRv.adapter = adapter

        // grid layout for RecyclerView
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.discoverRv.layoutManager = layoutManager

        //populate initial list
        adapter.submitList(Repository().fetchData())

        //populate spotlight
        val single = Repository().fetchRandomRecipe()
        binding.spotlightRecipeTitle.text = single.title
        Glide.glideFetch(single.image, single.image, binding.spotlightRecipeImage)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")
        initRVGrid(binding)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}