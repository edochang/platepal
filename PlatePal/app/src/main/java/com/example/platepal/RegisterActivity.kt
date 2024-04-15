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
import com.example.platepal.databinding.ActivityRegisterBinding
import com.example.platepal.databinding.ActivitySignInBinding
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
                            val intent = Intent(this, SignInActivity::class.java)
                            startActivity(intent)

                            /*
                            val user = auth.currentUser
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(username) // Set the name
                                //can also set photourl?
                                .build()
                            user?.updateProfile(profileUpdates)
                                ?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // Name updated successfully
                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                    } else {
                                        // Name update failed
                                        Log.d("name update", "failed")
                                    }
                                }

                            val userUpdates = hashMapOf<String, Any>(
                                "email" to email // Set the email
                            )
                            val db = FirebaseFirestore.getInstance()
                            db.collection("users").document(user?.uid.toString())
                                .set(userUpdates)
                                .addOnSuccessListener { documentReference ->
                                    // email added successfully
                                    Log.d("email added", "success")
                                }
                                .addOnFailureListener { e ->
                                    // email addition failed
                                    Log.d("email added", "success")
                                }

                             */
                        }else{
                            Toast.makeText(baseContext,"Cannot register account. Please try again.",Toast.LENGTH_SHORT).show()
                            progressbar.visibility = View.GONE
                        }
                    }
                }else{
                    Toast.makeText(baseContext,"Password does not matched",Toast.LENGTH_SHORT).show()
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