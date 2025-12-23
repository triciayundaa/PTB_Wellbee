@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.wellbee.frontend.screens.Edukasi

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.wellbee.R
import com.example.wellbee.data.model.EducationViewModel
import com.example.wellbee.frontend.components.EducationTopBarWithBack
import com.example.wellbee.ui.theme.BluePrimary
import com.example.wellbee.ui.theme.GrayBackground

@Composable
fun CreateArticlePreviewScreen(
    navController: NavHostController,
    viewModel: EducationViewModel,
    category: String,
    readTime: String,
    tag: String,
    title: String,
    content: String,
    imageUri: String? = null,
    articleId: String? = null
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val isLoading = viewModel.isLoading
    val serverError = viewModel.errorMessage

    LaunchedEffect(serverError) {
        serverError?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Long
            )
        }
    }

    fun navigateToMyArticles() {
        navController.navigate("my_articles") {
            popUpTo("education_list") { inclusive = false }
            launchSingleTop = true
        }
    }

    fun handleAksi(status: String) {
        if (articleId == null) {
            viewModel.uploadArticleWithImage(
                imageUri = viewModel.draftImageUri,
                kategori = viewModel.draftCategory.ifBlank { category },
                readTime = if (viewModel.draftReadTime.isNotBlank()) "${viewModel.draftReadTime} menit" else readTime,
                tag = viewModel.draftTag.ifBlank { tag },
                title = viewModel.draftTitle.ifBlank { title },
                content = viewModel.draftContent.ifBlank { content },
                status = status,
                onSuccess = {
                    Toast.makeText(context, "Berhasil menyimpan artikel!", Toast.LENGTH_SHORT).show()
                    navigateToMyArticles()
                }
            )
        } else {
            val id = articleId.toIntOrNull() ?: return
            viewModel.updateMyArticle(
                id = id,
                kategori = viewModel.draftCategory.ifBlank { category },
                readTime = if (viewModel.draftReadTime.isNotBlank()) "${viewModel.draftReadTime} menit" else readTime,
                tag = viewModel.draftTag.ifBlank { tag },
                title = viewModel.draftTitle.ifBlank { title },
                content = viewModel.draftContent.ifBlank { content },
                imageUri = viewModel.draftImageUri,
                onSuccess = {
                    viewModel.changeMyArticleStatus(id, status)
                    Toast.makeText(context, "Berhasil memperbarui artikel!", Toast.LENGTH_SHORT).show()
                    navigateToMyArticles()
                },
                onError = {  }
            )
        }
    }

    Scaffold(
        topBar = {
            EducationTopBarWithBack(
                title = "Preview Artikel",
                onBackClick = { navController.popBackStack() }
            )
        },
        containerColor = GrayBackground,

        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    containerColor = Color(0xFFCC3300),
                    contentColor = Color.White,
                    action = {
                        TextButton(onClick = { data.dismiss() }) {
                            Text("TUTUP", color = Color.White)
                        }
                    }
                ) {
                    Text(data.visuals.message)
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(GrayBackground)
                .verticalScroll(rememberScrollState())
        ) {

            Card(
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(bottom = 24.dp)) {
                    Image(
                        painter = if (viewModel.draftImageUri != null)
                            rememberAsyncImagePainter(viewModel.draftImageUri)
                        else
                            painterResource(R.drawable.ic_launcher_foreground),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Column(Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                        Text(
                            text = "${(viewModel.draftCategory.ifBlank { category }).uppercase()} • ${if (viewModel.draftReadTime.isNotBlank()) "${viewModel.draftReadTime} mnt" else readTime}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BluePrimary,
                            letterSpacing = 1.sp
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = viewModel.draftTitle.ifBlank { title },
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1B3B6B),
                            lineHeight = 32.sp
                        )

                        Spacer(Modifier.height(12.dp))

                        val currentTag = viewModel.draftTag.ifBlank { tag }
                        if (currentTag.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BluePrimary.copy(alpha = 0.1f),
                                border = BorderStroke(1.dp, BluePrimary.copy(alpha = 0.2f))
                            ) {
                                Text(
                                    "#$currentTag",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    color = BluePrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(Modifier.height(20.dp))
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                        Spacer(Modifier.height(20.dp))

                        Text(
                            text = viewModel.draftContent.ifBlank { content },
                            fontSize = 15.sp,
                            color = Color(0xFF444444),
                            lineHeight = 24.sp,
                            textAlign = TextAlign.Justify
                        )
                    }
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BluePrimary)
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = { handleAksi("draft") },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, BluePrimary),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BluePrimary)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Draft", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { handleAksi("uploaded") },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BluePrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Upload", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}