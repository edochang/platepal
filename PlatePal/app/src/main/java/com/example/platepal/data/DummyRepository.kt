package com.example.platepal.data



class DummyRepository {
    companion object {
        private var initialRecipeList = listOf (
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
    }

    fun fetchData(): List<SpoonacularRecipe> {
        return initialRecipeList
    }

    private fun size() : Int { return initialRecipeList.size }

    fun fetchRandomRecipe(): SpoonacularRecipe{
        val rand = (0..<size()).random()
        return initialRecipeList[rand]
    }

}

