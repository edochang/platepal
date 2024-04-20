package com.example.platepal.ui.viewmodel

import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.platepal.data.PostMeta
import com.example.platepal.data.StorageDirectory
import com.example.platepal.repository.PostsDBHelper
import com.example.platepal.repository.Storage
import edu.cs371m.reddit.glide.Glide

class PostViewModel : ViewModel() {
    companion object {
        const val TAG = "PostViewModel"
    }

    // DBHelpers
    private val postsDBHelper = PostsDBHelper()
    private var posts = MutableLiveData<List<PostMeta>>()

    // Repositories
    private val storage = Storage()

    // Observers
    fun observePosts(): LiveData<List<PostMeta>> {
        return posts
    }

    // Public functions
    fun fetchPosts() {
        postsDBHelper.realTimeReadPosts {
            posts.postValue(it)
        }
    }

    fun fetchPostPhoto(image: String, imageView: ImageView) {
        Glide.fetchFromStorage(
            storage.uuid2StorageReference(image, StorageDirectory.POST),
            imageView
        )
    }
}