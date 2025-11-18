package com.fit2081.a3_racheltham

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.fit2081.a3_racheltham.data.NutriTrackViewModel
import com.fit2081.a3_racheltham.api.GenAIViewModel
import com.fit2081.a3_racheltham.api.UiState
import com.fit2081.a3_racheltham.data.NutriCoachTip
import com.fit2081.a3_racheltham.ui.theme.A3_RachelThamTheme

class NutriCoachActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userID = sharedPref.getString("userID", "Unknown") ?: "Unknown"

        setContent {
            A3_RachelThamTheme {
                NutriCoachScreen(userID)
                }
            }
        }
    }

@Composable
fun NutriCoachScreen(
    userId: String,
) {
    val fruitVm: NutriTrackViewModel = viewModel()
    val aiVm:   GenAIViewModel       = viewModel()

    // — 1) Collect the scores for all categories —
    val vegScore by fruitVm.getVegetablesScore(userId).collectAsState(0f)
    val fruitScore by fruitVm.getFruitsScore(userId).collectAsState(0f)
    val cerealsScore by fruitVm.getCerealsScore(userId).collectAsState(0f)
    val wholeGrainsScore by fruitVm.getWholeGrainsScore(userId).collectAsState(0f)
    val meatScore by fruitVm.getMeatScore(userId).collectAsState(0f)
    val dairyScore by fruitVm.getDairyScore(userId).collectAsState(0f)
    val waterScore by fruitVm.getWaterScore(userId).collectAsState(0f)
    val satFatScore by fruitVm.getSaturatedFatsScore(userId).collectAsState(0f)
    val unsatFatScore by fruitVm.getUnsaturatedFatsScore(userId).collectAsState(0f)
    val sodiumScore by fruitVm.getSodiumScore(userId).collectAsState(0f)
    val sugarScore by fruitVm.getSugarScore(userId).collectAsState(0f)
    val alcoholScore by fruitVm.getAlcoholScore(userId).collectAsState(0f)
    val discFoodsScore by fruitVm.getDiscretionaryFoodsScore(userId).collectAsState(0f)

    // — 2) Load existing tips into state —
    LaunchedEffect(userId) { fruitVm.loadPastTips(userId) }

    // UI state
    var showTipsDialog by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    val fruitInfo by fruitVm.fruitResponse.collectAsState()
    val aiState by aiVm.uiState.collectAsState()
    val pastTips by fruitVm.pastTips.collectAsState()

    LaunchedEffect(aiState) {
        if (aiState is UiState.Success) {
            val msg = (aiState as UiState.Success).text
            fruitVm.saveTip(NutriCoachTip(userId = userId, tipText = msg))
            fruitVm.loadPastTips(userId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 80.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "NutriCoach",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(12.dp))

        // Fruits section
        Column(modifier = Modifier.weight(1f)) {
            if (fruitScore < 5f) {
                Text(text = "Fruit Name",
                    fontWeight = FontWeight.Bold)

                Row {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        label = { Text("Search fruit") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { fruitVm.fetchFruitInfo(query) }) {
                        Text("Details")
                    }
                }
                fruitInfo?.let { info ->
                    Spacer(Modifier.height(12.dp))
                    Text("Name: ${info.name}")
                    Text("Family: ${info.family}")
                    Text("Calories: ${info.nutritions.calories}")
                    Text("Fat: ${info.nutritions.fat}g")
                    Text("Sugar: ${info.nutritions.sugar}g")
                    Text("Carbs: ${info.nutritions.carbohydrates}g")
                    Text("Protein: ${info.nutritions.protein}g")
                }
            } else {
                Text("Great job on your fruits! 🎉")
                Spacer(Modifier.height(8.dp))
                AsyncImage(
                    model = "https://picsum.photos/400/200",
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
        }

        Divider(modifier = Modifier.padding(vertical = 14.dp))

        // GenAI section
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 0.dp) // no extra bottom padding
        ) {
            // 1) the “Ask” button
            item {
                val prompt = """
            Generate a short encouraging message to help someone improve their fruit intake. 
            Include emojis and make it feel friendly.
            Scores → Veg=$vegScore, Fruit=$fruitScore, Cereals=$cerealsScore, WholeGrains=$wholeGrainsScore,
            Meat=$meatScore, Dairy=$dairyScore, Water=$waterScore, SatFat=$satFatScore, UnsatFat=$unsatFatScore,
            Sodium=$sodiumScore, Sugar=$sugarScore, Alcohol=$alcoholScore, DiscretionaryFoods=$discFoodsScore.
          """.trimIndent()

                Button(onClick = { aiVm.sendPrompt(prompt) }) {
                    Text("Motivational message (AI)")
                }
            }

            // 2) the loading / content / error
            when (aiState) {
                UiState.Initial -> {
                    // nothing
                }
                UiState.Loading -> {
                    item { CircularProgressIndicator() }
                }
                is UiState.Error -> {
                    item { Text("Error: ${(aiState as UiState.Error).message}") }
                }
                is UiState.Success -> {
                    val message = (aiState as UiState.Success).text
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Text(
                                text = message,
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Show All Tips button, pinned at bottom right
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),  // ensure it sits above nav bar
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = { showTipsDialog = true }) {
                Text("Show All Tips")
            }
        }
    }

    if (showTipsDialog) {
        PastTipsModal(tips = pastTips, onDismiss = { showTipsDialog = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PastTipsModal(
    tips: List<NutriCoachTip>,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {

            Text(
                text = "AI Tips",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Scrollable list of cards
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false) // allow content to scroll if it grows
                    .padding(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tips) { tip ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = tip.tipText,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 8.dp)
            ) {
                Text("Done")
            }
        }
    }
}

