package com.fit2081.a3_racheltham

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fit2081.a3_racheltham.data.NutriTrackViewModel
import com.fit2081.a3_racheltham.ui.theme.A3_RachelThamTheme

class InsightsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userID = sharedPref.getString("userID", "Unknown") ?: "Unknown"

        setContent {
            A3_RachelThamTheme {
                val navController: NavHostController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { MyBottomAppBar(navController) }
                ) { innerPadding ->
                    InsightsScreen(innerPadding, navController, userID)
                }
            }
        }
    }
}

@Composable
fun InsightsScreen(innerPadding: PaddingValues, navController: NavHostController, userID: String) {
    val context = LocalContext.current
    val viewModel: NutriTrackViewModel = viewModel()

    val totalScore by viewModel.getTotalScore(userID).collectAsState(initial = 0f)
    val vegetables by viewModel.getVegetablesScore(userID).collectAsState(initial = 0f)
    val fruits by viewModel.getFruitsScore(userID).collectAsState(initial = 0f)
    val cereals by viewModel.getCerealsScore(userID).collectAsState(initial = 0f)
    val wholeGrains by viewModel.getWholeGrainsScore(userID).collectAsState(initial = 0f)
    val meat by viewModel.getMeatScore(userID).collectAsState(initial = 0f)
    val dairy by viewModel.getDairyScore(userID).collectAsState(initial = 0f)
    val water by viewModel.getWaterScore(userID).collectAsState(initial = 0f)
    val saturatedFats by viewModel.getSaturatedFatsScore(userID).collectAsState(initial = 0f)
    val unsaturatedFats by viewModel.getUnsaturatedFatsScore(userID).collectAsState(initial = 0f)
    val sodium by viewModel.getSodiumScore(userID).collectAsState(initial = 0f)
    val sugar by viewModel.getSugarScore(userID).collectAsState(initial = 0f)
    val alcohol by viewModel.getAlcoholScore(userID).collectAsState(initial = 0f)
    val discretionary by viewModel.getDiscretionaryFoodsScore(userID).collectAsState(initial = 0f)

    val scoreList = listOf(
        Triple("Vegetables", vegetables, 10f),
        Triple("Fruits", fruits, 10f),
        Triple("Grains & Cereals", cereals, 5f),
        Triple("Whole Grains", wholeGrains, 5f),
        Triple("Meat & Alternatives", meat, 10f),
        Triple("Dairy", dairy, 10f),
        Triple("Water", water, 5f),
        Triple("Saturated Fats", saturatedFats, 5f),
        Triple("Unsaturated Fats", unsaturatedFats, 5f),
        Triple("Sodium", sodium, 10f),
        Triple("Sugar", sugar, 10f),
        Triple("Alcohol", alcohol, 5f),
        Triple("Discretionary Foods", discretionary, 10f)
    )


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text("Insights: Food Score", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        }

        items(scoreList) { (label, value, max) ->
            ScoreRow(label = label, score = value, max = max)
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text("Total Food Quality Score", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Slider(
                    value = totalScore,
                    onValueChange = {},
                    valueRange = 0f..100f,
                    steps = 9,
                    modifier = Modifier
                        .height(20.dp)
                        .weight(1f),
                    enabled = false
                )
                Text(
                    text = "${"%.2f".format(totalScore)}/100",
                    fontSize = 14.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(70.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            val shareText = "My score is ${"%.2f".format(totalScore)} !!\nVisit: https://www.monash.edu/medicine/scs/nutrition/clinics/"

            Button(onClick = {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                }
                context.startActivity(Intent.createChooser(intent, "Share score via"))
            }) {
                Icon(Icons.Filled.Share, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Share with someone", fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Button(onClick = { navController.navigate("NutriCoach") }) {
                Text("Improve my diet!", fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun ScoreRow(label: String, score: Float, max: Float) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "${"%.1f".format(score)}/${max.toInt()}",
                fontSize = 13.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.width(60.dp)
            )
        }

        Slider(
            value = score,
            onValueChange = {},
            valueRange = 0f..max,
            steps = max.toInt() - 1,
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp),
            enabled = false
        )
    }
}
