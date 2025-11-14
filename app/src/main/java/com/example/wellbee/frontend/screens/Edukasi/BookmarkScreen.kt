@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.wellbee.frontend.screens.Edukasi

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.wellbee.frontend.components.BookmarkArticleCard
import com.example.wellbee.frontend.components.SearchBar
import com.example.wellbee.frontend.components.TagChip
import com.example.wellbee.ui.theme.BluePrimary
import com.example.wellbee.ui.theme.GrayBackground

@Composable
fun BookmarkScreen(navController: NavHostController? = null) {

    // 🔹 Ambil list bookmark global dari BookmarkManager
    val bookmarks = BookmarkManager.bookmarks

    // 🔹 Join bookmark + artikel asli dari EducationArticles
    val bookmarkArticles = bookmarks.mapNotNull { bookmark ->
        val article = EducationArticles.articles.find { it.id == bookmark.articleId }
        if (article != null) bookmark to article else null
    }

    // ─── STATE FILTER ───────────────────────────────────────────────────────
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    // ─── STATE DIALOG ───────────────────────────────────────────────────────
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var articlePendingDelete by remember { mutableStateOf<BookmarkItem?>(null) }

    // ─── FILTER LIST ARTIKEL ───────────────────────────────────────────────
    val filteredArticles = bookmarkArticles.filter { (bookmark, article) ->
        val matchCategory = selectedCategory?.let { cat ->
            article.categories.any { it.equals(cat, ignoreCase = true) }
        } ?: true

        val matchQuery =
            searchQuery.isBlank() || article.title.contains(searchQuery, ignoreCase = true)

        matchCategory && matchQuery
    }

    // Hitung jumlah belum dibaca
    val unreadCount = bookmarks.count { !it.isRead }

    Scaffold(
        containerColor = GrayBackground,
        topBar = { BookmarkTopBar(navController) }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // SECTION: chip + search + reminder
            item {
                BookmarkFilterSection(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { cat ->
                        selectedCategory = if (selectedCategory == cat) null else cat
                    },
                    onSearch = { query -> searchQuery = query },
                    onMyArticlesClick = { /* TODO: navigate ke Artikel Saya */ },
                    unreadCount = unreadCount
                )
                Spacer(Modifier.height(8.dp))
            }

            // SECTION: list artikel tersimpan
            items(
                items = filteredArticles,
                key = { (bookmark, _) -> bookmark.articleId }
            ) { (bookmark, article) ->
                BookmarkArticleCard(
                    imageRes = article.imageRes,
                    categories = article.categories,
                    title = article.title,
                    readTime = article.readTime,
                    isRead = bookmark.isRead,
                    onDeleteClick = {
                        // ketika tombol hapus di card diklik
                        articlePendingDelete = bookmark
                        showConfirmDialog = true
                    },
                    onReadMoreClick = {
                        navController?.navigate("article_detail/${article.id}")
                    }
                )
            }
        }

        // ─── DIALOG KONFIRMASI HAPUS ────────────────────────────────────────
        if (showConfirmDialog && articlePendingDelete != null) {
            ConfirmDeleteDialog(
                onCancel = {
                    showConfirmDialog = false
                    articlePendingDelete = null
                },
                onConfirm = {
                    articlePendingDelete?.let { pending ->
                        BookmarkManager.bookmarks.remove(pending)
                    }
                    articlePendingDelete = null
                    showConfirmDialog = false
                    showSuccessDialog = true
                }
            )
        }

        // ─── DIALOG BERHASIL DIHAPUS ────────────────────────────────────────
        if (showSuccessDialog) {
            DeleteSuccessDialog(
                onDismiss = { showSuccessDialog = false }
            )
        }
    }
}

/* ───────────────────────────── TOP BAR (HEADER BIRU) ───────────────────── */

@Composable
private fun BookmarkTopBar(navController: NavHostController?) {
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
            text = "Artikel Tersimpan",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

/* ───────────────────────────── FILTER SECTION ───────────────────────────── */

@Composable
private fun BookmarkFilterSection(
    selectedCategory: String?,
    onCategorySelected: (String) -> Unit,
    onSearch: (String) -> Unit,
    onMyArticlesClick: () -> Unit,
    unreadCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        CategoryChipsRow(
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected
        )

        Spacer(Modifier.height(16.dp))

        SearchBar(
            hint = "Cari di favorit",
            onSearch = onSearch,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        ReminderCard(
            unreadCount = unreadCount,
            onMyArticlesClick = onMyArticlesClick
        )
    }
}

@Composable
private fun CategoryChipsRow(
    selectedCategory: String?,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf("Kesehatan Mental", "Kesehatan Fisik", "Tips Sehat")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        categories.forEach { category ->
            TagChip(
                text = category,
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) }
            )
        }
    }
}

/* ───────────────────────────── REMINDER CARD ───────────────────────────── */

@Composable
private fun ReminderCard(
    unreadCount: Int,
    onMyArticlesClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFDDE6F7)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Reminder",
                tint = BluePrimary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Reminder",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1B3B6B)
                )
                Text(
                    text = "Kamu punya $unreadCount artikel yang belum dibaca",
                    fontSize = 12.sp,
                    color = Color(0xFF4A4A4A)
                )
            }
        }
    }
}

/* ───────────────────────────── DIALOGS ─────────────────────────────────── */

@Composable
fun ConfirmDeleteDialog(
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onCancel) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF7EA4C9)
            ),
            modifier = Modifier
                .fillMaxWidth(0.85f)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Apakah Anda Ingin\nMenghapus Artikel Ini?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onCancel,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF355A84)
                        ),
                        shape = RoundedCornerShape(999.dp),
                        modifier = Modifier.width(100.dp)
                    ) {
                        Text("Batal", color = Color.White)
                    }

                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF355A84)
                        ),
                        shape = RoundedCornerShape(999.dp),
                        modifier = Modifier.width(100.dp)
                    ) {
                        Text("Iya", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteSuccessDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF7EA4C9)
            ),
            modifier = Modifier
                .fillMaxWidth(0.85f)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFF355A84),
                    modifier = Modifier.size(48.dp)
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Artikel Berhasil Dihapus!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Spacer(Modifier.height(16.dp))

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

/* ───────────────────────────── PREVIEW ───────────────────────────── */

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BookmarkScreenPreview() {
    val navController = rememberNavController()
    BookmarkScreen(navController = navController)
}
