package com.example.platepal.ui.viewmodel

import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.platepal.MainActivity
import com.example.platepal.api.SpoonacularApi
import com.example.platepal.data.DummyRepository
import com.example.platepal.data.RecipeMeta
import com.example.platepal.data.SpoonacularRecipe
import com.example.platepal.data.StorageDirectory
import com.example.platepal.repository.AppDBHelper
import com.example.platepal.repository.RecipesDBHelper
import com.example.platepal.repository.SpoonacularRecipeRepository
import com.example.platepal.repository.Storage
import com.google.firebase.Timestamp
import com.example.platepal.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Duration

class MainViewModel : ViewModel() {
    companion object {
        const val TAG = "MainViewModel"
    }

    // DBHelpers
    private val recipesDBHelper = RecipesDBHelper()
    private val appDBHelper = AppDBHelper()

    // API Interfaces
    private val spoonacularApi = SpoonacularApi.create()
    private var lastSpoonacularSearchApiCall: Timestamp? = null
    private var appMetaDocumentId: String = "".apply {
        appDBHelper.getAppMeta {
            appMetaDocumentId = it.firestoreId
            lastSpoonacularSearchApiCall = Timestamp(
                it.lastSpoonacularSearchApiCallTimestampSec,
                it.lastSpoonacularSearchApiCallTimestampNanosec
            )
        }
    }

    // Repositories
    private val spoonacularRecipeRepository = SpoonacularRecipeRepository(spoonacularApi)
    private val storage = Storage()

    private var initFavList = MutableLiveData<Boolean>().apply {
        this.value = false
    }

    // Maintain a list of all Spoonacular Recipe items
    private var recipeList = MediatorLiveData<List<RecipeMeta>>().apply {
        addSource(initFavList) {
            if (it) {
                fetchReposRecipeList {
                    Log.d(TAG, "Initial Recipe List fetched after favlist")
                }
            }
        }
    }

    //Maintain a list of user created recipe items
    private var userCreatedRecipeList = MutableLiveData<List<RecipeMeta>>()

    //Maintain a list of Spoonacular + User-created recipe items
    private var allRecipeList = MutableLiveData<List<RecipeMeta>>()

    //title of the fragment
    private var title = MutableLiveData<String>()  // fragment title
    private var randomSpotlightRecipe = MutableLiveData<RecipeMeta>()

    // Photo Metadata
    var pictureNameByUser = "" // String provided by the user
    private var pictureUUID = ""

    fun observeRecipeList(): LiveData<List<RecipeMeta>> {
        return recipeList
    }

    fun observeUserCreatedRecipeList(): LiveData<List<RecipeMeta>> {
        return userCreatedRecipeList
    }

    fun observeAllRecipeList(): LiveData<List<RecipeMeta>> {
        return allRecipeList
    }

    fun getRecipeList(): List<RecipeMeta> {
        return recipeList.value ?: emptyList()
    }

    // Setter
    fun setRandomRecipe() {
        Log.d(TAG, "Setting random recipe...")
        val recipes = recipeList.value
        Log.d(TAG, "From $recipes")
        val recipesSize = recipes?.size ?: 0
        recipes?.let {
            if (recipesSize > 0) {
                val rand = (0..<recipesSize).random()
                randomSpotlightRecipe.value = recipes[rand]
            }
        }
    }

    fun setInitFavList(flag: Boolean) {
        initFavList.value = flag
    }

    fun setTitle(newTitle: String) {
        title.value = newTitle
    }

    // Observers
    fun observeRandomSpotlightRecipe(): LiveData<RecipeMeta> {
        return randomSpotlightRecipe
    }

    fun observeTitle(): LiveData<String> {
        return title
    }

    // Public functions
    fun fetchRecipePhoto(image: String, createdBy: String, imageView: ImageView) {
        if (createdBy == MainActivity.SPOONACULAR_API_NAME) {
            Glide.glideFetch(image, image, imageView)
        } else {
            Glide.fetchFromStorage(
                storage.uuid2StorageReference(image, StorageDirectory.RECIPE),
                imageView
            )
        }
    }

    fun fetchReposUserCreatedRecipeList(resultListener: () -> Unit){
        recipesDBHelper.getUserCreatedRecipes {
            if (it.isNotEmpty()) {
                userCreatedRecipeList.postValue(it)
                resultListener.invoke()
            }
        }
    }

