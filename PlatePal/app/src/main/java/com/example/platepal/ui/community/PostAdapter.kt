package com.example.platepal.ui.community

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.platepal.data.PostMeta
import com.example.platepal.databinding.CommunityPostCardBinding
import com.example.platepal.ui.viewmodel.OnePostViewModel
import com.example.platepal.ui.viewmodel.PostViewModel

class PostAdapter(
    private val viewModel: PostViewModel,
    private val navigateToOnePost: (PostMeta) -> Unit
) : ListAdapter<PostMeta, PostAdapter.VH>(PostDiff()) {
    inner class VH(val communityPostCardBinding: CommunityPostCardBinding) :
        RecyclerView.ViewHolder(communityPostCardBinding.root) {
        //init {}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val rowBinding = CommunityPostCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(rowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        val binding = holder.communityPostCardBinding

        binding.postUsername.text = item.createdBy
        viewModel.fetchPostPhoto(item.picture, binding.postImage)
        binding.postRecipe.text = item.recipeTitle
        binding.postBody.text = item.postMessage
    }

    class PostDiff : DiffUtil.ItemCallback<PostMeta>() {
        override fun areItemsTheSame(oldItem: PostMeta, newItem: PostMeta): Boolean {
            return oldItem.firestoreId == newItem.firestoreId
        }

        override fun areContentsTheSame(oldItem: PostMeta, newItem: PostMeta): Boolean {
            return oldItem.recipeSourceId == newItem.recipeSourceId &&
                    oldItem.recipeTitle == newItem.recipeTitle &&
                    oldItem.postMessage == newItem.postMessage &&
                    oldItem.pictureLabel == newItem.pictureLabel &&
                    oldItem.picture == newItem.picture &&
                    oldItem.createdBy == newItem.createdBy &&
                    oldItem.timeStamp.toString() == newItem.timeStamp.toString()
        }
    }
}