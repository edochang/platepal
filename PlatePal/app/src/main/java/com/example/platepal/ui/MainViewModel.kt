package com.example.platepal.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.platepal.api.SpoonacularRecipe
import com.example.platepal.api.Repository


class MainViewModel: ViewModel() {
    private var repository = Repository()
    // Maintain a list of all Data items
    private var list = repository.fetchData()

    //favorite (cookbook)
    private val favList = mutableListOf<SpoonacularRecipe>()
    private val favListLive = MutableLiveData<List<SpoonacularRecipe>>()


    //Favorites
    fun isFavorite(favItem: SpoonacularRecipe): Boolean {
        return favList.contains(favItem)
    }
    fun addFavorite(favItem: SpoonacularRecipe) {
        favList.add(favItem)
    }
    fun removeFavorite(favItem: SpoonacularRecipe) {
        favList.remove(favItem)
    }

    fun setFavList(){
        favListLive.value = favList
    }

    fun faveListSize(): Int{
        return favList.size
    }

    fun observeFavListLive(): LiveData<List<SpoonacularRecipe>> {
        return favListLive
    }


}