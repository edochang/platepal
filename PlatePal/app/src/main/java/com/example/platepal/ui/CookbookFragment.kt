package com.example.platepal.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.platepal.R
import com.example.platepal.api.Repository
import com.example.platepal.databinding.CookbookFragmentBinding
import edu.cs371m.reddit.glide.Glide

class CookbookFragment : Fragment() {
    private var _binding: CookbookFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    private fun initRVGrid(binding: CookbookFragmentBinding) {

        val adapter = RecipeGridAdapter(viewModel)
        binding.cookbookRv.adapter = adapter

        // grid layout for RecyclerView
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.cookbookRv.layoutManager = layoutManager

        viewModel.setFavList()
        viewModel.observeFavListLive().observe(viewLifecycleOwner){
            adapter.submitList(it)
        }

        //issue: placeholder does not reappear when favlist is emptied on cookbook page
        //placeholder msg only appears when we first go to the cookbook page w/ an empty favlist
        //could try putting this code in the adapter? Perhaps in a lambda?
        if (viewModel.faveListSize() > 0)
            binding.placeholder.visibility = View.GONE
        else
            binding.placeholder.visibility = View.VISIBLE

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = CookbookFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(javaClass.simpleName, "onViewCreated")
        _binding = CookbookFragmentBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        initRVGrid(binding)
    }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}