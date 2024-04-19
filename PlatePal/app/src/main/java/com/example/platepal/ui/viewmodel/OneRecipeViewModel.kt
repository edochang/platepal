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
import com.example.platepal.data.SpoonacularRecipeInfo
import com.example.platepal.databinding.DirectionsFragmentBinding
import com.example.platepal.databinding.IngredientsFragmentBinding
import com.example.platepal.databinding.NotesFragmentBinding
import com.example.platepal.repository.RecipeInfoDBHelper
import com.example.platepal.repository.RecipesDBHelper
import com.example.platepal.repository.SpoonacularRecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "OneRecipeViewModel"

class OneRecipeViewModel: ViewModel() {
    // API Interfaces
    private val spoonacularApi = SpoonacularApi.create()

    // DBHelpers and Repositories
    private val recipesDBHelper = RecipesDBHelper()
    private val recipeInfoDBHelper = RecipeInfoDBHelper()
    private val spoonacularRecipeRepository = SpoonacularRecipeRepository(spoonacularApi)

    // Recipes
    private var recipe: RecipeMeta? = null
    private val recipeInfo = MutableLiveData<RecipeInfoMeta>()
    private var recipeSourceId = ""

    // tabView Fragment Bindings
    private var ingredientFragmentBinding: IngredientsFragmentBinding? = null
    private var directionsFragmentBinding: DirectionsFragmentBinding? = null
    private var notesFragmentBinding: NotesFragmentBinding? = null

    var fetchDone = MutableLiveData<Boolean>(false)

    // Observers
    fun observeRecipeInfo(): LiveData<RecipeInfoMeta> {
        return recipeInfo
    }

    // Getters
    fun getIngredientFragmentBinding(): IngredientsFragmentBinding? {
        return ingredientFragmentBinding
    }

    fun getDirectionsFragmentBinding(): DirectionsFragmentBinding? {
        return directionsFragmentBinding
    }

    fun getNotesFragmentBinding(): NotesFragmentBinding? {
        return notesFragmentBinding
    }

    fun getRecipeSourceId(): String {
        return recipeSourceId
    }

    // Setters
    fun setRecipeSourceId(sourceId: String) {
        recipeSourceId = sourceId
    }

    fun setRecipe(recipeMeta: RecipeMeta) {
        recipe = recipeMeta
    }

    // Public helper functions
    fun fetchReposRecipeInfo(resultListener:() -> Unit) {
        Log.d(TAG, "recipeSourceId: $recipeSourceId ; recipeInfo.sourceId: ${recipeInfo.value?.sourceId}")
        recipeInfoDBHelper.getRecipeInfo(recipeSourceId) {
            if(it.isEmpty()) {
                Log.d(TAG, "No recipe info in DB.")
                viewModelScope.launch(Dispatchers.IO) {
                    val spoonacularRecipeInfo = if(MainActivity.globalDebug) {
                        DummyRepository().fetchRecipeInfoData() // Used for testing
                    } else {
                        spoonacularRecipeRepository.getRecipeInfo(recipeSourceId)
                    }

                    val recipeInfoMeta = convertSpoonacularRecipeToRecipeMeta(spoonacularRecipeInfo)

                    recipeInfoDBHelper.createAndRetrieveDocument(recipeInfoMeta) { createdRecipeInfoMeta ->
                        Log.d(TAG, "DB List is empty.  Pulled info from Spoonacular and saved to DB.  " +
                                "Post Recipe Info")
                        recipeInfo.postValue(createdRecipeInfoMeta.first())
                        fetchDoneTrue()
                    }
                }
            } else {
                Log.d(TAG, "DB List is not empty.  Post Recipe Info")
                recipeInfo.postValue(it.first())
                fetchDoneTrue()
            }
            resultListener.invoke()
        }
    }

    fun createRecipe(title: String,
                     image: String,
                     ingredients: String,
                     directions: String,
                     notes: String,
                     createdBy: String,
                     navigateToOneRecipe: (RecipeMeta)->Unit) {
        // TODO(Add image and ImageType)  // will do this when working on community post
        val createdRecipeMeta = RecipeMeta(
                "",
                title,
                image,
                "image/jpg",
                createdBy
            )

        val createdRecipeInfoMeta = RecipeInfoMeta(
                "",
                ingredients,
                emptyList<Any>(),
                directions,
                notes,
                createdBy
            )

        recipesDBHelper.createAndRetrieveDocumentId(createdRecipeMeta) { id ->
            val updateCreatedRecipe = hashMapOf(
                "sourceId" to id
            )
            recipesDBHelper.updateDocument(id, updateCreatedRecipe as Map<String, kotlin.Any>) {
                createdRecipeInfoMeta.sourceId = id
                recipeInfoDBHelper.createAndRetrieveDocument(createdRecipeInfoMeta) { createdRecipeInfoMeta ->
                    recipeInfo.postValue(createdRecipeInfoMeta.first())
                    createdRecipeMeta.sourceId = id
                    createdRecipeMeta.firestoreId = id
                    navigateToOneRecipe(createdRecipeMeta)
                }
            }
        }
    }

    fun initIngredientFragmentBinding(binding: IngredientsFragmentBinding) {
        ingredientFragmentBinding = binding
    }

    fun initDirectionsFragmentBinding(binding: DirectionsFragmentBinding) {
        directionsFragmentBinding = binding
    }

    fun initNotesFragmentBinding(binding: NotesFragmentBinding) {
        notesFragmentBinding = binding
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
        val createdBy = MainActivity.SPOONACULAR_API_NAME

        return RecipeInfoMeta(
            spoonacularRecipeInfo.id.toString(),
            ingredients,
            spoonacularRecipeInfo.ingredients,
            instruction,
            notes,
            createdBy
        )
    }

    private fun fetchDoneTrue() {
        Log.d(TAG, "Recipe info received.  Turn off progress bar.")
        fetchDone.postValue(true)
    }
}