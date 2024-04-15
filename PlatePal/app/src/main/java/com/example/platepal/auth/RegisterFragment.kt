package com.example.platepal.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.platepal.databinding.RegisterFragmentBinding
import com.example.platepal.ui.MainViewModel
import com.google.firebase.auth.FirebaseAuth


class RegisterFragment : Fragment() {

    private var _binding: RegisterFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = RegisterFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Log.d(javaClass.simpleName, "onViewCreated")
        _binding = RegisterFragmentBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        viewModel.setTitle("Register")

        firebaseAuth = FirebaseAuth.getInstance()

        binding.buttonSignUp.setOnClickListener {
            val username = binding.registerUsername.text.toString()
            val email = binding.registerEmail.text.toString()
            val password = binding.registerPassword.text.toString()
            val rePassword = binding.registerReenterPassword.text.toString()


            if(username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && rePassword.isNotEmpty() ){
                if (password == rePassword ){
                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                        if (it.isSuccessful){
                            val intent = Intent(requireContext(), LogInFragment::class.java)
                            startActivity(intent)
                        }else{
                            Toast.makeText(requireContext(),it.exception.toString(),Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(requireContext(),"Password does not matched",Toast.LENGTH_SHORT).show()

                }
            }else{
                Toast.makeText(requireContext(),"Fields can not be empty ",Toast.LENGTH_SHORT).show()

            }
        }

        binding.textSignIn.setOnClickListener {
            val signUpIntent = Intent(requireContext(), LogInFragment::class.java)
            startActivity(signUpIntent)
        }


    }

}