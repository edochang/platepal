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
    }

    fun fetchData(): List<SpoonacularRecipe> {
        return initialSpoonacularRecipeList
    }

    fun secondFetchData(): List<SpoonacularRecipe> {
        return secondSpoonacularRecipeList
    }

    private fun size() : Int { return initialSpoonacularRecipeList.size }

    fun fetchRandomRecipe(): SpoonacularRecipe{
        val rand = (0..<size()).random()
        return initialSpoonacularRecipeList[rand]
    }

}

