package com.fit2081.a3_racheltham

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fit2081.a3_racheltham.data.NutriTrackViewModel
import com.fit2081.a3_racheltham.ui.theme.A3_RachelThamTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            A3_RachelThamTheme {
                SettingsScreen(
                    onLogout = {
                        val prefs = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                        prefs.edit().remove("userID").apply()
                        startActivity(Intent(this, LoginActivity::class.java)
                            .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK })
                    },
                    onClinician = {
                        startActivity(
                            Intent(this, ClinicianLoginActivity::class.java)
                                .apply {
                                // clear back stack if you like:
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    onClinician: () -> Unit
) {
    val viewModel: NutriTrackViewModel = viewModel()
    val context = LocalContext.current
    val userId = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        .getString("userID", "") ?: ""
    val patient by viewModel.getPatient(userId).observeAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings", fontSize = 20.sp) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Upper half: user details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                patient?.let { p ->
                    DetailRow(icon = Icons.Default.Person, label = p.name ?: p.userId)
                    DetailRow(icon = Icons.Default.Phone, label = p.phoneNumber)
                    DetailRow(icon = Icons.Default.Info, label = p.userId)
                }
            }

            Divider(thickness = 1.dp)

            // Lower half: action buttons
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout")
                }
                Button(
                    onClick = onClinician,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Person, contentDescription = "Clinician Login")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Clinician Login")
                }
            }
        }
    }
}

@Composable
fun DetailRow(icon: ImageVector, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(label, fontSize = 16.sp)
    }
}
