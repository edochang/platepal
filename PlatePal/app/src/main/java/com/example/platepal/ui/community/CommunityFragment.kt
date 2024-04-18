package com.example.platepal.ui.community

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.platepal.MainActivity
import com.example.platepal.databinding.CommunityFragmentBinding
import com.example.platepal.ui.viewmodel.MainViewModel

private const val TAG = "CommunityFragment"

class CommunityFragment : Fragment() {
    private var _binding: CommunityFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = CommunityFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        _binding = CommunityFragmentBinding.bind(view)
        viewModel.setTitle("PlatePal")

        binding.communityPost.setOnClickListener {
            val action = CommunityFragmentDirections.actionCommunityToCreateOnePost(
                null,
                MainActivity.ONEPOST_TRIGGER_TEXTVIEW)
            findNavController().navigate(action)
        }

        binding.communityPostPicture.setOnClickListener {
            val action = CommunityFragmentDirections.actionCommunityToCreateOnePost(
                null,
                MainActivity.ONEPOST_TRIGGER_CAMERA)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


}