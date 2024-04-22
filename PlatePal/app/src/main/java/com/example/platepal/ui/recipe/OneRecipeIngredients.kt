package com.example.platepal.ui.recipe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.platepal.MainActivity
import com.example.platepal.databinding.OneRecipeIngredientsFragmentBinding
import com.example.platepal.ui.viewmodel.OneRecipeViewModel

class OneRecipeIngredients : Fragment() {
    companion object {
        const val TAG = "OneRecipeIngredients"
    }

    private var _binding: OneRecipeIngredientsFragmentBinding? = null
    private val binding get() = _binding!!
    private val oneRecipeViewModel: OneRecipeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = OneRecipeIngredientsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        oneRecipeViewModel.observeRecipeInfo().observe(viewLifecycleOwner) {
            binding.oneRecipeIngredientsEditText.text =
                Html.fromHtml(it.ingredients, Html.FROM_HTML_MODE_COMPACT)
        }
    }
}