package com.example.platepal.ui.community

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.platepal.databinding.CommunityFragmentBinding
import com.example.platepal.ui.viewmodel.MainViewModel

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
        //Log.d(javaClass.simpleName, "onViewCreated")
        _binding = CommunityFragmentBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        viewModel.setTitle("PlatePal")


    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


}