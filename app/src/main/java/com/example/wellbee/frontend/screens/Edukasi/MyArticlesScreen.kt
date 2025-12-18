@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.wellbee.frontend.education

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.wellbee.data.model.EducationViewModel
import com.example.wellbee.data.model.MyArticleDto
import com.example.wellbee.frontend.components.TagChip
import com.example.wellbee.ui.theme.BluePrimary
import com.example.wellbee.ui.theme.GrayBackground

@Composable
fun MyArticlesScreen(
    navController: NavHostController? = null ,
    viewModel: EducationViewModel,
) {
    val context = LocalContext.current

    val myArticles = viewModel.myArticles
    val isLoading = viewModel.isLoadingMyArticles
    val error = viewModel.myArticleError

    var showConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var articlePendingDelete by remember { mutableStateOf<MyArticleDto?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadMyArticles()
    }

    Scaffold(
        containerColor = GrayBackground,
        topBar = { MyArticlesTopBar(navController) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BluePrimary)
                    }
                }
                error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = error, color = Color.Red)
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(myArticles, key = { it.id }) { article ->
                            MyArticleCard(
                                article = article,
                                onStatusActionClick = { action ->
                                    val newStatus = when (action) {
                                        MyArticleAction.UPLOAD,
                                        MyArticleAction.REUPLOAD -> "uploaded"
                                        MyArticleAction.CANCEL_UPLOAD -> "canceled"
                                    }
                                    viewModel.changeMyArticleStatus(article.id, newStatus)
                                },
                                onOpenDetail = {
                                    navController?.navigate("article_detail/${article.id}?source=my")
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
            }
        }
    }

    if (showConfirmDialog && articlePendingDelete != null) {
        ConfirmDeleteDialogLocal(
            onCancel = {
                showConfirmDialog = false
                articlePendingDelete = null
            },
            onConfirm = {
                articlePendingDelete?.let { viewModel.deleteMyArticle(it.id) }
                articlePendingDelete = null
                showConfirmDialog = false
                showSuccessDialog = true
            }
        )
    }

    if (showSuccessDialog) {
        DeleteSuccessDialogLocal(onDismiss = { showSuccessDialog = false })
    }
}

@Composable
private fun MyArticlesTopBar(navController: NavHostController?) {
    Surface(
        color = BluePrimary,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(bottom = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController?.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, "Kembali", tint = Color.White)
                }
                Text(
                    text = "Education",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Artikel Saya",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
    }
}

enum class MyArticleAction { UPLOAD, CANCEL_UPLOAD, REUPLOAD }

private data class ArticleStatusUI(
    val statusText: String,
    val actionLabel: String,
    val actionType: MyArticleAction?,
    val statusColor: Color
)

@Composable
private fun MyArticleCard(
    article: MyArticleDto,
    onStatusActionClick: (MyArticleAction) -> Unit,
    onOpenDetail: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val statusUI = when (article.status) {
        "draft" -> ArticleStatusUI("Draft", "Upload Sekarang", MyArticleAction.UPLOAD, Color(0xFFFFA000))
        "uploaded" -> ArticleStatusUI("Diupload", "Batalkan upload", MyArticleAction.CANCEL_UPLOAD, Color(0xFF4CAF50))
        "canceled" -> ArticleStatusUI("Dibatalkan", "Upload ulang", MyArticleAction.REUPLOAD, Color(0xFFE53935))
        else -> ArticleStatusUI("Unknown", "", null, Color.Gray)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenDetail() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = article.judul,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF2D3142)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${article.kategori ?: "Umum"} • ${article.waktuBaca ?: "-"}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Row {
                    Surface(
                        modifier = Modifier.size(36.dp).clickable { onEditClick() },
                        shape = CircleShape,
                        color = Color(0xFFF0F4F8)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color(0xFF355A84),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        modifier = Modifier.size(36.dp).clickable { onDeleteClick() },
                        shape = CircleShape,
                        color = Color(0xFFFFEBEE)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TagChip(text = article.tag ?: "Tips", selected = false, onClick = {})

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(statusUI.statusColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = statusUI.statusText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusUI.statusColor
                )

                if (statusUI.actionType != null && statusUI.actionLabel.isNotBlank()) {
                    Text(
                        text = statusUI.actionLabel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BluePrimary,
                        modifier = Modifier.clickable { onStatusActionClick(statusUI.actionType) }
                    )
                }
            }
        }
    }
}

/* ─── PERBAIKAN DIALOG DENGAN TEKS PUTIH (LOKAL) ─── */

@Composable
fun ConfirmDeleteDialogLocal(
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onCancel) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF7EA4C9)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Hapus Artikel?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Apakah Anda yakin ingin menghapus artikel ini?",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, Color.White),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Text("Batal")
                    }
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF355A84),
                            contentColor = Color.White // Menjamin warna teks icon/konten putih
                        )
                    ) {
                        Text(
                            text = "Iya, Hapus",
                            color = Color.White, // Penegasan paksa warna putih
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteSuccessDialogLocal(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF7EA4C9)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "Berhasil Dihapus!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF355A84),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("OK", color = Color.White)
                }
            }
        }
    }
}
