package com.example.platepal.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.platepal.data.RecipeMeta
import com.example.platepal.databinding.CookbookFragmentBinding
import com.example.platepal.ui.viewmodel.MainViewModel

class CookbookFragment : Fragment() {

    private var _binding: CookbookFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

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
        //Log.d(javaClass.simpleName, "onViewCreated")
        _binding = CookbookFragmentBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        viewModel.setTitle("PlatePal")

        val adapter = RecipeAdapter(viewModel){
            val action = CookbookFragmentDirections.actionCookbookToOneRecipe(it)
            findNavController().navigate(action)
        }
        binding.cookbookRv.adapter = adapter

        // grid layout for RecyclerView
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.cookbookRv.layoutManager = layoutManager

        viewModel.observeFavListLive().observe(viewLifecycleOwner){
            if (it.isNotEmpty()){
                binding.placeholder.visibility = View.GONE
                //Log.d(javaClass.simpleName, "placeholder view gone")
            }
            else {
                binding.placeholder.visibility = View.VISIBLE
                //Log.d(javaClass.simpleName, "placeholder view visible")
            }

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
                filterFavList(newText, adapter, binding.cookbookRv)
                return true
            }
        })
    }

    private fun filterFavList(query: String?, adapter: RecipeAdapter, view: View){

        if (query != null){
            val filteredList =  mutableListOf<RecipeMeta>()
            for (i in viewModel.getFavList()!!){
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