package com.example.platepal.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.platepal.MainActivity
import com.example.platepal.databinding.ProfileFragmentBinding
import com.example.platepal.repository.UserDBHelper
import com.example.platepal.ui.viewmodel.MainViewModel
import com.example.platepal.ui.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

private const val TAG = "ProfileFragment"
class ProfileFragment : Fragment() {
    private var _binding: ProfileFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var mainActivity: MainActivity
    private val db = FirebaseFirestore.getInstance()
    private lateinit var listenerRegistration: ListenerRegistration

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
        mainActivity = (requireActivity() as MainActivity)

        viewModel.setTitle("Profile")

         val userID = FirebaseAuth.getInstance().currentUser?.uid

        //filter query based on uid so there is only one document
         val docRef = db.collection("Users").whereEqualTo("uid", userID)
         listenerRegistration = docRef.addSnapshotListener { value, error ->
            //Log.d(TAG, "length of ref query result is ${value?.size()}")
            if (error != null){
                Log.d(TAG, "snapshot listener failed: $error")
                return@addSnapshotListener
            }
            if (value != null) {
                for (doc in value){
                    //Log.d(TAG, "this is the doc id ${doc.id}")
                    if(doc.get("uid")?.toString() == userID){
                        binding.profileName.text = doc.get("fullName")?.toString()
                        binding.profileEmail.text = doc.get("email")?.toString()
                        break
                    }
                }
            }
        }

        binding.profileLogout.setOnClickListener{
            mainActivity.logout()
        }

    }
    override fun onDestroyView() {
        _binding = null
        listenerRegistration.remove()
        super.onDestroyView()
    }

}