package com.fit2081.a3_racheltham

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
import androidx.compose.ui.unit.dp
import com.fit2081.a3_racheltham.ui.theme.A3_RachelThamTheme

class ClinicianLoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            A3_RachelThamTheme {
                ClinicianLoginScreen(
                    onSuccess = {
                        // Navigate to the AdminViewActivity
                        startActivity(Intent(this, ClinicianActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                    },
                    onCancel = {
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun ClinicianLoginScreen(
    onSuccess: () -> Unit,
    onCancel: () -> Unit
) {
    var accessKey by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Clinician Login", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = accessKey,
            onValueChange = {
                accessKey = it
                errorMessage = null
            },
            label = { Text("Access Key") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Row {
            Button(onClick = {
                if (accessKey == "dollar-entry-apples") {
                    onSuccess()
                } else {
                    errorMessage = "Invalid access key"
                }
            }) {
                Text("Login")
            }
            Spacer(Modifier.width(8.dp))
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        }
    }
}
