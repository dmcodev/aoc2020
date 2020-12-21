package day21

import util.loadInput

data class Food(val ingredients: Set<String>, val allergens: Set<String>)

fun main() {
    val foodList = loadInput(21)
        .map {
            Food(
                it.substringBefore(" (contains ").split(" ").toSet(),
                it.substringAfter(" (contains ").substringBefore(")").split(", ").toSet()
            )
        }

    val allergenToPossibleIngredients = mutableMapOf<String, MutableSet<String>>()
    foodList.forEach { food ->
        food.allergens.forEach { allergen ->
            allergenToPossibleIngredients.computeIfAbsent(allergen) { food.ingredients.toMutableSet() }
                .retainAll(food.ingredients)
        }
    }

    val allergenToIngredient = mutableMapOf<String, String>()
    while (allergenToPossibleIngredients.isNotEmpty()) {
        val entry = allergenToPossibleIngredients.entries.find { it.value.size == 1 }!!
        val allergen = entry.key
        val ingredient = entry.value.first()
        allergenToIngredient[allergen] = ingredient
        allergenToPossibleIngredients.values.forEach { possibleIngredients -> possibleIngredients.remove(ingredient) }
        allergenToPossibleIngredients.remove(allergen)
    }

    val ingredientToAllergen = allergenToIngredient.entries.associateBy({ it.value }) { it.key }

    val allIngredients = foodList.flatMap { it.ingredients.toList() }
    val allergenIngredients = allergenToIngredient.values.toSet()

    println(allIngredients.count { !allergenIngredients.contains(it) })
    println(allergenIngredients.sortedBy { ingredientToAllergen[it] }.joinToString(","))
}