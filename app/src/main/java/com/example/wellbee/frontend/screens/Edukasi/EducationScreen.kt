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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.wellbee.frontend.components.ArticleCard
import com.example.wellbee.frontend.components.SearchBar
import com.example.wellbee.frontend.components.TagChip
import com.example.wellbee.ui.theme.BluePrimary
import com.example.wellbee.ui.theme.GrayBackground

@Composable
fun EducationScreen(navController: NavHostController) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Semua") }

    val categories = listOf(
        "Semua", "Kesehatan Mental", "Kesehatan Fisik",
        "Tips Sehat", "Nutrisi", "Keseimbangan Hidup", "Produktivitas"
    )

    // 🔹 Artikel bawaan (statis)
    val defaultArticles = EducationArticles.articles

    // 🔹 Artikel buatan user yang statusnya UPLOADED (diambil dari repository publik)
    val uploadedUserArticles = MyArticleRepository.getUploaded()

    // ==================== FILTERING ====================

    // Filter artikel bawaan
    val filteredDefault = defaultArticles.filter { article ->
        val matchCategory =
            selectedCategory == "Semua" || article.categories.contains(selectedCategory)
        val matchSearch =
            searchQuery.isBlank() || article.title.contains(searchQuery, ignoreCase = true)
        matchCategory && matchSearch
    }

    // Filter artikel user (kategori diambil dari category + tag)
    val filteredUser = uploadedUserArticles.filter { article ->
        val userCategories = buildList {
            add(article.category)
            if (article.tag.isNotBlank()) add(article.tag)
        }

        val matchCategory =
            selectedCategory == "Semua" || userCategories.contains(selectedCategory)
        val matchSearch =
            searchQuery.isBlank() || article.title.contains(searchQuery, ignoreCase = true)

        matchCategory && matchSearch
    }

    // ==================== UI ====================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GrayBackground)
    ) {
        // HEADER BIRU
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BluePrimary)
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Edukasi",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(16.dp))

        // 🔍 SEARCH BAR
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            onSearch = { searchQuery = it }
        )

        Spacer(Modifier.height(12.dp))

        // 🏷️ KATEGORI
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
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

        // ⭐ ARTIKEL TERSIMPAN (Bookmark)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Artikel Tersimpan", fontWeight = FontWeight.Bold)
            TextButton(onClick = {
                navController.navigate("bookmark")
            }) {
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

        // ✍️ BUAT ARTIKEL
        OutlinedButton(
            onClick = {
                navController.navigate("create_article_meta")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
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

        // 📰 ARTIKEL TERBARU + ARTIKEL SAYA
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Artikel Terbaru", fontWeight = FontWeight.Bold)
            TextButton(
                onClick = {
                    navController.navigate("my_articles")
                }
            ) {
                Text("Artikel Saya", color = BluePrimary)
            }
        }

        // 📚 LIST ARTIKEL (BAWAAN + USER)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // 1️⃣ Artikel bawaan
            items(filteredDefault, key = { it.id }) { article ->
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

            // 2️⃣ Artikel user yang di-upload
            items(filteredUser, key = { it.id }) { article ->
                val userCategories = buildList {
                    add(article.category)
                    if (article.tag.isNotBlank()) add(article.tag)
                }

                ArticleCard(
                    articleId = article.id,
                    title = article.title,
                    categories = userCategories,
                    readTime = article.readTime,
                    imageRes = null, // belum support gambar user
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
fun EducationScreenPreview() {
    val nav = rememberNavController()
    EducationScreen(navController = nav)
}
