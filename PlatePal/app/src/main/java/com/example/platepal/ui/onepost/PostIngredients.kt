package com.example.platepal.ui.onepost

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.platepal.R
import com.example.platepal.databinding.PostIngredientsFragmentBinding


class PostIngredients : Fragment() {

    private var _binding: PostIngredientsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = PostIngredientsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


}