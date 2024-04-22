package com.example.platepal.ui.community

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.platepal.R
import com.example.platepal.data.PostMeta
import com.example.platepal.databinding.CommunityPostCardBinding
import com.example.platepal.ui.viewmodel.OnePostViewModel
import com.example.platepal.ui.viewmodel.PostViewModel
import com.example.platepal.ui.viewmodel.UserViewModel

class PostAdapter(
    private val viewModel: PostViewModel,
    private val userViewModel: UserViewModel,
    private val navigateToOnePost: (PostMeta) -> Unit  // TODO: Not MVP, but could create a one post view
) : ListAdapter<PostMeta, PostAdapter.VH>(PostDiff()) {
    companion object {
        const val TAG = "PostAdapter"
    }

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

        val userRecord = userViewModel.userMetaList.filter {
            it.uid == item.createdBy
        }
        val user = if (userRecord.isNotEmpty()) userRecord.first()
        else null
        //Log.d(TAG, "Size: ${userViewModel.userMetaList.size}:: ${userViewModel.userMetaList}")
        //Log.d(TAG, "Size: ${userRecord.size}:: $userRecord")

        user?.let {
            if (it.pictureUUID.isNotEmpty()) {
                binding.postUserProfilePicture.setImageResource(R.drawable.transparent)
                binding.postUserProfilePicture.setBackgroundColor(Color.Transparent.hashCode())
                binding.postUserProfilePicture.imageTintList = null
                userViewModel.fetchProfilePhoto(it.pictureUUID, binding.postUserProfilePicture)
            }
        }

        binding.postUsername.text = user?.fullName ?: "Anonymous Chef"
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