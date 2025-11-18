package com.fit2081.a3_racheltham.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nutricoach_tips")
data class NutriCoachTip(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val tipText: String,
    val timestamp: Long = System.currentTimeMillis()
)