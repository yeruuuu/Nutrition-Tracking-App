package com.fit2081.a3_racheltham

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.fit2081.a3_racheltham.api.GenAIViewModel
import com.fit2081.a3_racheltham.api.UiState
import com.fit2081.a3_racheltham.data.NutriTrackViewModel
import com.fit2081.a3_racheltham.ui.theme.A3_RachelThamTheme
import com.google.gson.Gson

class ClinicianActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            A3_RachelThamTheme {
                AdminViewScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminViewScreen() {
    val dataVm: NutriTrackViewModel = viewModel()
    val aiVm:   GenAIViewModel      = viewModel()

    // 1) average HEIFA scores by sex
    val avgMale   by dataVm.getAverageScoreMale().collectAsState(0f)
    val avgFemale by dataVm.getAverageScoreFemale().collectAsState(0f)

    // 2) pull all patients from Room
    val patients by dataVm.getAllPatients().collectAsState(emptyList())

    // 3) AI state
    val aiState  by aiVm.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Clinician Dashboard", fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center) })
        },
        bottomBar = {
            MyBottomAppBar(navController = rememberNavController())
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Average HEIFA Scores", fontSize = 18.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Card(Modifier
                        .weight(1f)
                        .padding(end = 8.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Male", fontSize = 16.sp)
                            Text(String.format("%.2f", avgMale), fontSize = 24.sp)
                        }
                    }
                    Card(Modifier.weight(1f)) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Female", fontSize = 16.sp)
                            Text(String.format("%.2f", avgFemale), fontSize = 24.sp)
                        }
                    }
                }
            }

            item {
                Divider(thickness = 1.dp)
                Spacer(Modifier.height(16.dp))

                Button(onClick = {
                    // serialize your full patients list to JSON
                    val json = Gson().toJson(patients)
                    aiVm.generateDataPatterns(json)
                }) {
                    Text("Find Data Patterns")
                }
            }


            when (aiState) {
                UiState.Initial -> { /* nothing */ }
                UiState.Loading -> item {
                    CircularProgressIndicator()
                }
                is UiState.Error -> item {
                    Text("Error: ${(aiState as UiState.Error).message}")
                }
                is UiState.Success -> {
                    // get up to 3 bullets
                    val bullets = (aiState as UiState.Success).text
                        .lines()
                        .map { it.trim().removePrefix("•").trim() }
                        .filter { it.isNotEmpty() }
                        .take(3)


                    items(bullets) { rawLine ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            MarkdownText(
                                markdown = " $rawLine",
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun MarkdownText(markdown: String, modifier: Modifier = Modifier) {
    // Recompute only when markdown changes
    val annotated = remember(markdown) {
        buildAnnotatedString {
            var currentIndex = 0
            // Find **bold** spans
            val regex = Regex("\\*\\*(.*?)\\*\\*")
            for (match in regex.findAll(markdown)) {
                // append text before bold span
                append(markdown.substring(currentIndex, match.range.first))
                // append bold span
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(match.groupValues[1])
                }
                currentIndex = match.range.last + 1
            }
            // append any trailing text
            if (currentIndex < markdown.length) {
                append(markdown.substring(currentIndex))
            }
        }
    }

    Text(
        text = annotated,
        modifier = modifier
    )
}


