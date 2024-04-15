package com.example.platepal.data

import com.example.platepal.data.SpoonacularRecipe

class DummyRepository {
    companion object {
        private var initialSpoonacularRecipeList = listOf (
            SpoonacularRecipe(
                id = 782585,
                title = "Cannellini Bean and Asparagus Salad with Mushrooms",
                image = "https://img.spoonacular.com/recipes/782585-312x231.jpg",
                imageType = "jpg"
            ),
            SpoonacularRecipe(
                id = 715497,
                title = "Berry Banana Breakfast Smoothie",
                image = "https://img.spoonacular.com/recipes/715497-312x231.jpg",
                imageType = "jpg"
            ),
            SpoonacularRecipe(
                id = 715415,
                title = "Red Lentil Soup with Chicken and Turnips",
                image = "https://img.spoonacular.com/recipes/715415-312x231.jpg",
                imageType = "jpg"
            ),
            SpoonacularRecipe(
                id = 716406,
                title = "Asparagus and Pea Soup: Real Convenience Food",
                image = "https://img.spoonacular.com/recipes/716406-312x231.jpg",
                imageType = "jpg"
            ),
            SpoonacularRecipe(
                id = 644387,
                title = "Garlicky Kale",
                image = "https://img.spoonacular.com/recipes/644387-312x231.jpg",
                imageType = "jpg"
            ),
            SpoonacularRecipe(
                id = 715446,
                title = "Slow Cooker Beef Stew",
                image = "https://img.spoonacular.com/recipes/715446-312x231.jpg",
                imageType = "jpg"
            ),
            SpoonacularRecipe(
                id = 782601,
                title = "Red Kidney Bean Jambalaya",
                image = "https://img.spoonacular.com/recipes/782601-312x231.jpg",
                imageType = "jpg"
            ),
            SpoonacularRecipe(
                id = 795751,
                title = "Chicken Fajita Stuffed Bell Pepper",
                image = "https://img.spoonacular.com/recipes/795751-312x231.jpg",
                imageType = "jpg"
            ),
            SpoonacularRecipe(
                id = 716426,
                title = "Cauliflower, Brown Rice, and Vegetable Fried Rice",
                image = "https://img.spoonacular.com/recipes/716426-312x231.jpg",
                imageType = "jpg"
            ),
            SpoonacularRecipe(
                id = 766453,
                title = "Hummus and Za'atar",
                image = "https://img.spoonacular.com/recipes/766453-312x231.jpg",
                imageType = "jpg"
            )
        )

        private var secondSpoonacularRecipeList = listOf (
            SpoonacularRecipe(
                id = 716426,
                title = "Cauliflower, Brown Rice, and Vegetable Fried Rice",
                image = "https://img.spoonacular.com/recipes/716426-312x231.jpg",
                imageType = "jpg"
            ),
            SpoonacularRecipe(
                id = 766453,
                title = "Hummus and Za'atar",
                image = "https://img.spoonacular.com/recipes/766453-312x231.jpg",
                imageType = "jpg"
            ),
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
        )

        private var initialSpoonacularRecipeInfo = SpoonacularRecipeInfo(
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
    }

    // Public Functions
    fun fetchData(): List<SpoonacularRecipe> {
        return initialSpoonacularRecipeList
    }

    fun secondFetchData(): List<SpoonacularRecipe> {
        return secondSpoonacularRecipeList
    }

    fun fetchRecipeInfoData(): SpoonacularRecipeInfo {
        return initialSpoonacularRecipeInfo
    }

    fun fetchRandomRecipe(): SpoonacularRecipe{
        val rand = (0..<size()).random()
        return initialSpoonacularRecipeList[rand]
    }

    // Private Functions
    private fun size() : Int { return initialSpoonacularRecipeList.size }
}

