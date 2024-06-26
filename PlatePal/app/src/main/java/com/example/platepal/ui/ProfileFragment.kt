package com.example.platepal.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.example.platepal.MainActivity
import com.example.platepal.camera.TakePictureWrapper
import com.example.platepal.databinding.ProfileFragmentBinding
import com.example.platepal.ui.viewmodel.MainViewModel
import com.example.platepal.ui.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions

class ProfileFragment : Fragment() {
    companion object {
        const val TAG = "ProfileFragment"
    }

    private var _binding: ProfileFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var mainActivity: MainActivity
    private val db = FirebaseFirestore.getInstance()
    private lateinit var listenerRegistration1: ListenerRegistration
    private lateinit var listenerRegistration2: ListenerRegistration
    private var removeListener: Boolean = false


    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            userViewModel.getProfilePhotoFile()?.let {
                if (it.exists()) {
                    userViewModel.pictureReplace()
                    Log.d(TAG, "camera click success - pic reset")
                }
            }
            setPhoto()
            Log.d(TAG, "camera click success - pic binding local")

        } else {
            userViewModel.getProfilePhotoFile()?.let {
                userViewModel.setProfilePhotoUUID(it.nameWithoutExtension)
            } ?: userViewModel.pictureReset()
            Log.d(TAG, "camera click failure - pic reset")
            Log.d(TAG, "camera click failure - pic reset, current UUID: $")
        }
    }

    private fun setPhoto() {
        val pictureUUID = userViewModel.getProfilePhotoUUID()
        Log.d(TAG, "setPhoto: UUID is $pictureUUID")
        userViewModel.setProfilePhotoFile(TakePictureWrapper.fileNameToFile(pictureUUID))
        Log.d(TAG, "setPhoto: FILE is ${userViewModel.getProfilePhotoFile()}")
        userViewModel.fetchLocalProfilePhoto(binding.profileImage)
    }

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
        super.onViewCreated(view, savedInstanceState)
        //Log.d(javaClass.simpleName, "onViewCreated")
        _binding = ProfileFragmentBinding.bind(view)
        mainActivity = (requireActivity() as MainActivity)
        viewModel.setTitle("Profile")

        val userID = FirebaseAuth.getInstance().currentUser?.uid

        //filter query based on uid so there is only one document
        val docRef = db.collection("Users").whereEqualTo("uid", userID)
        listenerRegistration1 = docRef.addSnapshotListener { value, error ->
            //Log.d(TAG, "length of ref query result is ${value?.size()}")
            if (error != null) {
                Log.d(TAG, "snapshot listener failed: $error")
                return@addSnapshotListener
            }
            if (value != null) {
                for (doc in value) {
                    //Log.d(TAG, "this is the doc id ${doc.id}")
                    if (doc.get("uid")?.toString() == userID) {
                        binding.profileName.text = doc.get("fullName")?.toString()
                        binding.profileEmail.text = doc.get("email")?.toString()

                        if (userViewModel.getProfilePhotoFile() != null) {
                            userViewModel.fetchLocalProfilePhoto(
                                binding.profileImage
                            )
                            Log.d(TAG, "First profile bind: from local file: ${userViewModel.getProfilePhotoUUID()}")
                        }else if(doc.get("pictureUUID")?.toString()?.isNotEmpty() == true){
                            // first add the pictureUUID to UUID list
                            userViewModel.setPreviousUUID(doc.get("pictureUUID")?.toString()!!)

                            //fetch from storage & display
                            userViewModel.fetchProfilePhoto(
                                doc.get("pictureUUID")?.toString()!!,
                                binding.profileImage
                            )
                            Log.d(
                                TAG,
                                "First profile bind: from cloud storage: ${doc.get("pictureUUID")}"
                            )
                        } else {
                            Log.d(
                                TAG,
                                "First profile bind: User currently doesn't have a profile pic"
                            )
                        }
                        break
                    }
                }
            }
        }

        binding.editPicture.setOnClickListener {
            TakePictureWrapper.takeProfilePicture(mainActivity, userViewModel, cameraLauncher)
        }


        binding.savePicture.setOnClickListener {
            Log.d(TAG, "Current pictureFILE: ${userViewModel.getProfilePhotoFile()}")
            Log.d(TAG, "Current pictureFILE: ${userViewModel.getProfilePhotoUUID()}")
            //first delete the previous profile pic from storage, if there is one
            if(userViewModel.getPreviousUUID().isNotEmpty()){
                userViewModel.deletePreviousProfile(userViewModel.getPreviousUUID())
            }
            //then save the current uuid
            userViewModel.setPreviousUUID(userViewModel.getProfilePhotoUUID())
            //upload current profile to storage
            userViewModel.profilePhotoSuccess()
            val data = hashMapOf<String, Any>("pictureUUID" to userViewModel.getProfilePhotoUUID())
            val query = db.collection("Users").whereEqualTo("uid", userID)
            listenerRegistration2 = query.addSnapshotListener { value, error ->
                if (error != null) {
                    Log.d(TAG, "snapshot listener failed: $error")
                    return@addSnapshotListener
                }
                if (value != null) {
                    for (doc in value) {
                        //update newest picture reference in user meta
                        doc.reference.set(data, SetOptions.merge())
                        Log.d(
                            TAG,
                            "Click listener: added newest ${userViewModel.getProfilePhotoUUID()} in existing User meta"
                        )
                        break
                    }
                }
            }
            Toast.makeText(context, "Profile picture saved", Toast.LENGTH_SHORT).show()
            removeListener = true
        }

        binding.profileLogout.setOnClickListener {
            userViewModel.pictureReset()
            userViewModel.setPreviousUUID("")
            Log.d(TAG, "picture reset")
            mainActivity.logout()
        }
    }


    override fun onDestroyView() {
        _binding = null
        listenerRegistration1.remove()
        if (removeListener) listenerRegistration2.remove()
        super.onDestroyView()
    }


}
