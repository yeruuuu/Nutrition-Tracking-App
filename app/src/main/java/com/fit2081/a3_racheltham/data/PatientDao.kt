package com.fit2081.a3_racheltham.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    @Query("SELECT * FROM patients WHERE userId = :id")
    fun getPatient(id: String): Flow<Patient?>

    @Query("SELECT * FROM patients")
    fun getAllPatients(): Flow<List<Patient>>

    @Query("SELECT * FROM patients")
    suspend fun getAllOnce(): List<Patient>

    @Query("SELECT * FROM patients WHERE userId = :userId AND phoneNumber = :phone")
    suspend fun findPatientByIdAndPhone(userId: String, phone: String): Patient?

    @Query("SELECT * FROM patients WHERE userId = :userId AND password = :password")
    suspend fun authenticate(userId: String, password: String): Patient?

    @Query("UPDATE patients SET name = :name, password = :password WHERE userId = :userId")
    suspend fun updateCredentials(userId: String, name: String, password: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(patients: List<Patient>)


    @Query("SELECT CASE WHEN sex = 'Male' THEN HEIFAtotalscoreMale ELSE HEIFAtotalscoreFemale END FROM patients WHERE userId = :userId")
    fun getTotalScore(userId: String): Flow<Float>

    @Query("SELECT CASE WHEN sex = 'Male' THEN VegetablesHEIFAscoreMale ELSE VegetablesHEIFAscoreFemale END FROM patients WHERE userId = :userId")
    fun getVegetablesScore(userId: String): Flow<Float>

    @Query("SELECT CASE WHEN sex = 'Male' THEN FruitsHEIFAscoreMale ELSE FruitsHEIFAscoreFemale END FROM patients WHERE userId = :userId")
    fun getFruitsScore(userId: String): Flow<Float>

    @Query("SELECT CASE WHEN sex = 'Male' THEN GrainsandcerealsHEIFAscoreMale ELSE GrainsandcerealsHEIFAscoreFemale END FROM patients WHERE userId = :userId")
    fun getCerealsScore(userId: String): Flow<Float>

    @Query("SELECT CASE WHEN sex = 'Male' THEN WholegrainsHEIFAscoreMale ELSE WholegrainsHEIFAscoreFemale END FROM patients WHERE userId = :userId")
    fun getWholeGrainsScore(userId: String): Flow<Float>

    @Query("SELECT CASE WHEN sex = 'Male' THEN MeatandalternativesHEIFAscoreMale ELSE MeatandalternativesHEIFAscoreFemale END FROM patients WHERE userId = :userId")
    fun getMeatScore(userId: String): Flow<Float>

    @Query("SELECT CASE WHEN sex = 'Male' THEN DairyandalternativesHEIFAscoreMale ELSE DairyandalternativesHEIFAscoreFemale END FROM patients WHERE userId = :userId")
    fun getDairyScore(userId: String): Flow<Float>

    @Query("SELECT CASE WHEN sex = 'Male' THEN WaterHEIFAscoreMale ELSE WaterHEIFAscoreFemale END FROM patients WHERE userId = :userId")
    fun getWaterScore(userId: String): Flow<Float>

    @Query("SELECT CASE WHEN sex = 'Male' THEN SaturatedFatHEIFAscoreMale ELSE SaturatedFatHEIFAscoreFemale END FROM patients WHERE userId = :userId")
    fun getSaturatedFatsScore(userId: String): Flow<Float>

    @Query("SELECT CASE WHEN sex = 'Male' THEN UnsaturatedFatHEIFAscoreMale ELSE UnsaturatedFatHEIFAscoreFemale END FROM patients WHERE userId = :userId")
    fun getUnsaturatedFatsScore(userId: String): Flow<Float>

    @Query("SELECT CASE WHEN sex = 'Male' THEN SodiumHEIFAscoreMale ELSE SodiumHEIFAscoreFemale END FROM patients WHERE userId = :userId")
    fun getSodiumScore(userId: String): Flow<Float>

    @Query("SELECT CASE WHEN sex = 'Male' THEN SugarHEIFAscoreMale ELSE SugarHEIFAscoreFemale END FROM patients WHERE userId = :userId")
    fun getSugarScore(userId: String): Flow<Float>

    @Query("SELECT CASE WHEN sex = 'Male' THEN AlcoholHEIFAscoreMale ELSE AlcoholHEIFAscoreFemale END FROM patients WHERE userId = :userId")
    fun getAlcoholScore(userId: String): Flow<Float>

    @Query("SELECT CASE WHEN sex = 'Male' THEN DiscretionaryHEIFAscoreMale ELSE DiscretionaryHEIFAscoreFemale END FROM patients WHERE userId = :userId")
    fun getDiscretionaryFoodsScore(userId: String): Flow<Float>

    @Query("SELECT AVG(HEIFAtotalscoreMale) FROM patients WHERE sex = 'Male'")
    fun getAverageScoreMale(): Flow<Float>

    @Query("SELECT AVG(HEIFAtotalscoreFemale) FROM patients WHERE sex = 'Female'")
    fun getAverageScoreFemale(): Flow<Float>

}