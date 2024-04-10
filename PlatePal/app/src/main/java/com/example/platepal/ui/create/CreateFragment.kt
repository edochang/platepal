package com.example.platepal.ui.create

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.platepal.R
import com.example.platepal.databinding.CreateFragmentBinding
import com.google.android.material.tabs.TabLayoutMediator

class CreateFragment : Fragment() {
    private var _binding: CreateFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = CreateFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")

        val fragmentsList = arrayListOf(Ingredients(), Directions(), Notes())

        binding.apply {
            viewPager.adapter = ViewPagerAdapter(fragmentsList, this@CreateFragment.childFragmentManager, lifecycle)

            TabLayoutMediator(tabView, viewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = "Ingredients"
                    1 -> tab.text = "Directions"
                    2 -> tab.text = "Notes"
                }
            }.attach()

        }
    }

}