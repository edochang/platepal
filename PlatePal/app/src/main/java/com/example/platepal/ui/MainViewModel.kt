package com.example.platepal.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.platepal.api.SpoonacularRecipe
import com.example.platepal.api.Repository


class MainViewModel: ViewModel() {
    private var repository = Repository()
    // Maintain a list of all Recipe items
    private var list = repository.fetchData()

    // get a random recipe
    private var randomRecipe = repository.fetchRandomRecipe()


    //favorite (cookbook)
    private val favList = MutableLiveData<List<SpoonacularRecipe>>().apply{
        postValue(mutableListOf())
    }

    fun getCopyOfRecipeList(): MutableList<SpoonacularRecipe> {
        return list.toMutableList()
    }

    fun getRandomRecipe(): SpoonacularRecipe {
        return randomRecipe
    }


    //Favorites
    fun setFavoriteRecipe(recipe: SpoonacularRecipe, isFavorite: Boolean){
        if (isFavorite){
            //add favorite recipe
            favList.apply{
                this.value?.let{
                    val tempFavList = it.toMutableList()
                    tempFavList.add(recipe)
                    this.value = tempFavList
                }
            }
        } else {
            //remove favorite recipe
            favList.apply{
                this.value?.let{
                    val tempFavList = it.toMutableList()
                    tempFavList.remove(recipe)
                    this.value = tempFavList
                }
            }
        }
    }

    fun isFavoriteRecipe(recipe: SpoonacularRecipe): Boolean? {
        return favList.value?.contains(recipe)
    }

    fun observeFavListLive(): LiveData<List<SpoonacularRecipe>> {
        return favList
    }


}