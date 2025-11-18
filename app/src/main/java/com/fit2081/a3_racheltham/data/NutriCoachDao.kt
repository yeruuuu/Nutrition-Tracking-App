package com.fit2081.a3_racheltham.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NutriCoachDao {
    @Insert
    suspend fun insertTip(tip: NutriCoachTip)

    @Query("SELECT * FROM nutricoach_tips WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTipsForUser(userId: String): Flow<List<NutriCoachTip>>
}