package com.example.platepal.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.platepal.databinding.CookbookFragmentBinding

class CookbookFragment : Fragment() {
    companion object {
        const val searchHint = "Search cookbook..."
    }

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

        val adapter = RecipeAdapter(viewModel){}
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

        //click into search page
        binding.cookbookActionSearch.setOnClickListener{
            val action = CookbookFragmentDirections.actionCookbookToSearch(searchHint)
            findNavController().navigate(action)
        }
    }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}