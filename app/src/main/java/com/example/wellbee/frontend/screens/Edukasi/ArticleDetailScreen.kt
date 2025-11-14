package com.example.wellbee.frontend.screens.Edukasi

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.wellbee.R
import com.example.wellbee.ui.theme.BluePrimary
import com.example.wellbee.ui.theme.GrayBackground

@Composable
fun ArticleDetailScreen(
    navController: NavHostController,
    articleId: String,          // 🔹 dipakai untuk bookmark & markAsRead
    title: String,
    category: String,
    readTime: String,
    imageRes: Int? = null,
    content: String,
    // 🔽 tambahan untuk info penulis & tanggal
    isUserArticle: Boolean = false,
    authorName: String? = null,
    uploadedDate: String? = null
) {
    // baca status bookmark dari BookmarkManager
    val isBookmarked = BookmarkManager.isBookmarked(articleId)
    var showDownloadSuccess by remember { mutableStateOf(false) }

    // 🔹 Tandai artikel ini sebagai "sudah dibaca" saat screen dibuka
    LaunchedEffect(articleId) {
        BookmarkManager.markAsRead(articleId)
    }

    // 🔹 Tentukan label penulis
    val penulisLabel: String? = when {
        isUserArticle -> "Kamu"
        !authorName.isNullOrBlank() -> authorName
        else -> null
    }

    // 🔹 Tentukan teks tanggal (kalau null, pakai default seperti sebelumnya)
    val tanggalText: String = uploadedDate ?: "19 Oktober 2025"

    Scaffold(
        containerColor = GrayBackground,
        topBar = {
            // HEADER BIRU
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BluePrimary)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = "Education",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start
                    )

                    IconButton(
                        onClick = { BookmarkManager.toggleBookmark(articleId) }
                    ) {
                        Icon(
                            imageVector = if (isBookmarked)
                                Icons.Default.Bookmark
                            else
                                Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = Color.White
                        )
                    }
                }

                Text(
                    text = "Artikel Detail",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, bottom = 12.dp)
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {

            // CARD ATAS: gambar + header artikel + tombol download
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F9FC)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // Gambar artikel
                    if (imageRes != null) {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = "Gambar Artikel",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.height(16.dp))
                    }

                    // Judul + tombol download
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = BluePrimary,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = {
                                showDownloadSuccess = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Download Artikel",
                                tint = BluePrimary
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Info meta artikel (kategori • waktu baca + tanggal)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$category • $readTime",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = tanggalText,
                            fontSize = 12.sp,
                            color = BluePrimary
                        )
                    }

                    // 🔹 Baris "Penulis" (hanya kalau ada label)
                    if (penulisLabel != null) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Penulis: $penulisLabel",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            // Subjudul / judul section konten
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    lineHeight = 24.sp
                )
            )

            Spacer(Modifier.height(6.dp))

            // ISI ARTIKEL
            Text(
                text = content,
                color = Color(0xFF333333),
                fontSize = 14.sp,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(32.dp))
        }

        // POPUP DOWNLOAD BERHASIL
        if (showDownloadSuccess) {
            DownloadSuccessDialog(
                onDismiss = { showDownloadSuccess = false }
            )
        }
    }
}

/* ───────────────────────────── DIALOG DOWNLOAD BERHASIL ─────────────────── */

@Composable
private fun DownloadSuccessDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF7EA4C9) // biru muda seperti desain
            ),
            modifier = Modifier
                .fillMaxWidth(0.85f)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFF21436B),
                    modifier = Modifier.size(64.dp)
                )

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "Artikel Berhasil Didownload!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF355A84)
                    ),
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text("OK", color = Color.White)
                }
            }
        }
    }
}

/* ───────────────────────────── PREVIEW ──────────────────────────────────── */

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ArticleDetailScreenPreview() {
    val nav = rememberNavController()
    ArticleDetailScreen(
        navController = nav,
        articleId = "1",
        title = "Tips Tidur Nyenyak dan Nyaman",
        category = "Kesehatan Fisik",
        readTime = "5 menit",
        imageRes = R.drawable.ic_launcher_foreground,
        content = """
            Pernahkah Anda bertanya-tanya bagaimana cara mendapatkan tidur berkualitas yang benar-benar menyegarkan?

            Tidur bukan sekadar istirahat, melainkan fondasi penting bagi kesehatan fisik dan mental kita secara menyeluruh.
            Kualitas tidur yang baik meningkatkan konsentrasi, produktivitas, suasana hati, hingga daya ingat sepanjang hari.

            Kurang tidur secara kronis dapat menimbulkan berbagai masalah serius. 
            Oleh karena itu, memahami dan menerapkan kebiasaan tidur yang sehat sangatlah krusial.
        """.trimIndent(),
        isUserArticle = true,
        authorName = "Kamu",
        uploadedDate = "19 Oktober 2025"
    )
}
