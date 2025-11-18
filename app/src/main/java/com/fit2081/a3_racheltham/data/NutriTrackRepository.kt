package com.fit2081.a3_racheltham.data

import android.content.Context
import android.util.Log
import com.fit2081.a3_racheltham.api.Content
import com.fit2081.a3_racheltham.api.FruitResponse
import com.fit2081.a3_racheltham.api.GeminiClient
import com.fit2081.a3_racheltham.api.GeminiRequest
import com.fit2081.a3_racheltham.api.Part
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import com.fit2081.a3_racheltham.api.RetrofitClient

class NutriTrackRepository(context: Context) {
    private val context = context
    private val db = NutriTrackDatabase.getInstance(context)
    private val patientDao = db.patientDao()
    private val foodIntakeDao = db.foodIntakeDao()
    private val nutriCoachDao = db.nutriCoachDao()

    // Patients
    fun getPatient(id: String) = patientDao.getPatient(id)
    fun getAllPatients(): Flow<List<Patient>> = db.patientDao().getAllPatients()

    suspend fun updatePatientCredentials(userId: String, name: String, password: String) {
        db.patientDao().updateCredentials(userId, name, password)
    }

    suspend fun findPatientByIdAndPhone(userId: String, phone: String): Patient? {
        return db.patientDao().findPatientByIdAndPhone(userId, phone)
    }

    suspend fun authenticate(userId: String, password: String): Patient? {
        return db.patientDao().authenticate(userId, password)
    }


    // Food Intake
    fun getIntake(userId: String) = foodIntakeDao.getIntake(userId)
    suspend fun saveIntake(record: FoodIntake) = foodIntakeDao.upsert(record)

    // Scores
    fun getTotalScore(userId: String): Flow<Float> = patientDao.getTotalScore(userId)
    fun getVegetablesScore(userId: String): Flow<Float> = patientDao.getVegetablesScore(userId)
    fun getFruitsScore(userId: String): Flow<Float> = patientDao.getFruitsScore(userId)
    fun getCerealsScore(userId: String): Flow<Float> = patientDao.getCerealsScore(userId)
    fun getWholeGrainsScore(userId: String): Flow<Float> = patientDao.getWholeGrainsScore(userId)
    fun getMeatScore(userId: String): Flow<Float> = patientDao.getMeatScore(userId)
    fun getDairyScore(userId: String): Flow<Float> = patientDao.getDairyScore(userId)
    fun getWaterScore(userId: String): Flow<Float> = patientDao.getWaterScore(userId)
    fun getSaturatedFatsScore(userId: String): Flow<Float> = patientDao.getSaturatedFatsScore(userId)
    fun getUnsaturatedFatsScore(userId: String): Flow<Float> = patientDao.getUnsaturatedFatsScore(userId)
    fun getSodiumScore(userId: String): Flow<Float> = patientDao.getSodiumScore(userId)
    fun getSugarScore(userId: String): Flow<Float> = patientDao.getSugarScore(userId)
    fun getAlcoholScore(userId: String): Flow<Float> = patientDao.getAlcoholScore(userId)
    fun getDiscretionaryFoodsScore(userId: String): Flow<Float> = patientDao.getDiscretionaryFoodsScore(userId)

    suspend fun getFruitInfo(name: String): FruitResponse =
        withContext(Dispatchers.IO) {
            RetrofitClient.api.getFruitByName(name)
        }

    // NutriCoach Tips
    fun getTipsForUser(userId: String): Flow<List<NutriCoachTip>> =
        nutriCoachDao.getTipsForUser(userId)
    suspend fun insertTip(tip: NutriCoachTip) = nutriCoachDao.insertTip(tip)


