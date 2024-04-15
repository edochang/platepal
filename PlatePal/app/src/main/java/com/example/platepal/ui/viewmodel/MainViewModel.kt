package com.example.platepal.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.platepal.MainActivity
import com.example.platepal.api.SpoonacularApi
import com.example.platepal.data.DummyRepository
import com.example.platepal.data.RecipeMeta
import com.example.platepal.data.SpoonacularRecipe
import com.example.platepal.repository.RecipesDBHelper
import com.example.platepal.repository.SpoonacularRecipeRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant

private const val TAG = "MainViewModel"

class MainViewModel: ViewModel() {
    // DBHelpers
    private val recipesDBHelper = RecipesDBHelper()

    // API Interfaces
    private val spoonacularApi = SpoonacularApi.create()

    // Repositories
    private val spoonacularRecipeRepository = SpoonacularRecipeRepository(spoonacularApi)

    // Maintain a list of all Recipe items
    private var recipeList = MutableLiveData<List<RecipeMeta>>()

    //title of the fragment
    private var title = MutableLiveData<String>()

    //title of the fragment
    private var randomSpotlightRecipe = MutableLiveData<RecipeMeta>()

    //favorite (cookbook)
    private val favList = MutableLiveData<List<RecipeMeta>>().apply{
        postValue(mutableListOf())
    }

    fun observeRecipeList(): LiveData<List<RecipeMeta>> {
        return recipeList
    }

    fun getRecipeList(): List<RecipeMeta> {
        return recipeList.value ?: emptyList()
    }

    // Getters
    fun getFavList(): MutableList<RecipeMeta>? {
        return favList.value?.toMutableList()
    }

    // Observers
    fun observeFavListLive(): LiveData<List<RecipeMeta>> {
        return favList
    }

    fun observeRandomSpotlightRecipe(): LiveData<RecipeMeta> {
        return randomSpotlightRecipe
    }

    fun observeTitle(): LiveData<String> {
        return title
    }

    // Setters
    fun setFavoriteRecipe(recipe: RecipeMeta, isFavorite: Boolean){
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

    fun setRandomRecipe() {
        Log.d(TAG, "Setting random recipe...")
        val recipes = recipeList.value
        Log.d(TAG, "From $recipes")
        val recipesSize = recipes?.size ?: 0
        recipes?.let {
            if (recipesSize > 0) {
                val rand = (0..< recipesSize).random()
                randomSpotlightRecipe.value = recipes[rand]
            }
        }
    }

    fun setTitle(newTitle: String) {
        title.value = newTitle
    }

    // Public helper functions
    fun fetchReposRecipeList(resultListener:() -> Unit) {
        recipesDBHelper.getRecipes {
            if(it.isEmpty()) {
                viewModelScope.launch(Dispatchers.IO) {
                    val spoonacularRecipes = if(MainActivity.globalDebug) {
                        DummyRepository().fetchData() // Used for testing
                    } else {
                        spoonacularRecipeRepository.getRecipes()
                    }

                    val recipeMetas = List(spoonacularRecipes.size) { index ->
                        convertSpoonacularRecipeToRecipeMeta(spoonacularRecipes[index])
                    }
                    recipesDBHelper.batchCreateDocuments(recipeMetas) {
                        fetchReposRecipeList(resultListener)
                    }
                }
            } else {
                // Given this is a query from Firestore, timeStamp will be guaranteed and
                // null-safety warnings will be overridden by !!.
                val mostRecentRecipe = it.maxBy { recipeMeta ->
                    recipeMeta.timeStamp!!
                }
                Log.d(TAG, "Most Recent Recipe: $mostRecentRecipe")
                val nowTimestampSeconds = Timestamp.now().seconds
                val dayDuration = Duration.ofDays(1).seconds
                val minStaleTimestamp = nowTimestampSeconds - dayDuration
                Log.d(
                    TAG,
                    "recentDuration: ${mostRecentRecipe.timeStamp?.seconds}, minStaleDuration: $minStaleTimestamp"
                )
                if(minStaleTimestamp > mostRecentRecipe.timeStamp!!.seconds) {
                    viewModelScope.launch(Dispatchers.IO) {
                        val spoonacularRecipes = if(MainActivity.globalDebug) {
                            DummyRepository().secondFetchData() // Used for testing
                        } else {
                            spoonacularRecipeRepository.getRecipes()
                        }

                        val recipeMetas = List(spoonacularRecipes.size) { index ->
                            convertSpoonacularRecipeToRecipeMeta(spoonacularRecipes[index])
                        }
                        val newRecipes = (recipeMetas.toSet() - it.toSet()).toList()
                        if(newRecipes.isNotEmpty()) {
                            Log.d(TAG, "New recipes received from Spoonacular: $newRecipes")
                            recipesDBHelper.batchCreateDocuments(newRecipes) {
                                fetchReposRecipeList(resultListener)
                            }
                        }
                    }
                }
            }

            recipeList.postValue(it)
            resultListener.invoke()
        }
    }

    fun isFavoriteRecipe(recipe: RecipeMeta): Boolean? {
        return favList.value?.contains(recipe)
    }

    // Private helper functions
    private fun convertSpoonacularRecipeToRecipeMeta(spoonacularRecipe: SpoonacularRecipe): RecipeMeta {
        return RecipeMeta(
            spoonacularRecipe.id.toString(),
            spoonacularRecipe.title,
            spoonacularRecipe.image,
            spoonacularRecipe.imageType
        )
    }
}