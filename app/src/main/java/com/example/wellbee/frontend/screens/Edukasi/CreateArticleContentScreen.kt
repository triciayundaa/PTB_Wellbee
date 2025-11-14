package com.example.wellbee.frontend.screens.Edukasi

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
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
import com.example.wellbee.frontend.components.EducationTopBarWithBack
import com.example.wellbee.ui.theme.BluePrimary
import com.example.wellbee.ui.theme.GrayBackground

@Composable
fun CreateArticleContentScreen(
    navController: NavHostController,
    category: String,
    readTime: String,
    tag: String,
    articleId: String? = null
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    var titleError by remember { mutableStateOf<String?>(null) }
    var contentError by remember { mutableStateOf<String?>(null) }

    // 🔹 URI gambar yang dipilih user (belum disimpan ke repo, hanya UI)
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    // Prefill kalau edit
    LaunchedEffect(articleId) {
        if (articleId != null) {
            val article = MyArticleRepository.findById(articleId)
            if (article != null) {
                title = article.title
                content = article.content
            }
        }
    }

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
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {

            Text(
                text = "Buat Artikel",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = BluePrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFD9E1F0)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {

                    // JUDUL
                    Text(
                        text = "Judul Artikel",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = BluePrimary
                    )
                    Spacer(Modifier.height(6.dp))
                    TextField(
                        value = title,
                        onValueChange = {
                            title = it
                            titleError = null
                        },
                        placeholder = { Text("Tips Tidur Nyenyak") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (titleError != null) {
                        Text(
                            text = titleError!!,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // LABEL CONTENT
                    Text(
                        text = "Content",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = BluePrimary
                    )

                    Spacer(Modifier.height(8.dp))

                    // AREA KAMERA / GAMBAR
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .background(Color(0xFFB4C3DD), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(onClick = {
                                galleryLauncher.launch("image/*")
                            }) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Tambah foto",
                                    tint = Color.White,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            Text(
                                text = if (selectedImageUri != null)
                                    "Gambar sudah dipilih"
                                else
                                    "Tambah Gambar",
                                color = Color.White
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // ISI ARTIKEL
                    TextField(
                        value = content,
                        onValueChange = {
                            content = it
                            contentError = null
                        },
                        placeholder = { Text("Tulis konten artikel di sini...") },
                        minLines = 6,
                        maxLines = 12,
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 200.dp)
                    )
                    if (contentError != null) {
                        Text(
                            text = contentError!!,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // TOMBOL PREVIEW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        var valid = true
                        if (title.isBlank()) {
                            titleError = "Judul tidak boleh kosong"
                            valid = false
                        }
                        if (content.isBlank()) {
                            contentError = "Isi artikel tidak boleh kosong"
                            valid = false
                        }

                        if (valid) {
                            val catArg = Uri.encode(category)
                            val readArg = Uri.encode(readTime)
                            val tagArg = Uri.encode(tag)
                            val titleArg = Uri.encode(title)
                            val contentArg = Uri.encode(content)

                            val baseRoute =
                                "create_article_preview/$catArg/$readArg/$tagArg/$titleArg/$contentArg"

                            val fullRoute = if (articleId != null) {
                                baseRoute + "?articleId=" + Uri.encode(articleId)
                            } else {
                                baseRoute
                            }

                            navController.navigate(fullRoute)
                        }
                    }
                ) {
                    Text("Preview", color = BluePrimary, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CreateArticleContentScreenPreview() {
    val nav = rememberNavController()
    CreateArticleContentScreen(
        navController = nav,
        category = "Kesehatan Fisik",
        readTime = "5 menit",
        tag = "Produktif"
    )
}
