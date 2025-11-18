package com.fit2081.a3_racheltham

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.fit2081.a3_racheltham.data.NutriTrackViewModel
import com.fit2081.a3_racheltham.ui.theme.A3_RachelThamTheme
import java.io.BufferedReader
import java.io.InputStreamReader

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userID = sharedPref.getString("userID", "Unknown") ?: "Unknown"

        setContent {
            A3_RachelThamTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { MyBottomAppBar(navController) } // Bottom navigation bar
                ) { innerPadding ->
                    MyNavHost(innerPadding, navController, userID) // Navigation host

                }
            }
        }
    }
}

// Composable for the Home Screen UI
@Composable
fun HomeScreen(navController: NavHostController) {
    val viewModel: NutriTrackViewModel = viewModel()
    val context = LocalContext.current
    val userId = remember { context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE).getString("userID", "") ?: "" }
    val foodQualityScore by viewModel.getTotalScore(userId).collectAsState(initial = 0f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Greeting user by ID
        Text("Hello, ${getUserId(context) ?: "User"}", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "See all score >",
            color = Color.Blue,
            fontSize = 14.sp,
            modifier = Modifier
                .clickable {
                    navController.navigate("Insights")
                }
        )


        // "Edit" button row with instructions
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start // Align contents to the left
        ) {
            Text(
                text = "You've already filled in your Food Intake\n" +
                        "Questionnaire, but you can change details here: ",
                fontSize = 13.sp,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End // Align contents to the left
            ) {
                Button(
                    onClick = {
                        // Navigate back to questionnaire screen
                        context.startActivity(Intent(context, FoodIntakeActivity::class.java))
                    } ,
                    shape = RoundedCornerShape(15.dp),
                ) {
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Edit",
                        fontSize = 15.sp,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Image centered in the screen
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.home_image),
                contentDescription = "Home Image",
                modifier = Modifier.size(300.dp)
            )
        }

        // Display user’s total food quality score
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Your Food Quality Score: ", fontSize = 18.sp)
            Text(
                text = foodQualityScore?.toString() ?: "No data available",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Green
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Divider between sections
        HorizontalDivider(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .fillMaxWidth(),
            thickness = 1.dp,
            color = Color.LightGray
        )

        // Explanation of what the Food Quality Score means
        Text(
            text = "What is the Food Quality Score?",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
        )
        Text(
            text = "Your Food Quality Score provides a snapshot of how well your\n"+
                    "eating patterns align with established food guidelines, helping\n"+
                    "you identify both strengths and opportunities for improvement\n"+
                    "in your diet. \n\n" +
                    "This personalized measurement considers various food groups\n"+
                    "including vegetables, fruits, whole grains and proteins to give\n"+
                    "you practical insights for making healthier food choices.",
            fontSize = 12.sp
        )

    }
}

// Utility functions to handle user data

// Reads saved user ID from SharedPreferences
fun getUserId(context: Context): String? {
    val sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    return sharedPref.getString("userID", null)
}





