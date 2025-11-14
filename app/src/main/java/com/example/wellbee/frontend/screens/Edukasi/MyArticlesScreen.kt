package com.example.wellbee.frontend.screens.Edukasi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.example.wellbee.frontend.components.SearchBar
import com.example.wellbee.frontend.components.TagChip
import com.example.wellbee.ui.theme.BluePrimary
import com.example.wellbee.ui.theme.GrayBackground

@Composable
fun MyArticlesScreen(navController: NavHostController? = null) {

    // ✅ pakai data dari MyArticleRepository
    val myArticles = MyArticleRepository.articles

    var searchQuery by remember { mutableStateOf("") }

    // dialog state
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var articlePendingDelete by remember { mutableStateOf<MyArticle?>(null) }

    // filter berdasarkan pencarian
    val filteredArticles = myArticles.filter { article ->
        searchQuery.isBlank() ||
                article.title.contains(searchQuery, ignoreCase = true) ||
                article.category.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        containerColor = GrayBackground,
        topBar = { MyArticlesTopBar(navController) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(GrayBackground)
        ) {

            // 🔍 Search
            SearchBar(
                hint = "Cari di artikel saya",
                onSearch = { query -> searchQuery = query },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // 📚 List artikel
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredArticles, key = { it.id }) { article ->

                    MyArticleCard(
                        article = article,
                        onStatusActionClick = { action ->
                            val newStatus = when (action) {
                                MyArticleAction.UPLOAD ->
                                    MyArticleStatus.UPLOADED
                                MyArticleAction.CANCEL_UPLOAD ->
                                    MyArticleStatus.CANCELED
                                MyArticleAction.REUPLOAD ->
                                    MyArticleStatus.UPLOADED
                            }
                            MyArticleRepository.updateStatus(article.id, newStatus)
                        },
                        onEditClick = {
                            navController?.navigate("create_article_meta?articleId=${article.id}")
                        },
                        onDeleteClick = {
                            articlePendingDelete = article
                            showConfirmDialog = true
                        }
                    )
                }
            }
        }

        // Dialog konfirmasi hapus
        if (showConfirmDialog && articlePendingDelete != null) {
            ConfirmDeleteDialog(
                onCancel = {
                    showConfirmDialog = false
                    articlePendingDelete = null
                },
                onConfirm = {
                    articlePendingDelete?.let { toDelete ->
                        MyArticleRepository.delete(toDelete.id)
                    }
                    articlePendingDelete = null
                    showConfirmDialog = false
                    showSuccessDialog = true
                }
            )
        }

        // Dialog berhasil dihapus
        if (showSuccessDialog) {
            DeleteSuccessDialog(
                onDismiss = { showSuccessDialog = false }
            )
        }
    }
}

/* ───────────────────────────── TOP BAR ───────────────────────────── */

@Composable
private fun MyArticlesTopBar(navController: NavHostController?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BluePrimary)
            .statusBarsPadding()
            .padding(bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController?.popBackStack() }) {
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
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Artikel Saya",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

/* ───────────────────────────── CARD ARTIKEL ───────────────────────────── */

enum class MyArticleAction {
    UPLOAD,         // dari Draft → Uploaded
    CANCEL_UPLOAD,  // dari Uploaded → Canceled
    REUPLOAD        // dari Canceled → Uploaded
}

@Composable
private fun MyArticleCard(
    article: MyArticle,
    onStatusActionClick: (MyArticleAction) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    // mapping status → teks & aksi
    val (statusText, actionLabel, actionType) = when (article.status) {
        MyArticleStatus.DRAFT -> Triple(
            "Draft",
            "Upload",
            MyArticleAction.UPLOAD
        )
        MyArticleStatus.UPLOADED -> Triple(
            "Diupload",
            "Batalkan upload",
            MyArticleAction.CANCEL_UPLOAD
        )
        MyArticleStatus.CANCELED -> Triple(
            "Upload dibatalkan",
            "Upload ulang",
            MyArticleAction.REUPLOAD
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Judul + ikon edit/delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = article.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color(0xFF1F2933)
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "${article.category} • ${article.readTime}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = BluePrimary
                        )
                    }

                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = Color(0xFFD32F2F)
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Tag
            TagChip(
                text = article.tag,
                selected = false,
                onClick = {}
            )

            Spacer(Modifier.height(8.dp))

            // Status + tombol aksi (upload / batalkan / upload ulang)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = statusText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFD32F2F)
                )

                TextButton(
                    onClick = { onStatusActionClick(actionType) }
                ) {
                    Text(
                        text = actionLabel,
                        fontSize = 12.sp,
                        color = BluePrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/* ───────────────────────────── PREVIEW ───────────────────────────── */

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MyArticlesScreenPreview() {
    val nav = rememberNavController()
    MyArticlesScreen(navController = nav)
}
