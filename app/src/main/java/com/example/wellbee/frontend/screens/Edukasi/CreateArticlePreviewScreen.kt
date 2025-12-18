@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.wellbee.frontend.screens.Edukasi

import android.net.Uri
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
    val imageUriParsed = remember(imageUri) {
        imageUri?.takeIf { it.isNotBlank() }?.let { Uri.parse(Uri.decode(it)) }
    }

    var infoMessage by remember { mutableStateOf<String?>(null) }
    val isLoading = viewModel.isLoading // 🔹 Mengambil state loading dari ViewModel

    // Fungsi navigasi yang membersihkan stack agar data terbaru muncul paling atas
    fun navigateToMyArticles() {
        navController.navigate("my_articles") {
            // Membersihkan history navigasi pembuatan artikel agar tidak kembali ke Preview saat tekan back
            popUpTo("education_list") { inclusive = false }
            launchSingleTop = true
        }
    }

    // Fungsi Gabungan Aksi (Upload atau Update)
    fun handleAksi(status: String) {
        if (articleId == null) {
            // MODE BUAT BARU
            viewModel.uploadArticleWithImage(
                imageUri = imageUriParsed,
                kategori = category,
                readTime = readTime,
                tag = tag,
                title = title,
                content = content,
                status = status,
                onSuccess = {
                    // 🔹 Navigasi HANYA dilakukan setelah data berhasil di-refresh di ViewModel
                    navigateToMyArticles()
                }
            )
        } else {
            // MODE EDIT
            val id = articleId.toIntOrNull() ?: return
            viewModel.updateMyArticle(
                id = id,
                kategori = category,
                readTime = readTime,
                tag = tag,
                title = title,
                content = content,
                imageUri = imageUriParsed,
                onSuccess = {
                    // Pastikan status diperbarui sesuai pilihan user (Draft atau Uploaded)
                    viewModel.changeMyArticleStatus(id, status)
                    navigateToMyArticles()
                },
                onError = { error ->
                    infoMessage = error
                }
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
        containerColor = GrayBackground
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(GrayBackground)
                .verticalScroll(rememberScrollState())
        ) {

            // --- BAGIAN KONTEN ---
            Card(
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(bottom = 24.dp)) {
                    // IMAGE
                    Image(
                        painter = if (imageUriParsed != null)
                            rememberAsyncImagePainter(imageUriParsed)
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
                            text = "${category.uppercase()} • $readTime",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BluePrimary,
                            letterSpacing = 1.sp
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1B3B6B),
                            lineHeight = 32.sp
                        )

                        Spacer(Modifier.height(12.dp))

                        if (tag.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BluePrimary.copy(alpha = 0.1f),
                                border = BorderStroke(1.dp, BluePrimary.copy(alpha = 0.2f))
                            ) {
                                Text(
                                    "#$tag",
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
                            text = content,
                            fontSize = 15.sp,
                            color = Color(0xFF444444),
                            lineHeight = 24.sp,
                            textAlign = TextAlign.Justify
                        )
                    }
                }
            }

            // --- TOMBOL AKSI ---
            if (isLoading) {
                // Tampilkan indikator loading agar user tidak menekan tombol berkali-kali
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
                    // TOMBOL SIMPAN DRAFT
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

                    // TOMBOL UPLOAD
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

    // Snackbar untuk Error/Info
    infoMessage?.let {
        Snackbar(
            modifier = Modifier.padding(16.dp),
            containerColor = Color(0xFF323232),
            action = {
                TextButton(onClick = { infoMessage = null }) {
                    Text("Tutup", color = Color.Yellow)
                }
            }
        ) { Text(it, color = Color.White) }
    }
}