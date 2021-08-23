package avro.kotlin

import kotlinx.serialization.Serializable

@Serializable
data class Pizza(
    val name: String,
    val ingredients: List<Ingredient>,
    val vegetarian: Boolean,
    val kcals: Int
)

@Serializable
data class Ingredient(
    val name: String,
    val sugar: Double,
    val fat: Double
)
