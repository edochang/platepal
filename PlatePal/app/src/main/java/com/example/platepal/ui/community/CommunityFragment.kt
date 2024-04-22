package com.example.platepal.ui.community

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.platepal.MainActivity
import com.example.platepal.databinding.CommunityFragmentBinding
import com.example.platepal.ui.viewmodel.MainViewModel
import com.example.platepal.ui.viewmodel.PostViewModel
import com.example.platepal.ui.viewmodel.UserViewModel

class CommunityFragment : Fragment() {
    companion object {
        const val TAG = "CommunityFragment"
    }

    private var _binding: CommunityFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private val postViewModel: PostViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

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
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")
        _binding = CommunityFragmentBinding.bind(view)
        viewModel.setTitle("PlatePal")

        postViewModel.fetchPosts()

        // Init and bind adapter
        val adapter = PostAdapter(postViewModel, userViewModel) {

        }
        val communityRV = binding.communityRv
        communityRV.adapter = adapter
        // grid layout for RecyclerView
        val layoutManager = LinearLayoutManager(requireContext())
        communityRV.adapter = adapter
        communityRV.layoutManager = layoutManager

        val dividerItemDecoration = DividerItemDecoration(
            communityRV.context, LinearLayoutManager.VERTICAL
        )
        communityRV.addItemDecoration(dividerItemDecoration)

        postViewModel.observePosts().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        binding.communityPost.setOnClickListener {
            val action = CommunityFragmentDirections.actionCommunityToCreateOnePost(
                MainActivity.ONEPOST_TRIGGER_TEXTVIEW
            )
            findNavController().navigate(action)
        }

        binding.communityPostPicture.setOnClickListener {
            val action = CommunityFragmentDirections.actionCommunityToCreateOnePost(
                MainActivity.ONEPOST_TRIGGER_CAMERA
            )
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


}