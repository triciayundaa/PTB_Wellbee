package com.example.wellbee.frontend.screens.Home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import com.example.wellbee.data.model.EducationViewModel
import com.example.wellbee.data.model.PublicArticleDto
import com.example.wellbee.frontend.components.ArticleCard
import com.example.wellbee.ui.theme.BluePrimary
import com.example.wellbee.ui.theme.GrayBackground
import com.example.wellbee.ui.theme.WellbeeTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

@Composable
fun HomeScreen(navController: NavHostController) {

    // 1. SETUP CONTEXT & REPOSITORY
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    // Repository Fisik (Fathiya)
    val fisikRepo = remember { FisikRepository(context) }

    // ViewModel Edukasi (Kamu)
    val educationViewModel = remember { EducationViewModel(context) }

    // 2. STATE DATA FISIK (Dari Fathiya)
    var sportDuration by remember { mutableStateOf("-") }
    var sportCalories by remember { mutableStateOf("Langkah") }
    var weightValue by remember { mutableStateOf("- kg") }
    var bmiValue by remember { mutableStateOf("BMI: -") }
    var sleepDuration by remember { mutableStateOf("- Jam Tidur") }
    var sleepQuality by remember { mutableStateOf("Kualitas: -") }

    // 3. STATE DATA ARTIKEL (Dari Kamu)
    val articles: List<PublicArticleDto> = educationViewModel.articles
    val isLoading = educationViewModel.isLoading
    val errorMessage = educationViewModel.errorMessage
    val bookmarks = educationViewModel.bookmarks

    // 4. LOGIKA LOAD DATA

    // Load Artikel saat pertama kali (Kamu)
    LaunchedEffect(Unit) {
        educationViewModel.loadArticles()
        educationViewModel.loadBookmarks()
    }

    // Load Data Fisik saat layar aktif/resume (Fathiya)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                scope.launch {
                    try {
                        // A. Olahraga
                        val sportRes = fisikRepo.getSportHistory()
                        val latestSport = sportRes.getOrNull()?.firstOrNull()
                        if (latestSport != null) {
                            sportDuration = "${latestSport.durasiMenit}"
                            sportCalories = "${latestSport.kaloriTerbakar} kcal"
                        } else {
                            sportDuration = "0"
                            sportCalories = "0 kcal"
                        }

                        // B. Berat Badan & BMI
                        val weightRes = fisikRepo.getWeightHistory()
                        val latestWeight = weightRes.getOrNull()?.firstOrNull()
                        if (latestWeight != null) {
                            weightValue = "${latestWeight.beratBadan} kg"
                            val bmiBulat = latestWeight.bmi.roundToInt()
                            bmiValue = "BMI: $bmiBulat (${latestWeight.kategori})"
                        }

                        // C. Tidur & Kualitas
                        val sleepRes = fisikRepo.getSleepHistory()
                        val latestSleep = sleepRes.getOrNull()?.firstOrNull()
                        if (latestSleep != null) {
                            sleepDuration = "${latestSleep.durasiTidur} Jam Tidur"
                            sleepQuality = "Kualitas: ${latestSleep.kualitasTidur}/5"
                        }
                    } catch (e: Exception) {
                        Log.e("HOME_DATA", "Gagal load data fisik", e)
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Warna Khusus (Fathiya)
    val CardGreen = Color(0xFFD9F2E6)
    val TextBlue = Color(0xFF0E4DA4)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GrayBackground)
    ) {
        // ================= HEADER (PUNYA KAMU - Ada Profil) =================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BluePrimary)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Wellbee",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )

                // Tombol Profil (Navigasi)
                IconButton(
                    onClick = {
                        navController.navigate("profile")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile",
                        tint = Color.White
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ================= KARTU FISIK (PUNYA FATHIYA) =================
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
                colors = CardDefaults.cardColors(containerColor = CardGreen)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.Bottom) {
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
                colors = CardDefaults.cardColors(containerColor = CardGreen)
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
            colors = CardDefaults.cardColors(containerColor = CardGreen)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(sleepDuration, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextBlue)
                Text(sleepQuality, color = TextBlue.copy(alpha = 0.8f))
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            "Artikel Terbaru",
            modifier = Modifier.padding(horizontal = 16.dp),
            fontWeight = FontWeight.Bold
        )

        // ================= LIST ARTIKEL (PUNYA KAMU - Real Data) =================
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BluePrimary)
                }
            }
            errorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = errorMessage, color = Color.Red)
                }
            }
            else -> {
                val sortedArticles = remember(articles) {
                    articles.sortedByDescending { parseBackendDateToMillis(it.tanggal) }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sortedArticles.take(3)) { article ->

                        val artikelTags = article.tag
                            ?.split(",")
                            ?.map { it.trim() }
                            ?.filter { it.isNotEmpty() }
                            ?: emptyList()

                        val existingBookmark = bookmarks.find { b ->
                            b.artikelId == article.id && b.jenis == article.jenis
                        }
                        val isBookmarked = existingBookmark != null

                        ArticleCard(
                            articleId = article.id.toString(),
                            imageUrl = article.gambarUrl,
                            categories = artikelTags,
                            title = article.judul,
                            readTime = article.waktuBaca ?: "-",
                            onReadMoreClick = {
                                navController.navigate("article_detail/${article.id}?source=public")
                            },
                            isBookmarked = isBookmarked,
                            onBookmarkClick = {
                                if (existingBookmark == null) {
                                    educationViewModel.addBookmark(article.id, article.jenis)
                                } else {
                                    educationViewModel.deleteBookmark(existingBookmark.bookmarkId)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

// Helper Date Parser (Punya Kamu)
private fun parseBackendDateToMillis(raw: String?): Long {
    if (raw.isNullOrBlank()) return 0L
    val patterns = listOf("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd")
    for (pattern in patterns) {
        try {
            val sdf = SimpleDateFormat(pattern, Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val date = sdf.parse(raw)
            if (date != null) return date.time
        } catch (_: Exception) {}
    }
    return raw.hashCode().toLong()
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewHomeScreen() {
    WellbeeTheme {
        val nav = rememberNavController()
        HomeScreen(navController = nav)
    }
}