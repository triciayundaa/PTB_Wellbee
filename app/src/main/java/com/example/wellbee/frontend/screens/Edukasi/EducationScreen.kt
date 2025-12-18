package com.example.wellbee.frontend.screens.Edukasi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.BookmarkAdd
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
import com.example.wellbee.frontend.components.SearchBar
import com.example.wellbee.frontend.components.TagChip
import com.example.wellbee.ui.theme.BluePrimary
import com.example.wellbee.ui.theme.GrayBackground
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun EducationScreen(navController: NavHostController, viewModel: EducationViewModel) {
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Semua") }

    // Load data awal
    LaunchedEffect(Unit) {
        viewModel.loadCategories()
        viewModel.loadBookmarks()
        viewModel.loadArticles()
    }

    val articles: List<PublicArticleDto> = viewModel.articles
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    val backendCategories = viewModel.categories
    val categories = remember(backendCategories) { listOf("Semua") + backendCategories }
    val bookmarks = viewModel.bookmarks

    // Filter berdasarkan kategori
    val filteredByCategory = remember(articles, selectedCategory) {
        articles.filter { article ->
            selectedCategory == "Semua" ||
                    (article.kategori != null && article.kategori.equals(selectedCategory, ignoreCase = true))
        }
    }

    // Filter berdasarkan searchQuery + sort terbaru
    val filteredArticlesSorted = remember(filteredByCategory, searchQuery) {
        filteredByCategory
            .filter { article ->
                searchQuery.isBlank() || article.judul.contains(searchQuery, ignoreCase = true)
            }
            .sortedByDescending { parseBackendDateToMillis(it.tanggal) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GrayBackground)
    ) {
        // HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BluePrimary)
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Education",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = BluePrimary
            )
        } else Spacer(Modifier.height(4.dp))

        // SEARCH BAR
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                onSearch = { query -> searchQuery = query }
            )
        }

        Spacer(Modifier.height(12.dp))

        // KATEGORI
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                TagChip(
                    text = category,
                    selected = category == selectedCategory,
                    onClick = { selectedCategory = category }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // MAIN CONTENT
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {

            item {
                // ARTIKEL TERSIMPAN HEADER
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Artikel Tersimpan", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    TextButton(onClick = { navController.navigate("bookmark") }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Lihat Semua", color = BluePrimary)
                            Icon(
                                Icons.Default.ArrowForwardIos,
                                contentDescription = null,
                                tint = BluePrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // BUTTON BUAT ARTIKEL
                OutlinedButton(
                    onClick = { navController.navigate("create_article_meta") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = BluePrimary.copy(alpha = 0.08f)
                    )
                ) {
                    Icon(Icons.Default.BookmarkAdd, contentDescription = null, tint = BluePrimary)
                    Spacer(Modifier.width(8.dp))
                    Text("Buat Artikel", color = BluePrimary, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = BluePrimary)
                }

                Spacer(Modifier.height(16.dp))

                // ARTIKEL TERBARU HEADER
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Artikel Terbaru", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    TextButton(onClick = { navController.navigate("my_articles") }) {
                        Text("Artikel Saya", color = BluePrimary)
                    }
                }

                Spacer(Modifier.height(8.dp))

                // ERROR MESSAGE
                if (!errorMessage.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFEBEE), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                }
            }

            if (filteredArticlesSorted.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isNotBlank()) "Artikel tidak ditemukan." else "Belum ada artikel tersimpan.",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(filteredArticlesSorted, key = { it.id }) { article ->
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
                        isBookmarked = isBookmarked,
                        onBookmarkClick = {
                            if (existingBookmark == null) {
                                viewModel.addBookmark(article.id, article.jenis)
                            } else {
                                viewModel.deleteBookmark(existingBookmark.bookmarkId)
                            }
                        },
                        onReadMoreClick = {
                            navController.navigate("article_detail/${article.id}?source=public")
                        }
                    )
                }
            }
        }
    }
}

// ==========================
// UTILITY: parse date backend
// ==========================
private fun parseBackendDateToMillis(raw: String?): Long {
    if (raw.isNullOrBlank()) return 0L
    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
        "yyyy-MM-dd'T'HH:mm:ssXXX",
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
    return raw.hashCode().toLong()
}

