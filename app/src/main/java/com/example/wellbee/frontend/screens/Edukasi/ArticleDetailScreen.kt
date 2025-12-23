package com.example.wellbee.frontend.screens.Edukasi

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.wellbee.R
import com.example.wellbee.frontend.components.EducationTopBarWithBack
import com.example.wellbee.ui.theme.BluePrimary
import com.example.wellbee.ui.theme.GrayBackground
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun ArticleDetailScreen(
    navController: NavHostController,
    articleId: String,
    title: String,
    category: String,
    readTime: String,
    imageRes: Int? = null,
    imageUrl: String? = null,
    content: String,
    isUserArticle: Boolean = false,
    authorName: String? = null,
    uploadedDate: String? = null
) {

    val tanggalText: String = remember(uploadedDate) {
        formatDateOnly(uploadedDate)
    }

    Scaffold(
        topBar = {
            EducationTopBarWithBack(
                title = "Detail Artikel",
                onBackClick = { navController.popBackStack() }
            )
        },
        containerColor = GrayBackground
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            Card(
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                    ) {
                        val painter = when {
                            !imageUrl.isNullOrBlank() -> rememberAsyncImagePainter(model = imageUrl)
                            imageRes != null -> painterResource(id = imageRes)
                            else -> painterResource(id = R.drawable.ic_launcher_foreground)
                        }

                        Image(
                            painter = painter,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
                    ) {

                        Text(
                            text = "${category.uppercase()} • $readTime",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BluePrimary,
                            letterSpacing = 1.sp
                        )

                        Spacer(Modifier.height(12.dp))


                        Text(
                            text = title,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1B3B6B),
                            lineHeight = 34.sp
                        )

                        Spacer(Modifier.height(20.dp))


                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(GrayBackground, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = BluePrimary,
                                modifier = Modifier.size(32.dp).padding(4.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (!authorName.isNullOrBlank()) authorName else if (isUserArticle) "Kamu" else "Wellbee Team",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(
                                    text = "Diterbitkan pada $tanggalText",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Spacer(Modifier.height(24.dp))
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                        Spacer(Modifier.height(24.dp))

                        Text(
                            text = content,
                            fontSize = 16.sp,
                            lineHeight = 26.sp,
                            color = Color(0xFF444444),
                            textAlign = TextAlign.Justify
                        )

                        Spacer(Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}


private fun formatDateOnly(raw: String?): String {
    if (raw.isNullOrBlank()) return "Baru saja"

    return try {

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        val date = inputFormat.parse(raw)
        val outFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

        if (date != null) outFormat.format(date) else raw.take(10)
    } catch (e: Exception) {

        raw.take(10)
    }
}

@Preview(showBackground = true)
@Composable
fun ArticleDetailScreenPreview() {
    ArticleDetailScreen(
        navController = rememberNavController(),
        articleId = "1",
        title = "Makanan dengan Nutrisi Seimbang untuk Tubuh",
        category = "Nutrisi",
        readTime = "6 menit",
        imageRes = R.drawable.ic_launcher_foreground,
        content = "Makanan dengan nutrisi seimbang adalah makanan yang mengandung karbohidrat, protein, lemak, vitamin, dan mineral dalam jumlah yang sesuai dengan kebutuhan tubuh. Mengonsumsi makanan bergizi sangat penting untuk menjaga kesehatan jangka panjang...\n\nNutrisi yang baik memberikan energi bagi tubuh untuk beraktivitas sepanjang hari.",
        isUserArticle = false,
        authorName = "Tricia",
        uploadedDate = "2025-12-18T10:00:00.000Z"
    )
}