package com.example.wellbee.frontend.screens.Fisik

import android.graphics.Color.parseColor
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.wellbee.data.FisikRepository
import com.example.wellbee.frontend.navigation.PhysicalNavGraph
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PhysicalHealthScreen(parentNavController: NavHostController) {
    val localNavController = rememberNavController()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FB))
    ) {
        PhysicalNavGraph(navController = localNavController)
        PhysicalDashboardContent()
    }
}

@Composable
fun PhysicalDashboardContent() {
    val scrollState = rememberScrollState()
    val context = LocalContext.current.applicationContext

    // 1. Inisialisasi Repository & Scope
    val repo = remember { FisikRepository(context) }
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    // 2. State untuk Data Grafik
    // --- OLAHRAGA (MINGGUAN) ---
    var sportChartData by remember {
        mutableStateOf(
            FisikRepository.WeeklyChartData(
                labels = emptyList(),
                values = List(7) { 0.0 },
                rangeText = "Memuat..."
            )
        )
    }

    // --- TIDUR (MINGGUAN - BARU) ---
    var sleepChartData by remember {
        mutableStateOf(
            FisikRepository.WeeklyChartData(
                labels = emptyList(),
                values = List(7) { 0.0 },
                rangeText = ""
            )
        )
    }

    // --- BERAT BADAN (7 TERAKHIR) ---
    var weightValues by remember { mutableStateOf<List<Double>>(emptyList()) }
    var weightLabels by remember { mutableStateOf<List<String>>(emptyList()) }

    // 3. LOGIKA REFRESH DATA (ON_RESUME)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                scope.launch {
                    try {
                        Log.d("UI_UPDATE", "🔄 Refreshing data grafik...")

                        // A. Load Grafik Olahraga (Mingguan)
                        sportChartData = repo.getWeeklySportChartData()

                        // B. Load Grafik Tidur (Mingguan) 🔥 BARU
                        sleepChartData = repo.getWeeklySleepChartData()

                        // C. Load Grafik Berat Badan (7 Input Terakhir)
                        val weightRes = repo.getWeightHistory()
                        val weightList = weightRes.getOrNull() ?: emptyList()

                        if (weightList.isNotEmpty()) {
                            val last7Weight = weightList.take(7).reversed()
                            weightValues = last7Weight.map { it.beratBadan }

                            val outputFmt = SimpleDateFormat("dd MMM", Locale("id", "ID"))
                            val inputFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)

                            weightLabels = last7Weight.map {
                                try {
                                    val date = inputFmt.parse(it.tanggal)
                                    if (date != null) outputFmt.format(date) else it.tanggal
                                } catch (e: Exception) { it.tanggal }
                            }
                        } else {
                            weightValues = emptyList()
                            weightLabels = emptyList()
                        }

                        Log.d("UI_UPDATE", "✅ Data loaded")
                    } catch (e: Exception) {
                        Log.e("UI_UPDATE", "❌ Gagal load data", e)
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // === UI UTAMA ===
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // === Header ===
        Text(
            text = "Ringkasan Kesehatan Fisik",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0E4DA4)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Pantau aktivitas olahraga, kualitas tidur, dan berat badan kamu di sini.",
            color = Color.Gray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ==========================================
        // 1. GRAFIK OLAHRAGA (BAR CHART - MINGGUAN)
        // ==========================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Grafik Olahraga (menit)",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0E4DA4)
            )
            Text(
                text = sportChartData.rangeText,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (sportChartData.values.any { it > 0 }) {
            BarChartView(
                xLabels = sportChartData.labels,
                yValues = sportChartData.values,
                label = "Durasi Olahraga (menit)"
            )
        } else {
            EmptyChartBox("Belum ada data olahraga minggu ini")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ==========================================
        // 2. GRAFIK TIDUR (LINE CHART - MINGGUAN)
        // ==========================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Durasi Tidur (Jam)",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0E4DA4)
            )
            Text(
                text = sleepChartData.rangeText, // 🔥 Range Mingguan
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (sleepChartData.values.any { it > 0 }) {
            LineChartView(
                xLabels = sleepChartData.labels, // Sen, Sel, Rab...
                yValues = sleepChartData.values,
                label = "Durasi Tidur (Jam)",
                lineColor = "#6C5CE7", // Ungu
                fillColor = "#A29BFE"
            )
        } else {
            EmptyChartBox("Belum ada data tidur minggu ini")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ==========================================
        // 3. GRAFIK BERAT BADAN (LINE CHART - 7 TERAKHIR)
        // ==========================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Grafik Berat Badan (kg)",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0E4DA4)
            )
            Text(
                text = "7 Input Terakhir",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (weightValues.isNotEmpty()) {
            LineChartView(
                xLabels = weightLabels,
                yValues = weightValues,
                label = "Berat Badan (kg)",
                lineColor = "#0E4DA4",
                fillColor = "#B3D4FC"
            )
        } else {
            EmptyChartBox("Belum ada data berat badan")
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// === WIDGET BANTUAN ===

@Composable
fun EmptyChartBox(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.White, RoundedCornerShape(8.dp)),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.Gray,
            fontSize = 14.sp
        )
    }
}

// === KOMPONEN GRAFIK ===

@Composable
fun LineChartView(
    xLabels: List<String>,
    yValues: List<Double>,
    label: String,
    lineColor: String = "#0E4DA4",
    fillColor: String = "#B3D4FC"
) {
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                val entries = yValues.mapIndexed { index, value ->
                    Entry(index.toFloat(), value.toFloat())
                }

                val dataSet = LineDataSet(entries, label)
                dataSet.color = parseColor(lineColor)
                dataSet.setCircleColor(parseColor(lineColor))
                dataSet.valueTextColor = parseColor(lineColor)
                dataSet.valueTextSize = 10f
                dataSet.lineWidth = 2f
                dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
                dataSet.setDrawFilled(true)
                dataSet.fillColor = parseColor(fillColor)

                val lineData = LineData(dataSet)
                this.data = lineData

                xAxis.apply {
                    valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xLabels)
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1f
                    setDrawGridLines(false)
                    textColor = parseColor(lineColor)
                    textSize = 10f
                }

                axisLeft.textColor = parseColor(lineColor)
                axisRight.isEnabled = false
                description.isEnabled = false
                legend.textColor = parseColor(lineColor)

                invalidate()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    )
}

@Composable
fun BarChartView(
    xLabels: List<String>,
    yValues: List<Double>,
    label: String
) {
    AndroidView(
        factory = { context ->
            com.github.mikephil.charting.charts.BarChart(context).apply {
                val entries = yValues.mapIndexed { index, value ->
                    com.github.mikephil.charting.data.BarEntry(index.toFloat(), value.toFloat())
                }

                val dataSet = com.github.mikephil.charting.data.BarDataSet(entries, label)
                dataSet.color = parseColor("#0E4DA4")
                dataSet.valueTextSize = 10f

                val barData = com.github.mikephil.charting.data.BarData(dataSet)
                barData.barWidth = 0.5f
                this.data = barData

                xAxis.apply {
                    valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xLabels)
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1f
                    setDrawGridLines(false)
                    textColor = parseColor("#0E4DA4")
                }

                axisLeft.axisMinimum = 0f
                axisRight.isEnabled = false
                description.isEnabled = false
                legend.isEnabled = false

                setFitBars(true)
                invalidate()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
    )
}