package com.example.platepal.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.platepal.R
import com.example.platepal.databinding.LogInFragmentBinding
import com.example.platepal.ui.DiscoverFragment
import com.example.platepal.ui.MainViewModel
import com.google.firebase.auth.FirebaseAuth


class LogInFragment : Fragment() {

    private var _binding: LogInFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = LogInFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Log.d(javaClass.simpleName, "onViewCreated")
        _binding = LogInFragmentBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        viewModel.setTitle("Log In")

        navController = findNavController()

        ////

        firebaseAuth= FirebaseAuth.getInstance()

        binding.buttonSignIn.setOnClickListener {

            val email = binding.logInEmail.text.toString()
            val password = binding.logInPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                    if(it.isSuccessful){
                        val intent = Intent(requireContext(), DiscoverFragment::class.java)
                        startActivity(intent)
                    }else{
                        Toast.makeText(requireContext(),it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(requireContext(),"Fields can not be empty ",Toast.LENGTH_SHORT).show()
            }
        }

        binding.textRegister.setOnClickListener {
            val signUpIntent = Intent(requireContext(), RegisterFragment::class.java)
            startActivity(signUpIntent)
        }

        binding.textForgot.setOnClickListener {
            val forgotIntent = Intent(requireContext(), NewPasswordFragment::class.java)
            startActivity(forgotIntent)
        }


    }

}