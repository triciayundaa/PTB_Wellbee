package com.example.wellbee.frontend.screens.Home

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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.wellbee.data.model.EducationViewModel
import com.example.wellbee.data.model.PublicArticleDto
import com.example.wellbee.frontend.components.ArticleCard
import com.example.wellbee.ui.theme.BluePrimary
import com.example.wellbee.ui.theme.GreenAccent
import com.example.wellbee.ui.theme.GrayBackground
import com.example.wellbee.ui.theme.WellbeeTheme
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun HomeScreen(navController: NavHostController) {

    val context = LocalContext.current
    // Pakai ViewModel yang sama tipe-nya dengan EducationScreen
    val viewModel = remember { EducationViewModel(context) }

    val articles: List<PublicArticleDto> = viewModel.articles
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    // 🔹 bookmark dari backend
    val bookmarks = viewModel.bookmarks

    // Muat artikel & bookmark saat pertama kali masuk Home
    LaunchedEffect(Unit) {
        viewModel.loadArticles()
        viewModel.loadBookmarks()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GrayBackground)
    ) {
        // Header dengan Judul dan Ikon Profil
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BluePrimary)
                .padding(horizontal = 16.dp, vertical = 8.dp) // Penyesuaian padding untuk row
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

                // 🔹 IKON PROFIL UNTUK NAVIGASI KE PROFILE SCREEN
                IconButton(
                    onClick = {
                        // Ini akan mencari rute "profile" di parentNavController (NavGraph utama)
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

        // Statistik langkah & BMI (dummy sementara)
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("10.222", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("Langkah", color = Color.Gray)
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("60 kg", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("BMI: 19.5", color = Color.Gray)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Tidur (dummy juga)
        Card(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GreenAccent.copy(alpha = 0.15f))
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("8 Jam Tidur", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Kualitas: ⭐⭐⭐⭐☆", color = Color.DarkGray)
            }
        }

        Spacer(Modifier.height(12.dp))

        // Artikel terbaru
        Text(
            "Artikel Terbaru",
            modifier = Modifier.padding(horizontal = 16.dp),
            fontWeight = FontWeight.Bold
        )

        // ================= LIST ARTIKEL / LOADING / ERROR =================
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BluePrimary)
                }
            }

            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage,
                        color = Color.Red
                    )
                }
            }

            else -> {
                // 🔹 Urutkan artikel: terbaru (tanggal paling besar) di paling atas
                val sortedArticles = remember(articles) {
                    articles.sortedByDescending { parseBackendDateToMillis(it.tanggal) }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Tampilkan misalnya 3 artikel terbaru saja
                    items(sortedArticles.take(3)) { article ->

                        // Parsing tag seperti di EducationScreen
                        val artikelTags = article.tag
                            ?.split(",")
                            ?.map { it.trim() }
                            ?.filter { it.isNotEmpty() }
                            ?: emptyList()

                        // 🔹 Cek apakah artikel ini sudah di-bookmark (backend)
                        val existingBookmark = bookmarks.find { b ->
                            b.artikelId == article.id && b.jenis == article.jenis
                        }
                        val isBookmarked = existingBookmark != null

                        ArticleCard(
                            articleId = article.id.toString(),
                            imageUrl = article.gambarUrl,           // URL sudah diproses di repository
                            categories = artikelTags,
                            title = article.judul,
                            readTime = article.waktuBaca ?: "-",
                            onReadMoreClick = {
                                // ⬇️ PENTING: kirim source=public supaya cocok dengan route di EducationNavGraph
                                navController.navigate("article_detail/${article.id}?source=public")
                            },
                            isBookmarked = isBookmarked,
                            onBookmarkClick = {
                                if (existingBookmark == null) {
                                    // belum dibookmark → tambah ke backend
                                    viewModel.addBookmark(article.id, article.jenis)
                                } else {
                                    // sudah dibookmark → hapus bookmark di backend
                                    viewModel.deleteBookmark(existingBookmark.bookmarkId)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Helper yang sama seperti di EducationScreen.
 * Biar urutan artikel konsisten (terbaru di atas) di Home juga.
 */
private fun parseBackendDateToMillis(raw: String?): Long {
    if (raw.isNullOrBlank()) return 0L

    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd"
    )

    for (pattern in patterns) {
        try {
            val sdf = SimpleDateFormat(pattern, Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val date = sdf.parse(raw)
            if (date != null) return date.time
        } catch (_: Exception) {
        }
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