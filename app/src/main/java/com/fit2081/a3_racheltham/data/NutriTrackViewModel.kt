package com.fit2081.a3_racheltham.data

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asLiveData
import com.fit2081.a3_racheltham.BuildConfig
import com.fit2081.a3_racheltham.FoodIntakeActivity
import com.fit2081.a3_racheltham.LoginActivity
import com.fit2081.a3_racheltham.api.FruitResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn


class NutriTrackViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NutriTrackRepository(application.applicationContext)

    init {
        viewModelScope.launch {
            repository.importCsvIfNeeded()
            Log.d("GeminiKeyCheck", BuildConfig.GEMINI_API_KEY)
        }
    }

    // Patients
    fun getPatient(userId: String) = repository.getPatient(userId).asLiveData()

    fun getAllPatients(): Flow<List<Patient>> = repository.getAllPatients()

    fun registerUser(userId: String, phone: String, name: String, password: String, context: Context) {
        viewModelScope.launch {
            val patient = repository.findPatientByIdAndPhone(userId, phone)
            if (patient != null) {
                repository.updatePatientCredentials(userId, name, password)
                Toast.makeText(context, "Registered successfully!", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, LoginActivity::class.java))
            } else {
                Toast.makeText(context, "Invalid ID or Phone", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getIntake(userId: String): Flow<FoodIntake?> = repository.getIntake(userId)

    fun saveIntake(intake: FoodIntake) { viewModelScope.launch { repository.saveIntake(intake)}}

    // Authentication & registration
    fun authenticateUser(userId: String, password: String, context: Context) {
        viewModelScope.launch {
            val patient = repository.authenticate(userId, password)
            if (patient != null) {
                val prefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                prefs.edit().putString("userID", patient.userId).apply()
                Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, FoodIntakeActivity::class.java))
            } else {
                Toast.makeText(context, "Incorrect credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Scores
    fun getTotalScore(userId: String): Flow<Float> = repository.getTotalScore(userId)
    fun getVegetablesScore(userId: String): Flow<Float> = repository.getVegetablesScore(userId)
    fun getFruitsScore(userId: String): Flow<Float> = repository.getFruitsScore(userId)
    fun getCerealsScore(userId: String): Flow<Float> = repository.getCerealsScore(userId)
    fun getWholeGrainsScore(userId: String): Flow<Float> = repository.getWholeGrainsScore(userId)
    fun getMeatScore(userId: String): Flow<Float> = repository.getMeatScore(userId)
    fun getDairyScore(userId: String): Flow<Float> = repository.getDairyScore(userId)
    fun getWaterScore(userId: String): Flow<Float> = repository.getWaterScore(userId)
    fun getSaturatedFatsScore(userId: String): Flow<Float> = repository.getSaturatedFatsScore(userId)
    fun getUnsaturatedFatsScore(userId: String): Flow<Float> = repository.getUnsaturatedFatsScore(userId)
    fun getSodiumScore(userId: String): Flow<Float> = repository.getSodiumScore(userId)
    fun getSugarScore(userId: String): Flow<Float> = repository.getSugarScore(userId)
    fun getAlcoholScore(userId: String): Flow<Float> = repository.getAlcoholScore(userId)
    fun getDiscretionaryFoodsScore(userId: String): Flow<Float> = repository.getDiscretionaryFoodsScore(userId)

    // Fruityvice API integration

    // fetch from Fruityvice API
    private val _fruitResponse = MutableStateFlow<FruitResponse?>(null)
    val fruitResponse: StateFlow<FruitResponse?> = _fruitResponse

    fun fetchFruitInfo(name: String) = viewModelScope.launch {
        try {
            _fruitResponse.value = repository.getFruitInfo(name)
        } catch (e: Exception) {
            _fruitResponse.value = null
        }
    }

    fun saveTip(tip: NutriCoachTip) {
        viewModelScope.launch {
            repository.insertTip(tip)
            // optionally reload past tips right after saving
            loadPastTips(tip.userId)
        }
    }


    // past tips
    private val _pastTips = MutableStateFlow<List<NutriCoachTip>>(emptyList())
    val pastTips: StateFlow<List<NutriCoachTip>> = _pastTips

    fun loadPastTips(userId: String) {
        repository.getTipsForUser(userId)
            .onEach { tips ->
                val unique = tips.distinctBy { it.tipText }
                _pastTips.value = unique
            }
            .launchIn(viewModelScope)
    }

    fun getAverageScoreMale() = repository.getAverageScoreMale()
    fun getAverageScoreFemale() = repository.getAverageScoreFemale()

}