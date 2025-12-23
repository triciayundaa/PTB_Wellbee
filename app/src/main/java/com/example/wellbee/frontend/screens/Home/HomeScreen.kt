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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.wellbee.data.viewmodel.FisikViewModel
import com.example.wellbee.data.viewmodel.FisikViewModelFactory
import com.example.wellbee.data.model.EducationViewModel
import com.example.wellbee.data.model.PublicArticleDto
import com.example.wellbee.frontend.components.ArticleCard
import com.example.wellbee.ui.theme.BluePrimary
import com.example.wellbee.ui.theme.GrayBackground
import com.example.wellbee.ui.theme.WellbeeTheme
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    navController: NavHostController,
    fisikViewModel: FisikViewModel = viewModel(
        factory = FisikViewModelFactory(LocalContext.current)
    )
) {
    val context = LocalContext.current
    val educationViewModel = remember { EducationViewModel(context) }
    
    val articles: List<PublicArticleDto> = educationViewModel.articles
    val isLoadingEdu = educationViewModel.isLoading
    val eduErrorMessage = educationViewModel.errorMessage
    val bookmarks = educationViewModel.bookmarks
    
    LaunchedEffect(Unit) {
        fisikViewModel.loadAllData()

        educationViewModel.loadArticles()
        educationViewModel.loadBookmarks()
    }

    val sportList by fisikViewModel.sportList.collectAsState()
    val sleepList by fisikViewModel.sleepList.collectAsState()
    val weightList by fisikViewModel.weightList.collectAsState()

    var sportDuration by remember { mutableStateOf("-") }
    var sportCalories by remember { mutableStateOf("Langkah") }
    var weightValue by remember { mutableStateOf("- kg") }
    var bmiValue by remember { mutableStateOf("BMI: -") }
    var sleepDuration by remember { mutableStateOf("- Jam Tidur") }
    var sleepQuality by remember { mutableStateOf("Kualitas: -") }

    LaunchedEffect(sportList, sleepList, weightList) {
        val latestSport = sportList.firstOrNull()
        if (latestSport != null) {
            sportDuration = "${latestSport.durasiMenit}"
            sportCalories = "${latestSport.kaloriTerbakar} kcal"
        } else {
            sportDuration = "0"
            sportCalories = "0 kcal"
        }

        val latestWeight = weightList.firstOrNull()
        if (latestWeight != null) {
            weightValue = "${latestWeight.beratBadan} kg"
            val bmiBulat = latestWeight.bmi.roundToInt()
            bmiValue = "BMI: $bmiBulat (${latestWeight.kategori})"
        }

        val latestSleep = sleepList.firstOrNull()
        if (latestSleep != null) {
            sleepDuration = "${latestSleep.durasiTidur} Jam Tidur"
            sleepQuality = "Kualitas: ${latestSleep.kualitasTidur}/5"
        }
    }

    val CardGreen = Color(0xFFD9F2E6)
    val TextBlue = Color(0xFF0E4DA4)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GrayBackground)
    ) {
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

                IconButton(onClick = { navController.navigate("profile") }) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile",
                        tint = Color.White
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
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

        when {
            isLoadingEdu -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BluePrimary)
                }
            }
            eduErrorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = eduErrorMessage, color = Color.Red)
                }
            }
            else -> {
                val sortedArticles = remember(articles) {
                    articles.sortedWith(
                        compareByDescending<PublicArticleDto> { parseBackendDateToMillis(it.tanggal) }
                            .thenByDescending { it.id }
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sortedArticles.take(3), key = { "${it.jenis}_${it.id}" }) { article ->
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
                                navController.navigate("article_detail/${article.id}?source=${article.jenis}")
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

private fun parseBackendDateToMillis(raw: String?): Long {
    if (raw.isNullOrBlank()) return 0L
    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
        "yyyy-MM-dd'T'HH:mm:ssXXX",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd"
    )
    for (pattern in patterns) {
        try {
            val sdf = SimpleDateFormat(pattern, Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val date = sdf.parse(raw)
            if (date != null) return date.time
        } catch (_: Exception) {}
    }
    return 0L
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewHomeScreen() {
    WellbeeTheme {
        val nav = rememberNavController()
        HomeScreen(navController = nav)
    }
}