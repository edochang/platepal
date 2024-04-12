package com.example.platepal.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.platepal.data.SpoonacularRecipe
import com.example.platepal.data.DummyRepository


class MainViewModel: ViewModel() {

    //dummy repo
    private var dummyRepository = DummyRepository()
    // Maintain a list of all Recipe items
    private var list = dummyRepository.fetchData()

    //title of the fragment
    private var title = MutableLiveData<String>()

    // get a random recipe
    private var randomRecipe = dummyRepository.fetchRandomRecipe()


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

    fun observeTitle(): LiveData<String> {
        return title
    }
    fun setTitle(newTitle: String) {
        title.value = newTitle
    }

}