    fun fetchAllRecipeList(resultListener: () -> Unit){
        recipesDBHelper.getAllRecipes {
            if (it.isNotEmpty()) {
                allRecipeList.postValue(it)
                resultListener.invoke()
            }
        }
    }

    fun fetchReposRecipeList(resultListener: () -> Unit) {
        recipesDBHelper.getRecipes {
            if (it.isEmpty()) {
                viewModelScope.launch(Dispatchers.IO) {
                    val spoonacularRecipes = if (MainActivity.globalDebug) {
                        DummyRepository().fetchData() // Used for testing
                    } else {
                        Log.d(TAG, "Get recipes from Spoonacular")
                        spoonacularRecipeRepository.getRecipes()
                    }

                    if (!MainActivity.globalDebug) {
                        val nowTimestamp = Timestamp.now()
                        val nowTimestampSec = nowTimestamp.seconds
                        val nowTimestampNanosec = nowTimestamp.nanoseconds

                        val updateAppMeta = hashMapOf(
                            "lastSpoonacularSearchApiCallTimestampSec" to nowTimestampSec,
                            "lastSpoonacularSearchApiCallTimestampNanosec" to nowTimestampNanosec
                        )
                        appDBHelper.updateDocument(
                            appMetaDocumentId,
                            updateAppMeta as Map<String, kotlin.Any>
                        ) {
                            Log.d(
                                TAG, "Set last call time for Spoonacular Search API " +
                                        "(sec: $nowTimestampSec, nanosec: $nowTimestampNanosec)"
                            )
                        }
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
                val minRecipeStaleTimestamp = nowTimestampSeconds - dayDuration
                Log.d(
                    TAG,
                    "recentDuration: ${mostRecentRecipe.timeStamp?.seconds}, minStaleDuration: $minRecipeStaleTimestamp"
                )

                lastSpoonacularSearchApiCall?.let { lastCallTS ->
                    val minCallStaleTimestamp = lastCallTS.seconds + dayDuration
                    if (minRecipeStaleTimestamp > mostRecentRecipe.timeStamp!!.seconds && Timestamp.now().seconds > minCallStaleTimestamp) {
                        viewModelScope.launch(Dispatchers.IO) {
                            val spoonacularRecipes = if (MainActivity.globalDebug) {
                                DummyRepository().secondFetchData() // Used for testing
                            } else {
                                //Log.d(TAG, "Recipes are more than a day created.  Check Spoonacular for changes...")
                                Log.d(
                                    TAG,
                                    "Recipes could be stale.  Check Spoonacular for changes..."
                                )
                                spoonacularRecipeRepository.getRecipes()
                            }

                            val recipeMetas = List(spoonacularRecipes.size) { index ->
                                convertSpoonacularRecipeToRecipeMeta(spoonacularRecipes[index])
                            }
                            val newRecipes = (recipeMetas.toSet() - it.toSet()).toList()
                            if (newRecipes.isNotEmpty()) {
                                Log.d(TAG, "New recipes received from Spoonacular: $newRecipes")
                                recipesDBHelper.batchCreateDocuments(newRecipes) {
                                    fetchReposRecipeList(resultListener)
                                }
                            }

                            val nowTimestamp = Timestamp.now()
                            val nowTimestampSec = nowTimestamp.seconds
                            val nowTimestampNanosec = nowTimestamp.nanoseconds

                            val updateAppMeta = hashMapOf(
                                "lastSpoonacularSearchApiCallTimestampSec" to nowTimestampSec,
                                "lastSpoonacularSearchApiCallTimestampNanosec" to nowTimestampNanosec
                            )
                            appDBHelper.updateDocument(
                                appMetaDocumentId,
                                updateAppMeta as Map<String, kotlin.Any>
                            ) {
                                Log.d(
                                    TAG, "Set last call time for Spoonacular Search API " +
                                            "(sec: $nowTimestampSec, nanosec: $nowTimestampNanosec)"
                                )
                            }
                        }
                    }
                }
            }

            recipeList.postValue(it)
            resultListener.invoke()
        }
    }

    // Private helper functions
    private fun convertSpoonacularRecipeToRecipeMeta(spoonacularRecipe: SpoonacularRecipe): RecipeMeta {
        val createdBy = MainActivity.SPOONACULAR_API_NAME

        return RecipeMeta(
            spoonacularRecipe.id.toString(),
            spoonacularRecipe.title,
            spoonacularRecipe.image,
            spoonacularRecipe.imageType,
            createdBy
        )
    }
}