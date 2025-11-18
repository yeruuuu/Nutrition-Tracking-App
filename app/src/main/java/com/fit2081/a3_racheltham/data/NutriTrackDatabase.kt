package com.fit2081.a3_racheltham.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Patient::class, FoodIntake::class, NutriCoachTip::class],
    version = 7,
    exportSchema = false
)

abstract class NutriTrackDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun foodIntakeDao(): FoodIntakeDao
    abstract fun nutriCoachDao(): NutriCoachDao

    companion object {
        @Volatile private var INSTANCE: NutriTrackDatabase? = null

        fun getInstance(context: Context): NutriTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val inst = Room.databaseBuilder(
                    context.applicationContext,
                    NutriTrackDatabase::class.java,
                    "nutritrack_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = inst
                inst
            }
        }
    }
}


