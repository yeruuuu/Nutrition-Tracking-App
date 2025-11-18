package com.fit2081.a3_racheltham.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(
    tableName = "food_intake",
    foreignKeys = [ForeignKey(
        entity = Patient::class,
        parentColumns = ["userId"],
        childColumns  = ["userId"],
        onDelete = CASCADE
    )]
)
data class FoodIntake(
    @PrimaryKey val userId: String,
    val selectedCategories: String,
    val persona: String,
    val biggestMealTime: String,
    val sleepTime: String,
    val wakeUpTime: String
)