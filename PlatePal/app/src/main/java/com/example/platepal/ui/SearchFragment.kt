package com.example.platepal.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.platepal.data.RecipeMeta
import com.example.platepal.databinding.SearchFragmentBinding
import com.example.platepal.ui.viewmodel.MainViewModel

class SearchFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: SearchFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = SearchFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setTitle("Search")

        val adapter = RecipeAdapter(viewModel){
            val action = SearchFragmentDirections.actionSearchToOnePost(it)
            findNavController().navigate(action)
        }
        binding.searchRv.adapter = adapter

        // linear layout for RecyclerView
        binding.searchRv.layoutManager = LinearLayoutManager(activity)

        //populate recipe list
        //adapter.submitList(viewModel.getCopyOfRecipeList())
        viewModel.observeRecipeList().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        //search
        binding.search.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                //hides keyboard when submitted a query
                //but does not hide keybarod when submitting empty query
                binding.search.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText, adapter, binding.searchRv)
                return true
            }
        })

    }

    //search
    private fun filterList(query: String?, adapter: RecipeAdapter, view: View){

        var recipeList: List<RecipeMeta> = emptyList()
        viewModel.observeRecipeList().observe(viewLifecycleOwner) {
            recipeList = it
        }

        if (query != null){
            val filteredList =  mutableListOf<RecipeMeta>()
            for (i in recipeList){
                if (i.title.lowercase().contains(query)) {
                    filteredList.add(i)
                }
            }
            if (filteredList.isEmpty()){
                //Toast.makeText(activity, "No data found", Toast.LENGTH_SHORT).show()
                view.visibility = View.GONE // remove recycler view list
            } else {
                view.visibility = View.VISIBLE  // put rv list back in
                adapter.submitList(filteredList)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}