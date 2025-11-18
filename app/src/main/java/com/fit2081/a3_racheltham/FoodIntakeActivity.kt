package com.fit2081.a3_racheltham

import android.app.TimePickerDialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.Image
import androidx.compose.foundation.selection.toggleable
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fit2081.a3_racheltham.data.FoodIntake
import com.fit2081.a3_racheltham.data.NutriTrackViewModel
import com.fit2081.a3_racheltham.ui.theme.A3_RachelThamTheme
import java.util.Calendar


class FoodIntakeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            A3_RachelThamTheme {
                FoodIntakeScreen {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
            }
        }
    }
}
@Composable
fun FoodIntakeScreen(onDone: ()->Unit) {
    val context = LocalContext.current
    // assume userId was stored on login
    val userId = remember {
        context.getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            .getString("userID","")!!
    }
    val vm: NutriTrackViewModel = viewModel()

    // 1) observe existing intake (null if none)
    val existing by vm.getIntake(userId).collectAsState(initial = null)

    // 2) UI state
    val categories = remember {
        mutableStateMapOf(
            "Fruits" to false, "Vegetables" to false, "Grains" to false,
            "Red Meat" to false, "Seafood" to false, "Poultry" to false,
            "Fish" to false, "Eggs" to false, "Nuts/Seeds" to false
        )
    }
    var persona by remember { mutableStateOf("") }
    var biggestMeal by remember { mutableStateOf("") }
    var sleepTime   by remember { mutableStateOf("") }
    var wakeTime    by remember { mutableStateOf("") }

    var showModal by remember { mutableStateOf<Triple<String, Int, String>?>(null) }

    LaunchedEffect(existing) {
        existing?.let { fi ->
            fi.selectedCategories
                .split(",")
                .forEach { cat -> categories[cat] = true }
            persona     = fi.persona
            biggestMeal = fi.biggestMealTime
            sleepTime   = fi.sleepTime
            wakeTime    = fi.wakeUpTime
        }
    }

    // Persona descriptions and images
    val personaData = mapOf(
        "Health Devotee" to Triple("Health Devotee", R.drawable.persona_1, "I’m passionate about healthy eating & health plays a big part in my life. I use social media to follow active lifestyle personalities or get new recipes/exercise ideas. I may even buy superfoods or follow a particular type of diet. I like to think I am super healthy."),
        "Mindful Eater" to Triple("Mindful Eater", R.drawable.persona_2, "I’m health-conscious and being healthy and eating healthy is important to me. Although health means different things to different people, I make conscious lifestyle decisions about eating based on what I believe healthy means. I look for new recipes and healthy eating information on social media."),
        "Wellness Striver" to Triple("Wellness Striver", R.drawable.persona_3, "I aspire to be healthy (but struggle sometimes). Healthy eating is hard work! I’ve tried to improve my diet, but always find things that make it difficult to stick with the changes. Sometimes I notice recipe ideas or healthy eating hacks, and if it seems easy enough, I’ll give it a go."),
        "Balance Seeker" to Triple("Balance Seeker", R.drawable.persona_4, "I try and live a balanced lifestyle, and I think that all foods are okay in moderation. I shouldn’t have to feel guilty about eating a piece of cake now and again. I get all sorts of inspiration from social media like finding out about new restaurants, fun recipes and sometimes healthy eating tips."),
        "Health Procrastinator" to Triple("Health Procrastinator", R.drawable.persona_5, "I’m contemplating healthy eating but it’s not a priority for me right now. I know the basics about what it means to be healthy, but it doesn’t seem relevant to me right now. I have taken a few steps to be healthier but I am not motivated to make it a high priority because I have too many other things going on in my life."),
        "Food Carefree" to Triple("Food Carefree", R.drawable.persona_6, "I’m not bothered about healthy eating. I don’t really see the point and I don’t think about it. I don’t really notice healthy eating tips or recipes and I don’t care what I eat.")
    )

    Column(Modifier.padding(16.dp)) {
        Spacer(Modifier.height(40.dp))
        Text("Tick all the food categories you can eat", fontSize = 18.sp)
        Spacer(Modifier.height(8.dp))

        // Arrange checkboxes in 3 columns
        categories.keys.chunked(3).forEach { row ->
            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                row.forEach { food ->
                    Row(
                        Modifier
                            .toggleable(categories[food]!!) { categories[food] = it }
                            .padding(4.dp)
                            .weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(categories[food]!!, onCheckedChange = null)
                        Spacer(Modifier.width(4.dp))
                        Text(food, modifier = Modifier.padding(start = 4.dp), fontSize = 13.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Persona Description and Modal Dialog
        Text("Your Persona", fontSize = 18.sp)
        Text("People can be broadly classified into 6 different types based on their eating preferences.")
        Spacer(modifier = Modifier.height(8.dp))

        // 1) chunk persona keys into rows of three
        val personas = personaData.keys.toList()
        personas.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // 2) for each label in this row
                rowItems.forEach { label ->
                    Button(
                        onClick = {
                            // showModal holds the (label, imageRes, description)
                            personaData[label]?.let { (title, imageRes, desc) ->
                                showModal = Triple(title, imageRes, desc)
                            }
                        },
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(horizontal = 4.dp),
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 5.dp)
                        )
                    }
                }
            }
        }

// 3) Declare your dialog once, outside of the grid
        showModal?.let { (title, imageRes, desc) ->
            AlertDialog(
                onDismissRequest = { showModal = null },
                title   = { Text(title) },
                text    = {
                    Column {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(desc)
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        persona = title
                        showModal = null
                    }) {
                        Text("Select")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showModal = null }) {
                        Text("Cancel")
                    }
                }
            )
        }


        Spacer(Modifier.height(16.dp))
        Text("Which persona best fits you?", fontSize = 18.sp)
        var dropdownExpanded by remember { mutableStateOf(false) }
        Box(Modifier.fillMaxWidth()) {
            Button(onClick = { dropdownExpanded = true }) {
                Text(if (persona.isEmpty()) "Select Persona" else persona)
            }
            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false }
            ) {
                personaData.keys.forEach { label ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            persona = label
                            dropdownExpanded = false
                        }
                    )
                }
            }
        }


        Spacer(Modifier.height(16.dp))
        TimeRow("What time of day approx. do you normally eat your biggest meal?", biggestMeal) { biggestMeal = it }
        TimeRow("What time of day approx. do you go to sleep at night?", sleepTime) { sleepTime = it }
        TimeRow("What time of day approx. do you wake up in the morning?", wakeTime) { wakeTime = it }

        Spacer(Modifier.height(24.dp))
        val allFilled = persona.isNotBlank() &&
                biggestMeal.isNotBlank() &&
                sleepTime.isNotBlank() &&
                wakeTime.isNotBlank()

        Button(
            onClick = {
                val selected = categories
                    .filter { it.value }
                    .keys
                    .joinToString(",")

                val intake = FoodIntake(
                    userId = userId,
                    selectedCategories = selected,
                    persona = persona,
                    biggestMealTime = biggestMeal,
                    sleepTime = sleepTime,
                    wakeUpTime = wakeTime
                )

                vm.saveIntake(intake)
                onDone()
            },
            modifier = Modifier
                .fillMaxWidth(),
            enabled = allFilled
        ) {
            Text("Save")
        }

    }
}


@Composable
private fun TimeRow(label: String, current: String, onPicked: (String)->Unit) {
    var open by remember { mutableStateOf(false) }
    if (open) {
        val now = Calendar.getInstance()
        TimePickerDialog(
            LocalContext.current,
            { _, h,m -> onPicked("%02d:%02d".format(h,m)) },
            now.get(Calendar.HOUR_OF_DAY),
            now.get(Calendar.MINUTE),
            false
        ).show()
        open = false
    }
    Row(Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, modifier = Modifier.weight(1f))
        TextButton(onClick = { open = true }) {
            Text(if (current.isBlank()) "--:--" else current)
        }
    }
}