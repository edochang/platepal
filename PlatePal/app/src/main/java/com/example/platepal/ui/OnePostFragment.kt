package com.example.platepal.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.platepal.R
import com.example.platepal.databinding.OnePostFragmentBinding
import edu.cs371m.reddit.glide.Glide

class OnePostFragment : Fragment() {

    private var _binding: OnePostFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private val args: OnePostFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = OnePostFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Log.d(javaClass.simpleName, "onViewCreated")
        _binding = OnePostFragmentBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        viewModel.setTitle("PlatePal")
        binding.onePostTitle.text = args.post.title

        Glide.glideFetch(args.post.image, args.post.image, binding.onePostImage)
    }

        override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}