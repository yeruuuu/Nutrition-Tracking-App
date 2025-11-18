package com.fit2081.a3_racheltham

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fit2081.a3_racheltham.ui.theme.A3_RachelThamTheme
import androidx.core.content.edit

class NavigationOptions : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Retrieve userID from SharedPreferences
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userID = sharedPref.getString("userID", "Unknown") ?: "Unknown"

        setContent {
            A3_RachelThamTheme {
                val navController: NavHostController = rememberNavController() // for managing navigation within app
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        MyBottomAppBar(navController)
                    }
                ) { innerPadding ->
                    MyNavHost(innerPadding, navController, userID)
                }
            }
        }
    }
}

@Composable
fun MyNavHost(innerPadding: PaddingValues, navController: NavHostController, userID: String) {

    val context = LocalContext.current

    // Define the navigation routes for the app
    NavHost(
        navController = navController, // The navigation controller that manages the navigation stack.
        startDestination = "Home" // Initial screen
    ) {
        // Defining the their routes and screens composable.
        composable("Home") {
            HomeScreen(navController)
        }
        composable("Insights") {
            InsightsScreen(innerPadding, navController, userID)
        }
        composable("NutriCoach") {
            NutriCoachScreen(userID)
        }
        composable("Settings") {
            SettingsScreen(
                onLogout = {
                    // clear shared-prefs and go back to LoginActivity
                    context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                        .edit() {
                            remove("userID")
                        }
                    context.startActivity(
                        Intent(context, LoginActivity::class.java)
                            .apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                    )
                },
                onClinician = { navController.navigate("ClinicianLogin") }
            )
        }
        composable("ClinicianLogin") {
            ClinicianLoginScreen(
                onSuccess = { navController.navigate("AdminView") },
                onCancel  = { navController.popBackStack() }
            )
        }
        composable("AdminView") {
            AdminViewScreen()
        }
    }
}


@Composable
fun MyBottomAppBar(navController: NavHostController) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val items = listOf("Home", "Insights", "NutriCoach", "Settings")

    NavigationBar {
        items.forEach { item ->
            val icon = when (item) {
                "Home" -> Icons.Filled.Home
                "Insights" -> Icons.Filled.Check
                "NutriCoach" -> Icons.Filled.Face
                "Settings" -> Icons.Filled.Settings
                else -> Icons.Filled.Home
            }
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = item) },
                label = { Text(item) },
                selected = currentRoute == item || (currentRoute == "AdminView" && item == "Settings")
                        || (currentRoute == "ClinicianLogin" && item == "Settings"),
                onClick = {
                    if (currentRoute != item) {
                        navController.navigate(item)
                    }
                }
            )
        }
    }
}
