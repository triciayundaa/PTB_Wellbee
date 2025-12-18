package com.example.wellbee.frontend.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.wellbee.ui.theme.BluePrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ArticleCard(
    articleId: String,          // masih ikut dikirim, walau di sini tidak dipakai
    imageUrl: String?,          // URL gambar dari backend (sudah full)
    categories: List<String>,
    title: String,
    readTime: String,
    isBookmarked: Boolean,      // 🔹 status bookmark dikirim dari luar (ViewModel)
    onBookmarkClick: () -> Unit,// 🔹 aksi saat icon bookmark diklik
    onReadMoreClick: () -> Unit // 🔹 aksi saat tombol "Baca Selengkapnya" diklik
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // 🔹 Gambar utama / placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                if (!imageUrl.isNullOrBlank()) {
                    // Debug kecil ke Logcat (boleh dihapus nanti)
                    println("ArticleCard imageUrl = $imageUrl")

                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Gambar artikel $title",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "No Image",
                            tint = Color.Gray,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "No Image Available",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Column(Modifier.padding(horizontal = 16.dp)) {

                // 🔹 Daftar kategori/tag (chip)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    categories.forEach { tag ->
                        TagChip(
                            text = tag,
                            selected = false,
                            onClick = {}
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // 🔹 Judul artikel
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    color = Color.Black
                )

                Spacer(Modifier.height(6.dp))

                // 🔹 Estimasi waktu baca + tombol bookmark
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = readTime,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    IconButton(
                        onClick = onBookmarkClick   // 🔹 serahkan ke caller
                    ) {
                        Icon(
                            imageVector = if (isBookmarked)
                                Icons.Default.Bookmark
                            else
                                Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) BluePrimary else Color.Gray
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // 🔹 Tombol "Baca Selengkapnya"
                Button(
                    onClick = onReadMoreClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Baca Selengkapnya",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(12.dp))
            }
        }
    }
}
