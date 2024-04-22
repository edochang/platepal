package com.example.platepal.ui.recipe

import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.platepal.databinding.OneRecipeDirectionsFragmentBinding
import com.example.platepal.ui.viewmodel.OneRecipeViewModel

class OneRecipeDirections : Fragment() {
    private var _binding: OneRecipeDirectionsFragmentBinding? = null
    private val binding get() = _binding!!
    private val oneRecipeViewModel: OneRecipeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = OneRecipeDirectionsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        oneRecipeViewModel.observeRecipeInfo().observe(viewLifecycleOwner) {
            binding.oneRecipeDirectionsEditText.text = Html.fromHtml(it.directions, Html.FROM_HTML_MODE_COMPACT)
        }
    }
}