package com.example.platepal

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.platepal.data.UserMeta
import com.example.platepal.databinding.ActivityRegisterBinding
import com.example.platepal.databinding.ActivitySignInBinding
import com.example.platepal.repository.UserDBHelper
import com.example.platepal.ui.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding : ActivityRegisterBinding
    private lateinit var progressbar: ProgressBar
    private val userViewModel = UserViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        progressbar = binding.progressBar

        progressbar.visibility = View.GONE

        binding.buttonSignUp.setOnClickListener {
            val username = binding.registerUsername.text.toString()
            val email = binding.registerEmail.text.toString()
            val password = binding.registerPassword.text.toString()
            val rePassword = binding.registerReenterPassword.text.toString()
            progressbar.visibility = View.VISIBLE

            if(username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && rePassword.isNotEmpty() ){
                if (password == rePassword ){
                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(baseContext, "success",
                                Toast.LENGTH_SHORT).show()

                            val userID = auth.currentUser?.uid.toString()
                            userViewModel.createUserMeta(username, email, userID)

                            val intent = Intent(this, SignInActivity::class.java)
                            startActivity(intent)
                        }else{
                            Toast.makeText(baseContext,"Password must be at least 6 characters long. Please try again.",Toast.LENGTH_SHORT).show()
                            progressbar.visibility = View.GONE
                        }
                    }
                }else{
                    Toast.makeText(baseContext,"Passwords do not matched",Toast.LENGTH_SHORT).show()
                    progressbar.visibility = View.GONE
                }
            }else{
                Toast.makeText(baseContext,"Fields can not be empty ",Toast.LENGTH_SHORT).show()
                progressbar.visibility = View.GONE
            }
        }

        binding.textSignIn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

    }
}

