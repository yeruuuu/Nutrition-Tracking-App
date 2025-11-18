package com.fit2081.a3_racheltham.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodIntakeDao {
    @Query("SELECT * FROM food_intake WHERE userId = :userId")
    fun getIntake(userId: String): Flow<FoodIntake?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(intake: FoodIntake)
}