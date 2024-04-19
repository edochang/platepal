package com.example.platepal.ui


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.example.platepal.MainActivity
import com.example.platepal.camera.TakePictureWrapper
import com.example.platepal.databinding.ProfileFragmentBinding
import com.example.platepal.repository.Storage
import com.example.platepal.repository.UserDBHelper
import com.example.platepal.ui.viewmodel.MainViewModel
import com.example.platepal.ui.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import edu.cs371m.reddit.glide.Glide


private const val TAG = "ProfileFragment"
class ProfileFragment : Fragment() {
    private var _binding: ProfileFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var mainActivity: MainActivity
    private val db = FirebaseFirestore.getInstance()
    private lateinit var listenerRegistration: ListenerRegistration
    private val storage = Storage()


    //note that when camera is successful
    //ProfilePhotoSuccess (which contains uploadProfileImage) is triggered.
    //UploadProfileIMage deletes the current local file, profilePhotoFile
    //and its callback also makes profilePhotoUUID = ""
    //This means we can't reference either of those things to update our UserMeta....
    //so now I'm not deleting either profilePhotoFile or profilePhotoUUID...
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            //upload to storage
            userViewModel.profilePhotoSuccess()
            Log.d(TAG, "camera click success - profilePhotoSuccess was called")
        } else {
            userViewModel.profilePhotoFailure()
            Log.d(TAG, "camera click failure - profilePhotoFailure was called")
        }
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
                        //userviewmodel.fetchlocalprofilephoto or fetchprofilephoto
                        if(userViewModel.getProfilePhotoFile() != null){
                            userViewModel.fetchLocalProfilePhoto(
                                binding.profileImage
                            )
                            Log.d(TAG, "First profile bind: from local file: ${userViewModel.getProfilePhotoUUID()}")
                        }else if(doc.get("pictureUUID")?.toString()?.isNotEmpty() == true){
                            userViewModel.fetchProfilePhoto(
                                doc.get("pictureUUID")?.toString()!!,
                                binding.profileImage
                            )
                            Log.d(TAG, "First profile bind: from cloud storage: ${doc.get("pictureUUID")}")
                        }else{
                            Log.d(TAG, "First profile bind: User currently doesn't have a profile pic")
                        }
                        break
                    }
                }
            }
        }


        binding.editPicture.setOnClickListener {
            //get rid of previously saved local pic & UUID
            userViewModel.pictureReset()
            //take picture, save current pictureUUID into viewModel, upload picture into storage
            TakePictureWrapper.takeProfilePicture(mainActivity, userViewModel, cameraLauncher)
            val data = hashMapOf<String, Any>("pictureUUID" to userViewModel.getProfilePhotoUUID())
            val query = db.collection("Users").whereEqualTo("uid", userID)
            listenerRegistration = query.addSnapshotListener { value, error ->
                if (error != null){
                    Log.d(TAG, "snapshot listener failed: $error")
                    return@addSnapshotListener
                }
                if (value != null) {
                    for (doc in value){
                        //at this point, the newest profile is not linked to User Meta
                        //delete in storage the current picture linked in User Meta, if any
                        //perhaps add a condition that it is not the same as the current uuid?
                        //and then log it to know if that will ever be the case
                        /*
                        if (doc.get("pictureUUID").toString().isNotEmpty()) {
                            storage.deleteProfileImage(doc.get("pictureUUID").toString())
                            Log.d(TAG, "deleted old profile picture from storage")
                        }
                         */
                        //update newest picture reference in user meta
                        doc.reference.set(data, SetOptions.merge())
                        Log.d(TAG, "Click listener: added newest pictureUUID in existing User meta")
                        // display current picture in profile
                        /*
                        userViewModel.fetchProfilePhoto(
                            userViewModel.getProfilePhotoUUID(),
                            binding.profileImage
                        )

                         */

                        if(userViewModel.getProfilePhotoFile() != null){
                            userViewModel.fetchLocalProfilePhoto(binding.profileImage)
                            Log.d(TAG, "Click listener: user got profile from local file: ${userViewModel.getProfilePhotoUUID()}")
                        }
                        else if(doc.get("pictureUUID")?.toString()?.isNotEmpty() == true){
                            userViewModel.fetchProfilePhoto(
                                doc.get("pictureUUID")?.toString()!!,
                                binding.profileImage
                            )
                            Log.d(TAG, "Click listener: user got profile from cloud: ${doc.get("pictureUUID")}")
                        }

                        break
                    }
                }
            }
        }


        binding.profileLogout.setOnClickListener{
            userViewModel.pictureReset()
            Log.d(TAG, "picture reset")
            mainActivity.logout()
        }


    }
    override fun onDestroyView() {
        _binding = null
        listenerRegistration.remove()
        super.onDestroyView()
    }


}
