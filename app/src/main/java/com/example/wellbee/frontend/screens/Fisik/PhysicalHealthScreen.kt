package com.example.wellbee.frontend.screens.Fisik

import android.graphics.Color.parseColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.example.wellbee.frontend.navigation.PhysicalNavGraph
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun PhysicalHealthScreen(parentNavController: NavHostController) {
    val localNavController = rememberNavController()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FB))
    ) {
        // 🔥 Jalankan NavGraph lokal (untuk dashboard, sport, sleep, weight)
        PhysicalNavGraph(navController = localNavController)
    }
}

@Composable
fun PhysicalDashboardContent() {
    val scrollState = rememberScrollState() // ✅ Tambahkan ini untuk scroll

    // === Dummy data (nanti bisa diambil dari database/backend) ===
    val hariMinggu = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
    val durasiTidur = listOf(7.5, 8.0, 6.5, 7.0, 7.8, 6.0, 8.2) // jam
    val durasiOlahraga = listOf(150.0, 90.0, 60.0, 100.0, 120.0, 45.0, 80.0) // menit
    val beratBadan = listOf(53.0, 52.5, 52.0, 52.2, 52.3, 51.8, 52.1) // kg

    val rataTidur = durasiTidur.average()

    // ✅ Bungkus seluruh konten dalam Column + verticalScroll
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

        // === Card Statistik Ringkas ===
        CardStat(
            title = "Total Hari Olahraga Minggu Ini",
            value = "4 Hari",
            backgroundColor = Color(0xFFDFFFE3)
        )

        CardStat(
            title = "Rata-rata Durasi Tidur",
            value = "${"%.1f".format(rataTidur)} Jam / malam",
            backgroundColor = Color(0xFFEAF1F8)
        )

        CardStat(
            title = "Berat Badan Terakhir",
            value = "${beratBadan.last()} kg",
            backgroundColor = Color(0xFFFFF2E0)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // === Grafik Olahraga ===
        Text(
            "Grafik Olahraga (menit)",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0E4DA4)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LineChartView(
            xLabels = hariMinggu,
            yValues = durasiOlahraga,
            label = "Durasi Olahraga (menit)"
        )

        Spacer(modifier = Modifier.height(20.dp))

        // === Grafik Berat Badan ===
        Text(
            "Grafik Berat Badan (kg)",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0E4DA4)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LineChartView(
            xLabels = hariMinggu,
            yValues = beratBadan,
            label = "Berat Badan (kg)"
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun CardStat(title: String, value: String, backgroundColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = Color(0xFF0E4DA4))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.Bold)
        }
    }
}



@Composable
fun LineChartView(xLabels: List<String>, yValues: List<Double>, label: String) {
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                val entries = yValues.mapIndexed { index, value ->
                    Entry(index.toFloat(), value.toFloat())
                }

                val dataSet = LineDataSet(entries, label)
                dataSet.color = ColorTemplate.getHoloBlue()
                dataSet.setCircleColor(ColorTemplate.getHoloBlue())
                dataSet.valueTextColor = parseColor("#0E4DA4")
                dataSet.valueTextSize = 10f
                dataSet.lineWidth = 2f
                dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
                dataSet.setDrawFilled(true)
                dataSet.fillColor = parseColor("#B3D4FC")

                val lineData = LineData(dataSet)
                this.data = lineData

                xAxis.valueFormatter =
                    com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xLabels)
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.granularity = 1f
                xAxis.textColor = parseColor("#0E4DA4")

                axisLeft.textColor = parseColor("#0E4DA4")
                axisRight.isEnabled = false

                description.isEnabled = false
                legend.textColor = parseColor("#0E4DA4")

                invalidate()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    )
}
