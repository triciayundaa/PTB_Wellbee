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
import androidx.navigation.NavController
import com.example.wellbee.frontend.components.ArticleCard
import com.example.wellbee.frontend.components.SearchBar
import com.example.wellbee.frontend.components.TagChip
import com.example.wellbee.ui.theme.BluePrimary
import com.example.wellbee.ui.theme.GrayBackground
import com.example.wellbee.ui.theme.WellbeeTheme

@Composable
fun EducationScreen(navController: NavController? = null) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Semua") }

    val categories = listOf(
        "Semua",
        "Kesehatan Mental",
        "Kesehatan Fisik",
        "Tips Sehat",
        "Nutrisi",
        "Keseimbangan Hidup",
        "Produktivitas"
    )

    // Data dummy artikel
    val articles = listOf(
        ArticleData(
            title = "Cara Mengatasi Stress Sehari-hari",
            category = "Kesehatan Mental",
            readTime = "5 menit",
            tags = listOf("stress", "mental", "relax"),
        ),
        ArticleData(
            title = "Pola Makan Sehat untuk Aktivitas Padat",
            category = "Nutrisi",
            readTime = "4 menit",
            tags = listOf("makan", "sehat", "gizi"),
        ),
        ArticleData(
            title = "7 Cara Menjaga Postur Tubuh Saat Bekerja",
            category = "Kesehatan Fisik",
            readTime = "3 menit",
            tags = listOf("postur", "fisik", "kerja"),
        )
    )

    val filteredArticles = articles.filter {
        (selectedCategory == "Semua" || it.category == selectedCategory) &&
                (searchQuery.isEmpty() || it.title.contains(searchQuery, ignoreCase = true))
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

        // ARTIKEL TERSIMPAN
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Artikel Tersimpan", fontWeight = FontWeight.Bold)
            TextButton(onClick = { navController?.navigate("bookmark_screen") }) {
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
            onClick = { navController?.navigate("create_article") },
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

        // ARTIKEL TERBARU
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Artikel Terbaru", fontWeight = FontWeight.Bold)
            TextButton(onClick = { navController?.navigate("noted_screen") }) {
                Text("Artikel Saya", color = BluePrimary)
            }
        }

        // LIST ARTIKEL
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            items(filteredArticles) { article ->
                ArticleCard(
                    title = "Tidur Berkualitas di Era Digital",
                    categories = listOf("Kesehatan", "Tidur", "Lifestyle"),
                    readTime = "5 menit",
                    imageRes = null,
                    onBookmarkClick = { /* simpan ke bookmark */ },
                    onReadMoreClick = { /* navigasi ke detail artikel */ }
                )

            }
        }
    }
}

// MODEL DATA ARTIKEL
data class ArticleData(
    val title: String,
    val category: String,
    val readTime: String,
    val tags: List<String>
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewEducationScreen() {
    WellbeeTheme {
        EducationScreen()
    }
}
