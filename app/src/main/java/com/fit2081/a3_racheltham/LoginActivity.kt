package com.fit2081.a3_racheltham

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fit2081.a3_racheltham.data.NutriTrackViewModel
import com.fit2081.a3_racheltham.ui.theme.A3_RachelThamTheme
import java.io.BufferedReader
import java.io.InputStreamReader

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            A3_RachelThamTheme {
                LoginScreen()
            }
        }
    }
}

@Composable
fun LoginScreen() {
    val context = LocalContext.current
    val viewModel: NutriTrackViewModel = viewModel()
    val patients by viewModel.getAllPatients().collectAsState(initial = emptyList())

    var selectedID by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(patients) {
        patients.forEach {
            println("Patient: ID=${it.userId}, Phone=${it.phoneNumber}")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Log in",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown with User_ID options
        UserIDDropdown(
            userIDs = patients.map {it.userId},
            selectedID = selectedID,
            onSelectID = { selectedID = it }
        )

        // Add space between dropdown box and textField
        Spacer(modifier = Modifier.height(16.dp))

        // Password Input Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            placeholder = { Text("Enter your password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "This app is only for pre-registered users. Please enter\n" +
                    "your ID and password or Register to claim your\n" +
                    "account on your first visit.",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // Add space between text and button
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.authenticateUser(selectedID, password, context)
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Continue")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            context.startActivity(Intent(context, RegisterActivity::class.java))
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Divider
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp)
            Text(text = "            ")
            HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserIDDropdown(userIDs: List<String>, selectedID: String, onSelectID: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) } // State to control dropdown visibility

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it } // Ensures the menu opens when clicked
    ) {
        OutlinedTextField(
            value = selectedID,
            onValueChange = {},
            label = { Text("My ID (Provided by your Clinician)", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
            placeholder = { Text("012345") },
            readOnly = true, // Prevents manual input
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor() // Attach menu properly
        )

        ExposedDropdownMenu(
            expanded = expanded, // Controls whether the dropdown is visible
            onDismissRequest = { expanded = false } // Close dropdown when clicked outside
        ) {
            userIDs.forEach { id -> // Iterate over each user ID in the list
                DropdownMenuItem(
                    text = { Text(id) }, // Display user ID as text
                    onClick = {
                        onSelectID(id) // Update the selected user ID
                        expanded = false // Close it
                    }
                )
            }
        }

    }
}

