package lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.local

import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.Ingredient
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.MenuItem

object SeedData {
    
    fun getSampleMenuItems(): List<MenuItem> {
        return listOf(
            // Starters
            MenuItem(
                id = "starter_1",
                name = "Golden Oak Soup",
                category = "Starters",
                price = 450.0,
                discountedPrice = 380.0,
                calories = 180,
                isVegetarian = true,
                isNew = true,
                imageUrl = "https://images.unsplash.com/photo-1547592166-23ac45744acd?w=400",
                ingredients = listOf(
                    Ingredient("Pumpkin", listOf("Rich in Vitamin A", "Boosts immunity")),
                    Ingredient("Cream", listOf("Good source of calcium")),
                    Ingredient("Herbs", listOf("Antioxidant properties"))
                ),
                description = "Creamy pumpkin soup with a hint of nutmeg and fresh herbs, served with garlic bread."
            ),
            MenuItem(
                id = "starter_2",
                name = "Crispy Calamari",
                category = "Starters",
                price = 550.0,
                calories = 320,
                isVegetarian = false,
                isNew = false,
                imageUrl = "https://images.unsplash.com/photo-1599487488170-d11ec9c172f0?w=400",
                ingredients = listOf(
                    Ingredient("Calamari", listOf("High protein", "Low fat")),
                    Ingredient("Lemon", listOf("Vitamin C", "Aids digestion")),
                    Ingredient("Garlic", listOf("Heart health"))
                ),
                description = "Golden fried calamari rings served with zesty lemon aioli and marinara sauce."
            ),
            MenuItem(
                id = "starter_3",
                name = "Bruschetta Trio",
                category = "Starters",
                price = 420.0,
                calories = 220,
                isVegetarian = true,
                isNew = true,
                imageUrl = "https://images.unsplash.com/photo-1572695157366-5e585ab2b69f?w=400",
                ingredients = listOf(
                    Ingredient("Tomato", listOf("Lycopene antioxidant", "Heart health")),
                    Ingredient("Basil", listOf("Anti-inflammatory")),
                    Ingredient("Olive Oil", listOf("Healthy fats"))
                ),
                description = "Three varieties of bruschetta: classic tomato, mushroom, and olive tapenade."
            ),
            
            // Main Course
            MenuItem(
                id = "main_1",
                name = "Grilled Salmon",
                category = "Main Course",
                price = 1200.0,
                calories = 450,
                isVegetarian = false,
                isNew = false,
                imageUrl = "https://images.unsplash.com/photo-1467003909585-2f8a72700288?w=400",
                ingredients = listOf(
                    Ingredient("Salmon", listOf("Omega-3 fatty acids", "High protein")),
                    Ingredient("Asparagus", listOf("Fiber rich", "Vitamin K")),
                    Ingredient("Lemon Butter", listOf("Flavor enhancer"))
                ),
                description = "Fresh Atlantic salmon grilled to perfection, served with asparagus and lemon butter sauce."
            ),
            MenuItem(
                id = "main_2",
                name = "Ribeye Steak",
                category = "Main Course",
                price = 1800.0,
                calories = 650,
                isVegetarian = false,
                isNew = false,
                imageUrl = "https://images.unsplash.com/photo-1600891964092-4316c288032e?w=400",
                ingredients = listOf(
                    Ingredient("Beef", listOf("High protein", "Iron rich")),
                    Ingredient("Rosemary", listOf("Digestive aid")),
                    Ingredient("Garlic", listOf("Immune booster"))
                ),
                description = "Premium 12oz ribeye steak cooked to your preference, served with roasted vegetables."
            ),
            MenuItem(
                id = "main_3",
                name = "Vegetable Risotto",
                category = "Main Course",
                price = 850.0,
                calories = 380,
                isVegetarian = true,
                isNew = true,
                imageUrl = "https://images.unsplash.com/photo-1476124369491-e7addf5db371?w=400",
                ingredients = listOf(
                    Ingredient("Arborio Rice", listOf("Carbohydrates", "Energy")),
                    Ingredient("Mixed Vegetables", listOf("Vitamins", "Fiber")),
                    Ingredient("Parmesan", listOf("Calcium", "Protein"))
                ),
                description = "Creamy Italian risotto with seasonal vegetables and aged parmesan cheese."
            ),
            
            // Burgers
            MenuItem(
                id = "burger_1",
                name = "Golden Oak Burger",
                category = "Burgers",
                price = 750.0,
                discountedPrice = 650.0,
                calories = 520,
                isVegetarian = false,
                isNew = false,
                imageUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400",
                ingredients = listOf(
                    Ingredient("Beef Patty", listOf("Protein", "Iron")),
                    Ingredient("Cheddar Cheese", listOf("Calcium")),
                    Ingredient("Caramelized Onions", listOf("Flavor", "Antioxidants"))
                ),
                description = "Signature beef patty with aged cheddar, caramelized onions, and secret sauce."
            ),
            MenuItem(
                id = "burger_2",
                name = "Mushroom Swiss Burger",
                category = "Burgers",
                price = 800.0,
                calories = 480,
                isVegetarian = false,
                isNew = true,
                imageUrl = "https://images.unsplash.com/photo-1586190848861-99aa4a171e90?w=400",
                ingredients = listOf(
                    Ingredient("Beef Patty", listOf("Protein", "Iron")),
                    Ingredient("Swiss Cheese", listOf("Calcium")),
                    Ingredient("Sautéed Mushrooms", listOf("Vitamin D", "Immune support"))
                ),
                description = "Juicy beef patty topped with melted Swiss cheese and garlic sautéed mushrooms."
            ),
            MenuItem(
                id = "burger_3",
                name = "Veggie Deluxe",
                category = "Burgers",
                price = 680.0,
                calories = 380,
                isVegetarian = true,
                isNew = false,
                imageUrl = "https://images.unsplash.com/photo-1520072959219-c595dc870360?w=400",
                ingredients = listOf(
                    Ingredient("Bean Patty", listOf("Plant protein", "Fiber")),
                    Ingredient("Avocado", listOf("Healthy fats", "Potassium")),
                    Ingredient("Lettuce", listOf("Vitamins", "Hydration"))
                ),
                description = "House-made vegetable patty with fresh avocado, lettuce, and herb mayo."
            ),
            
            // Salads
            MenuItem(
                id = "salad_1",
                name = "Caesar Salad",
                category = "Salads",
                price = 450.0,
                calories = 280,
                isVegetarian = true,
                isNew = false,
                imageUrl = "https://images.unsplash.com/photo-1550304943-4f24f54ddde9?w=400",
                ingredients = listOf(
                    Ingredient("Romaine", listOf("Vitamin K", "Fiber")),
                    Ingredient("Parmesan", listOf("Calcium", "Protein")),
                    Ingredient("Croutons", listOf("Carbohydrates"))
                ),
                description = "Classic Caesar salad with crisp romaine, parmesan, and house-made dressing."
            ),
            MenuItem(
                id = "salad_2",
                name = "Greek Salad",
                category = "Salads",
                price = 480.0,
                calories = 250,
                isVegetarian = true,
                isNew = true,
                imageUrl = "https://images.unsplash.com/photo-1540420773420-3366772f4999?w=400",
                ingredients = listOf(
                    Ingredient("Feta Cheese", listOf("Calcium", "Protein")),
                    Ingredient("Olives", listOf("Healthy fats", "Antioxidants")),
                    Ingredient("Cucumber", listOf("Hydration", "Vitamin K"))
                ),
                description = "Fresh Mediterranean salad with feta cheese, olives, and olive oil dressing."
            ),
            MenuItem(
                id = "salad_3",
                name = "Quinoa Power Bowl",
                category = "Salads",
                price = 550.0,
                calories = 320,
                isVegetarian = true,
                isNew = false,
                imageUrl = "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=400",
                ingredients = listOf(
                    Ingredient("Quinoa", listOf("Complete protein", "Fiber")),
                    Ingredient("Chickpeas", listOf("Plant protein", "Fiber")),
                    Ingredient("Kale", listOf("Vitamins", "Antioxidants"))
                ),
                description = "Nutritious bowl with quinoa, chickpeas, kale, and tahini dressing."
            ),
            
            // Desserts
            MenuItem(
                id = "dessert_1",
                name = "Chocolate Lava Cake",
                category = "Desserts",
                price = 380.0,
                calories = 420,
                isVegetarian = true,
                isNew = false,
                imageUrl = "https://images.unsplash.com/photo-1624353365286-3f8d62daad51?w=400",
                ingredients = listOf(
                    Ingredient("Dark Chocolate", listOf("Antioxidants", "Mood booster")),
                    Ingredient("Butter", listOf("Rich flavor")),
                    Ingredient("Vanilla Ice Cream", listOf("Creamy texture"))
                ),
                description = "Warm chocolate cake with a molten center, served with vanilla ice cream."
            ),
            MenuItem(
                id = "dessert_2",
                name = "Tiramisu",
                category = "Desserts",
                price = 420.0,
                calories = 380,
                isVegetarian = true,
                isNew = true,
                imageUrl = "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=400",
                ingredients = listOf(
                    Ingredient("Mascarpone", listOf("Creamy texture", "Calcium")),
                    Ingredient("Espresso", listOf("Caffeine", "Flavor")),
                    Ingredient("Cocoa", listOf("Antioxidants"))
                ),
                description = "Classic Italian dessert with layers of coffee-soaked ladyfingers and mascarpone cream."
            ),
            MenuItem(
                id = "dessert_3",
                name = "New York Cheesecake",
                category = "Desserts",
                price = 400.0,
                calories = 450,
                isVegetarian = true,
                isNew = false,
                imageUrl = "https://images.unsplash.com/photo-1533134242443-d4fd215305ad?w=400",
                ingredients = listOf(
                    Ingredient("Cream Cheese", listOf("Calcium", "Protein")),
                    Ingredient("Graham Cracker", listOf("Crunchy base")),
                    Ingredient("Berry Compote", listOf("Antioxidants", "Vitamin C"))
                ),
                description = "Rich and creamy cheesecake with a graham cracker crust and fresh berry compote."
            )
        )
    }
}
