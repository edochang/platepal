package com.example.platepal

import com.example.platepal.api.ApiUtils
import com.example.platepal.api.SpoonacularApi
import com.example.platepal.data.SpoonacularAnalyzedInstruction
import com.example.platepal.data.SpoonacularIngredient
import com.example.platepal.data.SpoonacularRecipe
import com.example.platepal.data.SpoonacularRecipeInfo
import com.example.platepal.data.SpoonacularSteps
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
    fun testSpoonacularRecipeAPI() = runTest {
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

    @Test
    fun testSpoonacularRecipeInfoAPI() = runTest {
        val spoonacularRecipeInfo = SpoonacularRecipeInfo(
            715562,
            8,
            60,
            "Pink When",
            "http://www.pinkwhen.com/loaded-baked-potato-soup/",
            "Loaded Baked Potato Soup might be just the main course you are searching for. This recipe serves 8. One portion of this dish contains about <b>21g of protein</b>, <b>35g of fat</b>, and a total of <b>624 calories</b>. For <b>\$1.29 per serving</b>, this recipe <b>covers 23%</b> of your daily requirements of vitamins and minerals. 5743 people found this recipe to be flavorful and satisfying. <b>Autumn</b> will be even more special with this recipe. If you have bacon bits, cream, onion, and a few other ingredients on hand, you can make it. From preparation to the plate, this recipe takes roughly <b>1 hour</b>. It is brought to you by Pink When. All things considered, we decided this recipe <b>deserves a spoonacular score of 82%</b>. This score is awesome. <a href=\\\"https://spoonacular.com/recipes/loaded-baked-potato-soup-with-crispy-fried-potato-skins-1218881\\\">Loaded Baked Potato Soup with Crispy-Fried Potato Skins</a>, <a href=\\\"https://spoonacular.com/recipes/loaded-baked-potato-soup-with-crispy-fried-potato-skins-1224705\\\">Loaded Baked Potato Soup with Crispy-Fried Potato Skins</a>, and <a href=\\\"https://spoonacular.com/recipes/loaded-baked-potato-soup-with-crispy-fried-potato-skins-1632271\\\">Loaded Baked Potato Soup with Crispy-Fried Potato Skins</a> are very similar to this recipe.",
            listOf(
                SpoonacularIngredient(
                    43212,
                    "bacon bits"
                ),
                SpoonacularIngredient(
                    11353,
                    "8 medium baking potatoes (peeled and cubed)"
                ),
            ),
            "<ol><li>Place a large pot of water on the stove and add in the peeled and cubed potatoes. Get the water up to a boil, and then boil for 20 minutes, or until potatoes are cooked. Remove from heat and drain water. Place to the side.</li><li>In a medium pot over medium-high heat melt butter and sautee onion for 6 minutes. Add in the flour and using a whisk, mix well for 30 seconds to one minute.</li><li>Gradually start whisking in the milk, about ½ cup to a cup at a time, constantly stirring. You want to keep whisking and adding milk and stirring. Bring to a boil, and then turn heat down to medium and allow the mixture to simmer for 8-10 minutes until thickened.</li><li>Stir in the potatoes and cook for an additional 5 minutes. Add in the cheese, salt and pepper, sour cream and stir well. Remove from heat and allow to cool slightly before serving.</li><li>Top with additional sour cream, cheese, and bacon bits if desired.</li></ol>",
            listOf(
                SpoonacularSteps(
                    listOf(
                        SpoonacularAnalyzedInstruction(
                            1,
                            "Place a large pot of water on the stove and add in the peeled and cubed potatoes. Get the water up to a boil, and then boil for 20 minutes, or until potatoes are cooked.",
                        ),
                        SpoonacularAnalyzedInstruction(
                            2,
                            "Remove from heat and drain water."
                        )
                    )
                )
            )
        )

        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        val jsonAdapter = moshi.adapter(SpoonacularRecipeInfo::class.java)
        val recipeInfoResponseJson = jsonAdapter.toJson(spoonacularRecipeInfo)

        val expectedResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(recipeInfoResponseJson)
        mockWebServer.enqueue(expectedResponse)

        println(recipeInfoResponseJson)

        val actualResponse = repository.getRecipeInfo("715562")
        assertEquals(spoonacularRecipeInfo, actualResponse)
    }
}