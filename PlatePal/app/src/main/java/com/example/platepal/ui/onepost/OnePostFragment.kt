package com.example.platepal.ui.onepost

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.platepal.R
import com.example.platepal.databinding.OnePostFragmentBinding
import com.example.platepal.ui.MainViewModel
import com.example.platepal.ui.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
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

        viewModel.setTitle("Recipe")
        binding.onePostTitle.text = args.post.title

        Glide.glideFetch(args.post.image, args.post.image, binding.onePostImage)

        //spotlight favorites
        viewModel.isFavoriteRecipe(args.post)?.let{
            if (it) binding.onePostHeart.setImageResource(R.drawable.ic_heart_filled)
            else binding.onePostHeart.setImageResource(R.drawable.ic_heart_empty)
        }

        binding.onePostHeart.setOnClickListener{
            Log.d(javaClass.simpleName, "heart clicklistener")
            viewModel.isFavoriteRecipe(args.post)?.let{
                if(it){
                    viewModel.setFavoriteRecipe(args.post, false)
                    binding.onePostHeart.setImageResource(R.drawable.ic_heart_empty)
                    Log.d(javaClass.simpleName, "set heart to empty")
                } else{
                    viewModel.setFavoriteRecipe(args.post, true)
                    binding.onePostHeart.setImageResource(R.drawable.ic_heart_filled)
                    Log.d(javaClass.simpleName, "set heart to filled")
                }
            }
        }

        val fragmentsList = arrayListOf(PostIngredients(), PostDirections(), PostNotes())

        binding.apply {
            viewPager.adapter = ViewPagerAdapter(fragmentsList, this@OnePostFragment.childFragmentManager, lifecycle)

            TabLayoutMediator(tabView, viewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = "Ingredients"
                    1 -> tab.text = "Directions"
                    2 -> tab.text = "Notes"
                }
            }.attach()
        }

    }

        override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}