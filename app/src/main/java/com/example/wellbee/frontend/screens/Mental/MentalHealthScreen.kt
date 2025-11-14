package com.example.wellbee.frontend.screens.mental

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MentalHealthScreen(navController: NavHostController) {

    val emojiList = listOf(
        "😄" to "Senang",
        "🙂" to "Netral",
        "😢" to "Sedih",
        "😭" to "Sangat Sedih",
        "😡" to "Marah"
    )

    var selectedEmoji by remember { mutableStateOf<Pair<String, String>?>(null) }
    var moodScale by remember { mutableStateOf(5f) }
    var moodHistory by remember { mutableStateOf(listOf<Int>()) }
    var isSaved by remember { mutableStateOf(false) }
    var selectedRange by remember { mutableStateOf("7 Days") }
    val dropdownItems = listOf("7 Days", "Today")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF105490))
                .padding(top = 40.dp, bottom = 20.dp, start = 20.dp)
        ) {
            Text(
                text = "Mental Health",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search here...") },
            shape = RoundedCornerShape(30.dp)
        )

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .animateContentSize()
        ) {

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD9F2E6)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "How’s your mood today?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF105490)
                    )
                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        emojiList.forEach { (emoji, label) ->
                            val isSelected = selectedEmoji?.first == emoji
                            Text(
                                text = emoji,
                                fontSize = if (isSelected) 56.sp else 36.sp,
                                modifier = Modifier
                                    .scale(if (isSelected) 1.25f else 1f)
                                    .clickable(enabled = !isSaved) {
                                        selectedEmoji = emoji to label
                                    }
                            )
                        }
                    }
                }
            }

            selectedEmoji?.let { (emoji, moodLabel) ->

                Spacer(Modifier.height(20.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F9FF)),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            "Skala Mood: ${moodScale.toInt()}/10",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF105490)
                        )

                        Slider(
                            value = moodScale,
                            onValueChange = { if (!isSaved) moodScale = it },
                            valueRange = 1f..10f,
                            enabled = !isSaved,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF105490),
                                activeTrackColor = Color(0xFF105490)
                            )
                        )

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Sangat Buruk", fontSize = 12.sp)
                            Text("Sangat Baik", fontSize = 12.sp)
                        }

                        Spacer(Modifier.height(16.dp))

                        if (!isSaved) {
                            Button(
                                onClick = {
                                    moodHistory = (moodHistory + moodScale.toInt()).takeLast(7)
                                    isSaved = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF105490)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Save", color = Color.White)
                            }
                        } else {
                            Button(
                                onClick = {
                                    isSaved = false
                                    selectedEmoji = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE67E22)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Edit Mood", color = Color.White)
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                        Text("Mood hari ini", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Kategori: $moodLabel", color = Color(0xFF105490))
                        Text("Skala: ${moodScale.toInt()}/10", color = Color(0xFF105490))
                        Spacer(Modifier.height(12.dp))
                        Text(emoji, fontSize = 56.sp)
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text("Statistik mood", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                var expanded by remember { mutableStateOf(false) }
                Box {
                    Text(
                        selectedRange,
                        modifier = Modifier
                            .clickable { expanded = true }
                            .background(Color(0xFFD9EFFF), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        color = Color(0xFF105490)
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        dropdownItems.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    selectedRange = item
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            val data = when {
                selectedRange == "Today" && moodHistory.isNotEmpty() -> listOf(moodHistory.last())
                moodHistory.isNotEmpty() -> moodHistory.takeLast(7)
                else -> listOf(5, 6, 8, 7, 9, 7, 8)
            }

            val chartEntries = data.mapIndexed { index, value -> entryOf(index, value) }
            val chartModel = ChartEntryModelProducer(chartEntries)

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .background(Color(0xFFF7F8FB), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Chart(
                        chart = lineChart(),
                        chartModelProducer = chartModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { navController.navigate("diary") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD9F2E6)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("✏️ Write something", color = Color(0xFF105490), fontSize = 16.sp)
            }

            Spacer(Modifier.height(60.dp))
        }
    }
}

annotation class MentalHealthScreen
