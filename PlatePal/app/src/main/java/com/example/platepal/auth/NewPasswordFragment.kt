package com.example.platepal.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.platepal.R
import com.example.platepal.ui.MainViewModel
import com.example.platepal.databinding.NewPasswordFragmentBinding
import com.google.firebase.auth.FirebaseAuth


class NewPasswordFragment : Fragment() {

    private var _binding: NewPasswordFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = NewPasswordFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

}