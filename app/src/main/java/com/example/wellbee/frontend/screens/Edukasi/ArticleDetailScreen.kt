package com.example.wellbee.frontend.screens.Edukasi

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.wellbee.R
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
    imageRes: Int? = null,      // fallback drawable lokal
    imageUrl: String? = null,   // URL gambar dari backend
    content: String,
    isUserArticle: Boolean = false,
    authorName: String? = null,
    uploadedDate: String? = null
) {

    // Label penulis
    val penulisLabel: String? = when {
        !authorName.isNullOrBlank() -> authorName
        isUserArticle -> "Kamu"
        else -> null
    }

    // Format tanggal
    val tanggalText: String = remember(uploadedDate) {
        formatDateOnly(uploadedDate)
    }

    Scaffold(
        containerColor = Color.White, // Menggunakan putih agar konten teks lebih bersih
        topBar = {
            Surface(shadowElevation = 4.dp) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BluePrimary)
                        .statusBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Kembali",
                                tint = Color.White
                            )
                        }
                        Text(
                            text = "Education",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Artikel Detail",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 20.dp, bottom = 16.dp)
                    )
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Gambar Utama (Full Width tanpa Card yang membungkus teks)
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
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // 2. Konten Artikel
            Column(
                modifier = Modifier
                    .padding(20.dp)
            ) {
                // Kategori Chip
                Surface(
                    color = BluePrimary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = category.uppercase(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BluePrimary
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Judul Besar
                Text(
                    text = title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1A1A1A),
                    lineHeight = 32.sp
                )

                Spacer(Modifier.height(16.dp))

                // Metadata (Penulis & Tanggal)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Info Penulis & Tanggal
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (penulisLabel != null) "Oleh $penulisLabel" else "Oleh Wellbee Team",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1A1A1A)
                        )
                        Text(
                            text = tanggalText,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    // Info Waktu Baca
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = readTime,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                Spacer(Modifier.height(20.dp))

                // Isi Artikel (Body)
                Text(
                    text = content,
                    fontSize = 16.sp,
                    lineHeight = 26.sp,
                    color = Color(0xFF333333),
                    textAlign = TextAlign.Justify
                )

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

/* ─────────────────────── FORMAT TANGGAL ─────────────────────── */

private fun formatDateOnly(raw: String?): String {
    if (raw.isNullOrBlank()) return ""

    return try {
        val inputFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }

        val date = inputFormat.parse(raw)
        val outFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

        if (date != null) outFormat.format(date) else raw.take(10)
    } catch (e: Exception) {
        raw.take(10)
    }
}

/* ─────────────────────── PREVIEW ─────────────────────── */

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ArticleDetailScreenPreview() {
    ArticleDetailScreen(
        navController = rememberNavController(),
        articleId = "1",
        title = "makanan dengan nutrisi seimbang",
        category = "Nutrisi",
        readTime = "6 menit",
        imageRes = R.drawable.ic_launcher_foreground,
        content = "Makanan dengan nutrisi seimbang adalah makanan yang mengandung karbohidrat, protein, lemak, vitamin, dan mineral dalam jumlah yang sesuai dengan kebutuhan tubuh. Mengonsumsi makanan bergizi sangat penting untuk menjaga kesehatan jangka panjang...\n\nbdjdkfhfkfk",
        isUserArticle = false,
        authorName = "trici",
        uploadedDate = "2025-12-18T00:00:00.000Z"
    )
}