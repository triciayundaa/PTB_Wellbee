@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.wellbee.frontend.screens.Edukasi

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.wellbee.data.model.BookmarkDto
import com.example.wellbee.data.model.EducationViewModel
import com.example.wellbee.frontend.components.BookmarkArticleCard
import com.example.wellbee.ui.theme.BluePrimary
import com.example.wellbee.ui.theme.GrayBackground
import kotlinx.coroutines.launch

@Composable
fun BookmarkScreen(
    navController: NavHostController? = null,
    viewModel: EducationViewModel,
) {
    val context = LocalContext.current
    val bookmarks = viewModel.bookmarks
    val isLoading = viewModel.isLoadingBookmarks
    val error = viewModel.bookmarkError

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadBookmarks()
    }

    LaunchedEffect(error) {
        if (!error.isNullOrBlank()) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = error!!,
                    withDismissAction = true,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    var showConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var bookmarkPendingDelete by remember { mutableStateOf<BookmarkDto?>(null) }

    val unreadCount = remember(bookmarks) { bookmarks.count { it.sudahDibaca == 0 } }

    Scaffold(
        containerColor = GrayBackground,
        topBar = { BookmarkTopBar(navController) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Spacer(Modifier.height(12.dp))

            ReminderCard(
                unreadCount = unreadCount
            )

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = BluePrimary,
                            strokeWidth = 4.dp
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Memuat data...",
                            color = BluePrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else if (bookmarks.isEmpty()) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada artikel yang disimpan.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = bookmarks,
                        key = { it.bookmarkId }
                    ) { item ->
                        BookmarkArticleCard(
                            imageUrl = item.gambarUrl,
                            categories = listOfNotNull(item.kategori),
                            title = item.judul,
                            readTime = item.waktuBaca ?: "-",
                            isRead = item.sudahDibaca == 1,
                            onDeleteClick = {
                                bookmarkPendingDelete = item
                                showConfirmDialog = true
                            },
                            onReadMoreClick = {
                                viewModel.markBookmarkAsRead(item.bookmarkId)
                                navController?.navigate("article_detail/${item.artikelId}?source=public")
                            }
                        )
                    }
                }
            }
        }

        if (showConfirmDialog && bookmarkPendingDelete != null) {
            ConfirmDeleteDialog(
                onCancel = {
                    showConfirmDialog = false
                    bookmarkPendingDelete = null
                },
                onConfirm = {
                    bookmarkPendingDelete?.let { pending ->
                        viewModel.deleteBookmark(pending.bookmarkId)
                    }
                    bookmarkPendingDelete = null
                    showConfirmDialog = false
                    showSuccessDialog = true
                }
            )
        }

        if (showSuccessDialog) {
            DeleteSuccessDialog(onDismiss = { showSuccessDialog = false })
        }
    }
}

@Composable
private fun BookmarkTopBar(navController: NavHostController?) {
    Surface(
        color = BluePrimary,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(bottom = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { navController?.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, "Kembali", tint = Color.White)
                }

                Text(
                    text = "Education",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Artikel Tersimpan",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}

@Composable
private fun ReminderCard(unreadCount: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3EDFF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.5f)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = BluePrimary,
                    modifier = Modifier.padding(8.dp).size(24.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Reminder", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1B3B6B))
                Text("Kamu punya $unreadCount artikel yang belum dibaca", fontSize = 13.sp, color = Color(0xFF4A4A4A))
            }
        }
    }
}

@Composable
fun ConfirmDeleteDialog(onCancel: () -> Unit, onConfirm: () -> Unit) {
    Dialog(onDismissRequest = onCancel) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF7EA4C9)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Hapus Artikel?", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.height(8.dp))
                Text("Yakin ingin menghapus artikel dari daftar simpan?", fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f), textAlign = TextAlign.Center)
                Spacer(Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, Color.White),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Text("Batal", color = Color.White)
                    }
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF355A84))
                    ) {
                        Text("Iya, Hapus", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteSuccessDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF7EA4C9)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(16.dp))
                Text("Berhasil Dihapus!", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF355A84)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("OK", color = Color.White)
                }
            }
        }
    }
}