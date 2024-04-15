package com.example.platepal.ui.onepost

import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.platepal.R
import com.example.platepal.databinding.PostDirectionsFragmentBinding
import com.example.platepal.ui.viewmodel.OneRecipeViewModel

class PostDirections : Fragment() {
    private var _binding: PostDirectionsFragmentBinding? = null
    private val binding get() = _binding!!
    private val oneRecipeViewModel: OneRecipeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = PostDirectionsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        oneRecipeViewModel.observeRecipeInfo().observe(viewLifecycleOwner) {
            binding.onePostDirectionsEditText.text = Html.fromHtml(it.directions, Html.FROM_HTML_MODE_COMPACT)
        }
    }
}