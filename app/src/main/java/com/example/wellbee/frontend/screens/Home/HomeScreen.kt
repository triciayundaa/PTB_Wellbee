package com.example.wellbee.frontend.screens.Home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.wellbee.data.FisikRepository
import com.example.wellbee.frontend.components.ArticleCard
import com.example.wellbee.frontend.screens.Edukasi.BookmarkManager
import com.example.wellbee.frontend.screens.Edukasi.EducationArticles
import com.example.wellbee.ui.theme.BluePrimary
import com.example.wellbee.ui.theme.GrayBackground
import com.example.wellbee.ui.theme.WellbeeTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun HomeScreen(navController: NavHostController) {

    // 1. Setup Repository & Context
    val context = LocalContext.current
    val repo = remember { FisikRepository(context) }
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    // 2. State Data
    var sportDuration by remember { mutableStateOf("-") }
    var sportCalories by remember { mutableStateOf("Langkah") }

    var weightValue by remember { mutableStateOf("- kg") }
    var bmiValue by remember { mutableStateOf("BMI: -") }

    var sleepDuration by remember { mutableStateOf("- Jam Tidur") }
    var sleepQuality by remember { mutableStateOf("Kualitas: -") }

    // 3. LOGIKA UPDATE DATA
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                scope.launch {
                    try {
                        // A. Olahraga
                        val sportRes = repo.getSportHistory()
                        val latestSport = sportRes.getOrNull()?.firstOrNull()
                        if (latestSport != null) {
                            sportDuration = "${latestSport.durasiMenit}"
                            sportCalories = "${latestSport.kaloriTerbakar} kcal"
                        } else {
                            sportDuration = "0"
                            sportCalories = "0 kcal"
                        }

                        // B. Berat Badan & BMI
                        val weightRes = repo.getWeightHistory()
                        val latestWeight = weightRes.getOrNull()?.firstOrNull()
                        if (latestWeight != null) {
                            weightValue = "${latestWeight.beratBadan} kg"

                            val bmiBulat = latestWeight.bmi.roundToInt()
                            bmiValue = "BMI: $bmiBulat (${latestWeight.kategori})"
                        }

                        // C. Tidur & Kualitas
                        val sleepRes = repo.getSleepHistory()
                        val latestSleep = sleepRes.getOrNull()?.firstOrNull()
                        if (latestSleep != null) {
                            sleepDuration = "${latestSleep.durasiTidur} Jam Tidur"
                            sleepQuality = "Kualitas: ${latestSleep.kualitasTidur}/5"
                        }

                    } catch (e: Exception) {
                        Log.e("HOME_DATA", "Gagal load data home", e)
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val articles = EducationArticles.articles

    // 🔥 PERUBAHAN WARNA DISINI SESUAI MENTAL HEALTH SCREEN 🔥
    val CardGreen = Color(0xFFD9F2E6) // Warna Hijau dari MentalHealthScreen
    val TextBlue = Color(0xFF0E4DA4)  // Biru Tua

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GrayBackground)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BluePrimary)
                .padding(16.dp)
        ) {
            Text(
                "Wellbee",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        }

        Spacer(Modifier.height(16.dp))

        // ROW: OLAHRAGA & BERAT BADAN
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // CARD KIRI: OLAHRAGA
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                colors = CardDefaults.cardColors(containerColor = CardGreen) // ✅ Hijau Baru
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.Bottom) {
                        Text(sportDuration, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextBlue)
                        if(sportDuration != "-" && sportDuration != "0") {
                            Text(" menit", fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp, start = 2.dp), color = TextBlue)
                        }
                    }
                    Text(sportCalories, fontSize = 14.sp, color = TextBlue.copy(alpha = 0.8f))
                }
            }

            // CARD KANAN: BERAT BADAN
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                colors = CardDefaults.cardColors(containerColor = CardGreen) // ✅ Hijau Baru
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(weightValue, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextBlue)
                    Text(bmiValue, fontSize = 12.sp, lineHeight = 14.sp, color = TextBlue.copy(alpha = 0.8f))
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // CARD TENGAH: TIDUR
        Card(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardGreen) // ✅ Hijau Baru
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(sleepDuration, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextBlue)
                Text(sleepQuality, color = TextBlue.copy(alpha = 0.8f))
            }
        }

        Spacer(Modifier.height(16.dp))

        // Artikel
        Text(
            "Artikel Terbaru",
            modifier = Modifier.padding(horizontal = 16.dp),
            fontWeight = FontWeight.Bold
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(articles) { article ->
                ArticleCard(
                    articleId = article.id,
                    title = article.title,
                    categories = article.categories,
                    readTime = article.readTime,
                    imageRes = article.imageRes,
                    onReadMoreClick = {
                        navController.navigate("article_detail/${article.id}")
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewHomeScreen() {
    WellbeeTheme {
        val nav = rememberNavController()
        HomeScreen(navController = nav)
    }
}