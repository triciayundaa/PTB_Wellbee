package com.example.wellbee.frontend.screens.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.wellbee.frontend.screens.Edukasi.BookmarkManager
import com.example.wellbee.frontend.screens.Edukasi.EducationArticles
import com.example.wellbee.ui.theme.BluePrimary
import com.example.wellbee.ui.theme.GreenAccent
import com.example.wellbee.ui.theme.GrayBackground
import com.example.wellbee.ui.theme.WellbeeTheme

@Composable
fun HomeScreen(navController: NavHostController) {

    // 👉 Pakai artikel dari modul edukasi
    val articles = EducationArticles.articles

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

        // Statistik langkah & BMI
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

        // Tidur
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

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(articles) { article ->
                val isBookmarked = BookmarkManager.isBookmarked(article.id)

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
