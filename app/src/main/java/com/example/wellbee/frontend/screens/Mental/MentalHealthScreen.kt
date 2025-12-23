package com.example.wellbee.frontend.screens.Mental

import android.graphics.Color.parseColor
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.example.wellbee.data.local.AppDatabase
import com.example.wellbee.data.local.MentalMoodEntity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MentalHealthScreen(navController: NavHostController) {
    val context = LocalContext.current
    val dao = remember { AppDatabase.getInstance(context).mentalDao() }
    val scope = rememberCoroutineScope()

    val moodList by dao.observeMoodByUser(1).collectAsState(initial = emptyList())
    val journalList by dao.observeJournalsByUser(1).collectAsState(initial = emptyList())

    val emojiList = listOf(
        "😄" to "Senang",
        "🙂" to "Netral",
        "😢" to "Sedih",
        "😭" to "Sangat Sedih",
        "😡" to "Marah"
    )

    var selectedEmoji by remember { mutableStateOf<Pair<String, String>?>(null) }
    var moodScale by remember { mutableFloatStateOf(5f) }

    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val moodsToday = moodList.count { it.tanggal == today }
    val journalsToday = journalList.count { it.tanggal == today }

    val isPendingMood = moodsToday > journalsToday

    var currentMoodId by remember { mutableStateOf<Int?>(null) }

    val chartData = remember(moodList) {
        val sortedList = moodList.sortedBy { it.tanggal }
        val batchSize = 7
        val total = sortedList.size
        
        if (total == 0) {
            emptyList()
        } else {

            val startIndex = ((total - 1) / batchSize) * batchSize
            sortedList.drop(startIndex)
        }
    }

    val xLabels = remember(chartData) {
        chartData.map {
            try {
                val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formatter = SimpleDateFormat("dd/MM", Locale.getDefault())
                formatter.format(parser.parse(it.tanggal) ?: Date())
            } catch (e: Exception) {
                it.tanggal
            }
        }
    }

    val yValues = remember(chartData) {
        chartData.map { it.moodScale.toDouble() }
    }

    var selectedRange by remember { mutableStateOf("7 Days") }
    val dropdownItems = listOf("7 Days", "Today")

    LaunchedEffect(moodList, journalList) {
        if (isPendingMood) {

            val lastMoodToday = moodList.filter { it.tanggal == today }.maxByOrNull { it.id } 
            if (lastMoodToday != null) {
                currentMoodId = lastMoodToday.id
                selectedEmoji = emojiList.find { it.first == lastMoodToday.emoji }
                moodScale = lastMoodToday.moodScale.toFloat()
            }
        } else {

            currentMoodId = null
            selectedEmoji = null
            moodScale = 5f
        }
    }

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
                        if (isPendingMood) "Update Your Mood" else "How’s your mood today?",
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
                                    .clickable {
                                        selectedEmoji = emoji to label
                                    }
                            )
                        }
                    }
                }
            }

            if (selectedEmoji != null) {
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
                            onValueChange = { moodScale = it },
                            valueRange = 1f..10f,
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

                        val (emoji, moodLabel) = selectedEmoji!!

                        Button(
                            onClick = {
                                scope.launch {
                                    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                                    
                                    if (isPendingMood && currentMoodId != null) {

                                        val updatedMood = MentalMoodEntity(
                                            id = currentMoodId!!,
                                            userId = 1,
                                            emoji = emoji,
                                            moodLabel = moodLabel,
                                            moodScale = moodScale.toInt(),
                                            tanggal = currentDate
                                        )
                                        dao.updateMood(updatedMood)
                                    } else {

                                        val newMood = MentalMoodEntity(
                                            userId = 1,
                                            emoji = emoji,
                                            moodLabel = moodLabel,
                                            moodScale = moodScale.toInt(),
                                            tanggal = currentDate
                                        )
                                        dao.insertMood(newMood)
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF105490)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isPendingMood) "Update Mood" else "Save Mood", color = Color.White)
                        }
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Statistik Mood", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF105490))
                
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

            if (chartData.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        LineChartViewMental(
                            xLabels = xLabels,
                            yValues = yValues,
                            label = "Mood Scale"
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color(0xFFF7F8FB), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada data mood.", color = Color.Gray)
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
                Text("✏️ Write Journal", color = Color(0xFF105490), fontSize = 16.sp)
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("journal_list") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF105490)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("📖 View My Journals", color = Color.White, fontSize = 16.sp)
            }

            Spacer(Modifier.height(60.dp))
        }
    }
}

@Composable
fun LineChartViewMental(xLabels: List<String>, yValues: List<Double>, label: String) {
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = false
                
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.granularity = 1f
                xAxis.textColor = parseColor("#105490")
                xAxis.setDrawGridLines(false)

                axisLeft.textColor = parseColor("#105490")
                axisLeft.axisMinimum = 0f
                axisLeft.axisMaximum = 10f
                axisRight.isEnabled = false
            }
        },
        update = { chart ->
            if (xLabels.isEmpty() || yValues.isEmpty()) return@AndroidView

            val entries = yValues.mapIndexed { index, value ->
                Entry(index.toFloat(), value.toFloat())
            }

            val dataSet = LineDataSet(entries, label).apply {
                color = parseColor("#105490")
                setCircleColor(parseColor("#105490"))
                valueTextColor = parseColor("#105490")
                valueTextSize = 10f
                lineWidth = 2f
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setDrawFilled(true)
                fillColor = parseColor("#D9EFFF")
            }

            val lineData = LineData(dataSet)
            chart.data = lineData
            chart.xAxis.valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xLabels)

            chart.notifyDataSetChanged()
            chart.invalidate()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    )
}