    /**
     * One-time CSV import. Call from Application on first launch.
     */
    suspend fun importCsvIfNeeded() {
        withContext(Dispatchers.IO) {
            val alreadyImported = patientDao.getAllOnce().isNotEmpty()
            if (!alreadyImported) {
                val input = context.assets.open("A1_Data.csv").bufferedReader().readLines()
                val data = input.drop(1).mapNotNull { line ->
                    val cols = line.split(',')
                    if (cols.size >= 15) {
                        Patient(
                            phoneNumber = cols[0],
                            userId = cols[1],
                            name = null,
                            sex = cols[2],
                            HEIFAtotalscoreMale = cols[3].toFloatOrNull() ?: 0f,
                            HEIFAtotalscoreFemale = cols[4].toFloatOrNull() ?: 0f,
                            DiscretionaryHEIFAscoreMale = cols[5].toFloatOrNull() ?: 0f,
                            DiscretionaryHEIFAscoreFemale = cols[6].toFloatOrNull() ?: 0f,
                            VegetablesHEIFAscoreMale = cols[8].toFloatOrNull() ?: 0f,
                            VegetablesHEIFAscoreFemale = cols[9].toFloatOrNull() ?: 0f,
                            FruitsHEIFAscoreMale = cols[19].toFloatOrNull() ?: 0f,
                            FruitsHEIFAscoreFemale = cols[20].toFloatOrNull() ?: 0f,
                            GrainsandcerealsHEIFAscoreMale = cols[29].toFloatOrNull() ?: 0f,
                            GrainsandcerealsHEIFAscoreFemale = cols[30].toFloatOrNull() ?: 0f,
                            WholegrainsHEIFAscoreMale = cols[33].toFloatOrNull() ?: 0f,
                            WholegrainsHEIFAscoreFemale = cols[34].toFloatOrNull() ?: 0f,
                            MeatandalternativesHEIFAscoreMale = cols[36].toFloatOrNull() ?: 0f,
                            MeatandalternativesHEIFAscoreFemale = cols[37].toFloatOrNull() ?: 0f,
                            DairyandalternativesHEIFAscoreMale = cols[40].toFloatOrNull() ?: 0f,
                            DairyandalternativesHEIFAscoreFemale = cols[41].toFloatOrNull() ?: 0f,
                            SodiumHEIFAscoreMale = cols[43].toFloatOrNull() ?: 0f,
                            SodiumHEIFAscoreFemale = cols[44].toFloatOrNull() ?: 0f,
                            AlcoholHEIFAscoreMale = cols[46].toFloatOrNull() ?: 0f,
                            AlcoholHEIFAscoreFemale = cols[47].toFloatOrNull() ?: 0f,
                            WaterHEIFAscoreMale = cols[49].toFloatOrNull() ?: 0f,
                            WaterHEIFAscoreFemale = cols[50].toFloatOrNull() ?: 0f,
                            SugarHEIFAscoreMale = cols[54].toFloatOrNull() ?: 0f,
                            SugarHEIFAscoreFemale = cols[55].toFloatOrNull() ?: 0f,
                            SaturatedFatHEIFAscoreMale = cols[57].toFloatOrNull() ?: 0f,
                            SaturatedFatHEIFAscoreFemale = cols[58].toFloatOrNull() ?: 0f,
                            UnsaturatedFatHEIFAscoreMale = cols[60].toFloatOrNull() ?: 0f,
                            UnsaturatedFatHEIFAscoreFemale = cols[61].toFloatOrNull() ?: 0f
                        )
                    } else null
                }
                patientDao.insertAll(data)
            }
        }
    }

    suspend fun getMotivationalTip(apiKey: String): String {
        Log.d("GeminiKeyCheck", "Using API Key: $apiKey")
        val prompt = "Generate a short encouraging message to help someone improve their fruit intake."
        val request = GeminiRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt))))
        )
        return try {
            val response = GeminiClient.api.generateContent(apiKey, request)
            response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No tip found."
        } catch (e: Exception) {
            "Error fetching tip."
        }
    }

    fun getAverageScoreMale(): Flow<Float> = patientDao.getAverageScoreMale()
    fun getAverageScoreFemale(): Flow<Float> = patientDao.getAverageScoreFemale()

}