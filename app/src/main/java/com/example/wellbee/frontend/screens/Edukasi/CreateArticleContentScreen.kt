@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.wellbee.frontend.screens.Edukasi

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.wellbee.data.model.EducationViewModel
import com.example.wellbee.frontend.components.EducationTopBarWithBack
import com.example.wellbee.ui.theme.BluePrimary
import com.example.wellbee.ui.theme.GrayBackground

@Composable
fun CreateArticleContentScreen(
    navController: NavHostController,
    viewModel: EducationViewModel,
    category: String,
    readTime: String,
    tag: String,
    articleId: String? = null
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    var titleError by remember { mutableStateOf<String?>(null) }
    var contentError by remember { mutableStateOf<String?>(null) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    /** ================= IMAGE PICKER ================= */
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) selectedImageUri = uri
    }

    /** ================= LOGIKA LOAD DATA (MODE EDIT) ================= */
    val myArticles = viewModel.myArticles

    LaunchedEffect(Unit) {
        if (articleId != null) {
            viewModel.loadMyArticles() // Mengambil daftar artikel saya untuk mencari data lama
        }
    }

    // Sinkronisasi data lama ke state input agar muncul di layar
    LaunchedEffect(myArticles) {
        if (articleId != null) {
            val idInt = articleId.toIntOrNull()
            val article = myArticles.find { it.id == idInt }
            article?.let {
                // Hanya mengisi jika state masih kosong untuk menghindari override saat user mengetik ulang
                if (title.isEmpty()) title = it.judul
                if (content.isEmpty()) content = it.isi
                if (selectedImageUri == null && !it.gambarUrl.isNullOrEmpty()) {
                    selectedImageUri = Uri.parse(it.gambarUrl)
                }
            }
        }
    }

    /** ================= UI ================= */
    Scaffold(
        topBar = {
            EducationTopBarWithBack(
                title = "Education",
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
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {

            Text(
                text = if (articleId == null) "Isi Konten Artikel" else "Edit Konten Artikel",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = BluePrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD9E1F0).copy(alpha = 0.7f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(Modifier.padding(20.dp)) {

                    /** ================= TITLE ================= */
                    Text("Judul Artikel", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF21436B))
                    Spacer(Modifier.height(8.dp))

                    TextField(
                        value = title,
                        onValueChange = {
                            title = it
                            titleError = null
                        },
                        placeholder = { Text("Contoh: Tips Hidup Sehat", color = Color.Gray) },
                        singleLine = true,
                        textStyle = TextStyle(color = Color.Black, fontSize = 14.sp),
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    titleError?.let {
                        Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                    }

                    Spacer(Modifier.height(20.dp))

                    /** ================= IMAGE PICKER ================= */
                    Text("Gambar Sampul", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF21436B))
                    Spacer(Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFB4C3DD))
                            .clickable { galleryLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(selectedImageUri),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            // Overlay ganti gambar
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.BottomEnd
                            ) {
                                Surface(
                                    color = Color.Black.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(8.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Ganti Gambar", color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(6.dp))
                                }
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(Modifier.height(4.dp))
                                Text("Upload Gambar", color = Color.White, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    /** ================= CONTENT ================= */
                    Text("Isi Artikel", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF21436B))
                    Spacer(Modifier.height(8.dp))

                    TextField(
                        value = content,
                        onValueChange = {
                            content = it
                            contentError = null
                        },
                        placeholder = { Text("Tuliskan detail informasi artikel di sini...", color = Color.Gray) },
                        minLines = 8,
                        textStyle = TextStyle(color = Color.Black, fontSize = 14.sp),
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    contentError?.let {
                        Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            /** ================= BUTTON PREVIEW ================= */
            Button(
                onClick = {
                    var valid = true
                    if (title.isBlank()) { titleError = "Judul tidak boleh kosong"; valid = false }
                    if (content.isBlank()) { contentError = "Isi artikel tidak boleh kosong"; valid = false }

                    if (valid) {
                        val baseRoute = "create_article_preview/" +
                                "${Uri.encode(category)}/" +
                                "${Uri.encode(readTime)}/" +
                                "${Uri.encode(tag)}/" +
                                "${Uri.encode(title)}/" +
                                "${Uri.encode(content)}"

                        val imageArg = Uri.encode(selectedImageUri?.toString() ?: "")
                        val fullRoute = if (articleId != null) {
                            "$baseRoute?imageUri=$imageArg&articleId=${Uri.encode(articleId)}"
                        } else {
                            "$baseRoute?imageUri=$imageArg"
                        }
                        navController.navigate(fullRoute)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BluePrimary,
                    contentColor = Color.White
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Lihat Preview", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
