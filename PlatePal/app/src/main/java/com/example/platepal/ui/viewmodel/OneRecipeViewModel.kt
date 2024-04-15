package com.example.platepal.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.platepal.MainActivity
import com.example.platepal.api.SpoonacularApi
import com.example.platepal.data.DummyRepository
import com.example.platepal.data.RecipeInfoMeta
import com.example.platepal.data.RecipeMeta
import com.example.platepal.data.SpoonacularRecipe
import com.example.platepal.data.SpoonacularRecipeInfo
import com.example.platepal.repository.RecipeInfoDBHelper
import com.example.platepal.repository.RecipesDBHelper
import com.example.platepal.repository.SpoonacularRecipeRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Duration

private const val TAG = "OneRecipeViewModel"
class OneRecipeViewModel: ViewModel() {
    // DBHelpers
    private val recipeInfoDBHelper = RecipeInfoDBHelper()

    // API Interfaces
    private val spoonacularApi = SpoonacularApi.create()

    // Repositories
    private val spoonacularRecipeRepository = SpoonacularRecipeRepository(spoonacularApi)

    private val recipeInfo = MutableLiveData<RecipeInfoMeta>()
    private var recipeSourceId = ""

    // Observers
    fun observeRecipeInfo(): LiveData<RecipeInfoMeta> {
        return recipeInfo
    }

    // Setters
    fun setRecipeSourceId(sourceId: String) {
        recipeSourceId = sourceId
    }

    // Public helper functions
    fun fetchReposRecipeInfo(resultListener:() -> Unit) {
        recipeInfoDBHelper.getRecipeInfo(recipeSourceId) {
            if(it.isEmpty()) {
                viewModelScope.launch(Dispatchers.IO) {
                    val spoonacularRecipeInfo = if(MainActivity.globalDebug) {
                        DummyRepository().fetchRecipeInfoData() // Used for testing
                    } else {
                        spoonacularRecipeRepository.getRecipeInfo(recipeSourceId)
                    }

                    val recipeInfoMeta = convertSpoonacularRecipeToRecipeMeta(spoonacularRecipeInfo)

                    recipeInfoDBHelper.createDocuments(recipeInfoMeta) { createdRecipeInfoMeta ->
                        recipeInfo.postValue(createdRecipeInfoMeta.first())
                    }
                }
            } else {
                recipeInfo.postValue(it.first())
            }

            resultListener.invoke()
        }
    }

    // Private helper functions
    private fun convertSpoonacularRecipeToRecipeMeta(spoonacularRecipeInfo: SpoonacularRecipeInfo): RecipeInfoMeta {
        var ingredients = "<ul>"
        spoonacularRecipeInfo.ingredients.forEach {
            ingredients = "$ingredients<li>${it.ingredient}</li>"
        }
        ingredients = "$ingredients</ul>"

        var notes = spoonacularRecipeInfo.summary
        notes = "$notes <br/><br/>" +
                "Servings: ${spoonacularRecipeInfo.servings}<br/>" +
                "Ready in Minutes: ${spoonacularRecipeInfo.readyInMinutes}<br/>" +
                "Recipe Sourced From: ${spoonacularRecipeInfo.sourceName} " +
                    "(url: ${spoonacularRecipeInfo.sourceUrl})<br/>"

        val instruction = spoonacularRecipeInfo.instructions ?: "Be creative, no directions!"

        return RecipeInfoMeta(
            spoonacularRecipeInfo.id.toString(),
            ingredients,
            spoonacularRecipeInfo.ingredients,
            instruction,
            notes
        )
    }
}