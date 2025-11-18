package com.fit2081.a3_racheltham.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey val userId: String,
    val phoneNumber: String,
    val password: String? = null,
    val name: String? = null,
    val sex: String,

    val HEIFAtotalscoreMale: Float,
    val HEIFAtotalscoreFemale: Float,
    val VegetablesHEIFAscoreMale: Float,
    val VegetablesHEIFAscoreFemale: Float,
    val FruitsHEIFAscoreMale: Float,
    val FruitsHEIFAscoreFemale: Float,
    val GrainsandcerealsHEIFAscoreMale: Float,
    val GrainsandcerealsHEIFAscoreFemale: Float,
    val WholegrainsHEIFAscoreMale: Float,
    val WholegrainsHEIFAscoreFemale: Float,
    val MeatandalternativesHEIFAscoreMale: Float,
    val MeatandalternativesHEIFAscoreFemale: Float,
    val DairyandalternativesHEIFAscoreMale: Float,
    val DairyandalternativesHEIFAscoreFemale: Float,
    val WaterHEIFAscoreMale: Float,
    val WaterHEIFAscoreFemale: Float,
    val SaturatedFatHEIFAscoreMale: Float,
    val SaturatedFatHEIFAscoreFemale: Float,
    val UnsaturatedFatHEIFAscoreMale: Float,
    val UnsaturatedFatHEIFAscoreFemale: Float,
    val SodiumHEIFAscoreMale: Float,
    val SodiumHEIFAscoreFemale: Float,
    val SugarHEIFAscoreMale: Float,
    val SugarHEIFAscoreFemale: Float,
    val AlcoholHEIFAscoreMale: Float,
    val AlcoholHEIFAscoreFemale: Float,
    val DiscretionaryHEIFAscoreMale: Float,
    val DiscretionaryHEIFAscoreFemale: Float

)