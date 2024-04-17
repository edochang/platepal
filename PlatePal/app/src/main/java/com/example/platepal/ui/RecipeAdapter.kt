package com.example.platepal.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.platepal.R
import com.example.platepal.data.RecipeMeta
import com.example.platepal.databinding.RecipeCardBinding
import com.example.platepal.ui.viewmodel.MainViewModel
import com.example.platepal.ui.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.cs371m.reddit.glide.Glide

private const val TAG = "RecipeAdapter"
class RecipeAdapter(private val userViewModel: UserViewModel,
                    private val navigateToOneRecipe: (RecipeMeta)->Unit)
    : ListAdapter<RecipeMeta, RecipeAdapter.VH>(RecipeDiff())
{

    inner class VH(val recipeCardBinding: RecipeCardBinding)
        : RecyclerView.ViewHolder(recipeCardBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val cardBinding = RecipeCardBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)

        return VH(cardBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        val cardBinding = holder.recipeCardBinding

        //bind the post title & likes and comment counts
        cardBinding.recipeTitle.text = item.title
        Glide.glideFetch(item.image, item.image, cardBinding.recipeImage)
        Log.d(javaClass.simpleName, "onBindViewHolder")

        /*
        userViewModel.fetchInitialFavRecipes{
            notifyItemChanged(position)
            Log.d(TAG, "favorite recipe list listener invoked")
        }

         */


        userViewModel.isFavoriteRecipe(item)?.let{
            if (it) cardBinding.heart.setImageResource(R.drawable.ic_heart_filled)
            else cardBinding.heart.setImageResource(R.drawable.ic_heart_empty)
            Log.d(TAG, "set favorite icon")
        }

        cardBinding.heart.setOnClickListener{
            //Log.d(javaClass.simpleName, "heart clicklistener")
            userViewModel.isFavoriteRecipe(item)?.let{
                if(it){
                    userViewModel.setFavoriteRecipe(item, false)
                    //userViewModel.removeFavRecipe(item)
                    cardBinding.heart.setImageResource(R.drawable.ic_heart_empty)
                    //Log.d(javaClass.simpleName, "set heart to empty")
                } else{
                    userViewModel.setFavoriteRecipe(item, true)
                    //userViewModel.addFavRecipe(item)
                    cardBinding.heart.setImageResource(R.drawable.ic_heart_filled)
                    //Log.d(javaClass.simpleName, "set heart to filled")
                }
            }
        }

        cardBinding.recipeImage.setOnClickListener {
            navigateToOneRecipe(item)
        }

    }



    class RecipeDiff: DiffUtil.ItemCallback<RecipeMeta>() {
        // Item identity
        override fun areItemsTheSame(oldItem: RecipeMeta, newItem: RecipeMeta): Boolean {
            Log.d("RecipeDiff", "areItemsTheSame triggered")
            return oldItem.hashCode() == newItem.hashCode()
        }
        // Item contents are the same, but the object might have changed
        override fun areContentsTheSame(oldItem: RecipeMeta, newItem: RecipeMeta): Boolean {
            Log.d("RecipeDiff", "areContentsTheSame triggered")
            return oldItem.firestoreId == newItem.firestoreId
                    && oldItem.sourceId == newItem.sourceId
                    && oldItem.title == newItem.title
                    && oldItem.image == newItem.image
                    && oldItem.imageType == newItem.imageType
        }
    }
}

