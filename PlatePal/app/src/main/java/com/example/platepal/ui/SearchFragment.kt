package com.example.platepal.ui

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.platepal.R
import com.example.platepal.data.SpoonacularRecipe
import com.example.platepal.databinding.SearchFragmentBinding

class SearchFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: SearchFragmentBinding? = null
    private val binding get() = _binding!!
    private val args: SearchFragmentArgs by navArgs()

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

        binding.search.queryHint = args.hint

        val adapter = RecipeAdapter(viewModel){

        }
        binding.searchRv.adapter = adapter

        // linear layout for RecyclerView
        binding.searchRv.layoutManager = LinearLayoutManager(activity)

        //populate initial list
        adapter.submitList(viewModel.getCopyOfRecipeList())

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

        if (query != null){
            val filteredList =  mutableListOf<SpoonacularRecipe>()
            for (i in viewModel.getCopyOfRecipeList()){
                if (i.title.lowercase().contains(query)) {
                    filteredList.add(i)
                }
            }
            if (filteredList.isEmpty()){
                //Toast.makeText(activity, "No data found", Toast.LENGTH_SHORT).show()
                view.visibility = View.GONE // remove recycler view list
            }else{
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