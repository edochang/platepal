package com.example.platepal

import com.example.platepal.api.ApiUtils
import com.example.platepal.api.SpoonacularApi
import com.example.platepal.data.SpoonacularRecipe
import com.example.platepal.repository.SpoonacularRecipeRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.HttpURLConnection

class SpoonacularRecipeRepositoryUnitTest {
    private lateinit var repository: SpoonacularRecipeRepository
    private lateinit var spoonacularApis: SpoonacularApi
    private lateinit var mockWebServer: MockWebServer

    object RetrofitHelper {

        fun testApiInstance(baseUrl: String): SpoonacularApi {
            val moshi = ApiUtils.getMoshiConverterFactory()
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(SpoonacularApi::class.java)
        }
    }

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        spoonacularApis = RetrofitHelper.testApiInstance(mockWebServer.url("/").toString())
        repository = SpoonacularRecipeRepository(spoonacularApis)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testSpoonacularAPI() = runTest {
        val spoonacularRecipes = listOf(
            SpoonacularRecipe(
                715449,
                "How to Make OREO Turkeys for Thanksgiving",
                "https://img.spoonacular.com/recipes/715449-312x231.jpg",
                "jpg"),
            SpoonacularRecipe(
                715424,
                "The Best Chili",
                "https://img.spoonacular.com/recipes/715424-312x231.jpg",
                "jpg"),
            SpoonacularRecipe(
                715560,
                "World’s Greatest Lasagna Roll Ups",
                "https://img.spoonacular.com/recipes/715560-312x231.jpg",
                "jpg"),
            SpoonacularRecipe(
                776505,
                "Sausage & Pepperoni Stromboli",
                "https://img.spoonacular.com/recipes/776505-312x231.jpg",
                "jpg"),
            SpoonacularRecipe(
                716410,
                "Cannoli Ice Cream w. Pistachios & Dark Chocolate",
                "https://img.spoonacular.com/recipes/716410-312x231.jpg",
                "jpg"),
            SpoonacularRecipe(
                715467,
                "Turkey Pot Pie",
                "https://img.spoonacular.com/recipes/715467-312x231.jpg",
                "jpg"),
            SpoonacularRecipe(
                715419,
                "Slow Cooker Spicy Hot Wings",
                "https://img.spoonacular.com/recipes/715419-312x231.jpg",
                "jpg"),
            SpoonacularRecipe(
                775585,
                "Crockpot \"Refried\" Beans",
                "https://img.spoonacular.com/recipes/775585-312x231.jpg",
                "jpg"),
            SpoonacularRecipe(
                716423,
                "Grilled Zucchini with Goat Cheese and Balsamic-Honey Syrup",
                "https://img.spoonacular.com/recipes/716423-312x231.jpg",
                "jpg"),
            SpoonacularRecipe(
                715421,
                "Cheesy Chicken Enchilada Quinoa Casserole",
                "https://img.spoonacular.com/recipes/715421-312x231.jpg",
                "jpg")
        )

        val recipeResponse = SpoonacularApi.RecipeResponse(spoonacularRecipes)

        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        //val listType = Types.newParameterizedType(List::class.java, SpoonacularRecipe::class.java)
        //val jsonAdapter: JsonAdapter<List<SpoonacularRecipe>> = moshi.adapter(listType)

        val jsonAdapter = moshi.adapter(SpoonacularApi.RecipeResponse::class.java)
        val recipeResponseJson = jsonAdapter.toJson(recipeResponse)

        val expectedResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(recipeResponseJson)
        mockWebServer.enqueue(expectedResponse)

        //println(recipeResponseJson)

        val actualResponse = repository.getRecipes()
        assertEquals(spoonacularRecipes, actualResponse)
    }
}