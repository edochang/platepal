package com.example.platepal.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.platepal.auth.AuthUser
import com.example.platepal.databinding.ProfileFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.example.platepal.R

class ProfileFragment : Fragment() {
    private var _binding: ProfileFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var authUser : AuthUser
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = ProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Log.d(javaClass.simpleName, "onViewCreated")
        _binding = ProfileFragmentBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        viewModel.setTitle("Profile")
        navController = findNavController()

        // Create authentication object.  This will log the user in if needed
        authUser = AuthUser(requireActivity().activityResultRegistry)
        // authUser needs to observe our lifecycle so it can run login activity
        lifecycle.addObserver(authUser)

        authUser.observeUser().observe(viewLifecycleOwner) {
            // XXX Write me, user status has changed
            viewModel.setCurrentAuthUser(it)
            binding.profileName.text = it.name
            binding.profileEmail.text = it.email
        }

        binding.profileLogout.setOnClickListener{
            authUser.logout()
        }

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}