package com.example.platepal.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.platepal.MainActivity
import com.example.platepal.data.RecipeMeta
import com.example.platepal.databinding.SearchFragmentBinding
import com.example.platepal.ui.viewmodel.MainViewModel
import com.example.platepal.ui.viewmodel.OnePostViewModel
import com.example.platepal.ui.viewmodel.UserViewModel

class SearchFragment : Fragment() {
    companion object {
        const val TAG = "SearchFragment"
    }

    private var _binding: SearchFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val onePostViewModel: OnePostViewModel by activityViewModels()
    private lateinit var recipeList: List<RecipeMeta>

    //search
    private fun filterList(query: String?, adapter: RecipeAdapter, view: View) {
        Log.d(TAG, "Enter filterList with query: $query (adapter: $adapter, view: $view")

        query?.let {
            val filteredList = mutableListOf<RecipeMeta>()
            for (i in recipeList) {
                if (i.title.lowercase().contains(query)) {
                    filteredList.add(i)
                }
            }
            if (filteredList.isEmpty()) {
                //Toast.makeText(activity, "No data found", Toast.LENGTH_SHORT).show()
                view.visibility = View.GONE // remove recycler view list
            } else {
                view.visibility = View.VISIBLE  // put rv list back in
                adapter.submitList(filteredList)
            }
        }
    }

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

        val fromAddress = requireArguments().getString("fromAddress")

        val adapter = RecipeAdapter(viewModel, userViewModel) {
            val action = if (fromAddress == MainActivity.SEARCH_FROM_ADDR_DISCOVER) {
                SearchFragmentDirections.actionSearchToOneRecipe(it)
            } else {
                onePostViewModel.recipeMeta = it
                SearchFragmentDirections.actionSearchToOnePost(MainActivity.ONEPOST_TRIGGER_SEARCH)
            }

            findNavController().navigate(action)
        }
        binding.searchRv.adapter = adapter

        // linear layout for RecyclerView
        //binding.searchRv.layoutManager = LinearLayoutManager(activity)

        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.searchRv.layoutManager = layoutManager

        //populate recipe list
        //adapter.submitList(viewModel.getCopyOfRecipeList())
        viewModel.observeAllRecipeList().observe(viewLifecycleOwner) {
            recipeList = it
            if (binding.search.query.isEmpty()) {
                adapter.submitList(it)
            }
            Log.d(TAG, "Search query: ${binding.search.query.toString()}")
        }

        //search
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                //hides keyboard when submitted a query
                //but does not hide keybarod when submitting empty query
                Log.d(TAG, "search.onQueryTextSubmit")
                binding.search.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText, adapter, binding.searchRv)
                return true
            }
        })


    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}