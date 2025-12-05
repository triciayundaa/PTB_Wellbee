package com.example.wellbee.frontend.screens.Edukasi

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.wellbee.data.model.EducationViewModel
import com.example.wellbee.frontend.components.EducationTopBarWithBack
import com.example.wellbee.ui.theme.BluePrimary
import com.example.wellbee.ui.theme.GrayBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateArticleMetaScreen(
    navController: NavHostController,
    articleId: String? = null    // null = buat baru, ada value = edit
) {
    val context = LocalContext.current
    val viewModel = remember { EducationViewModel(context) }

    var category by remember { mutableStateOf("") }
    var readTime by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("") }

    var categoryError by remember { mutableStateOf<String?>(null) }
    var readTimeError by remember { mutableStateOf<String?>(null) }
    var tagError by remember { mutableStateOf<String?>(null) }

    // 🔹 Ambil kategori dari backend saat pertama kali masuk screen
    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }

    // 🔹 Prefill kalau edit
    LaunchedEffect(articleId) {
        if (articleId != null) {
            val article = MyArticleRepository.findById(articleId)
            if (article != null) {
                category = article.category
                readTime = article.readTime
                tag = article.tag
            }
        }
    }

    // 🔹 List kategori: kalau backend kosong, pakai fallback lokal
    val backendCategories = viewModel.categories
    val categoryOptions = if (backendCategories.isNotEmpty()) {
        backendCategories
    } else {
        listOf(
            "Kesehatan Mental",
            "Kesehatan Fisik",
            "Tips Sehat",
            "Nutrisi",
            "Keseimbangan Hidup",
            "Produktivitas"
        )
    }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }

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
                text = if (articleId == null) "Buat Artikel" else "Edit Artikel",
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
                        .padding(20.dp)
                ) {
                    // ================== KATEGORI ARTIKEL (DROPDOWN) ==================
                    Text(
                        text = "Kategori Artikel",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(6.dp))

                    ExposedDropdownMenuBox(
                        expanded = categoryDropdownExpanded,
                        onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
                    ) {
                        TextField(
                            value = category,
                            onValueChange = { /* readOnly, jadi kosongkan */ },
                            readOnly = true,
                            placeholder = { Text("Pilih kategori") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = categoryDropdownExpanded
                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = categoryDropdownExpanded,
                            onDismissRequest = { categoryDropdownExpanded = false }
                        ) {
                            categoryOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        category = option
                                        categoryError = null
                                        categoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    if (categoryError != null) {
                        Text(
                            text = categoryError!!,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // ================== ESTIMASI WAKTU BACA ==================
                    Text(
                        text = "Estimasi waktu baca",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    TextField(
                        value = readTime,
                        onValueChange = {
                            readTime = it
                            readTimeError = null
                        },
                        singleLine = true,
                        placeholder = { Text("5 menit") },
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (readTimeError != null) {
                        Text(
                            text = readTimeError!!,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // ================== TAGAR ==================
                    Text(
                        text = "Tagar",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    TextField(
                        value = tag,
                        onValueChange = {
                            tag = it
                            tagError = null
                        },
                        singleLine = true,
                        placeholder = { Text("Produktif") },
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (tagError != null) {
                        Text(
                            text = tagError!!,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // CHIP TAGAR DI BAWAH
                    if (tag.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = Color(0xFFB2C3E0),
                            modifier = Modifier.align(Alignment.Start)
                        ) {
                            Text(
                                text = tag,
                                modifier = Modifier.padding(
                                    horizontal = 24.dp,
                                    vertical = 8.dp
                                ),
                                color = Color(0xFF21436B),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ================== TOMBOL LANJUTKAN ==================
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        var valid = true
                        if (category.isBlank()) {
                            categoryError = "Kategori harus diisi"
                            valid = false
                        }
                        if (readTime.isBlank()) {
                            readTimeError = "Estimasi waktu baca harus diisi"
                            valid = false
                        }
                        if (tag.isBlank()) {
                            tagError = "Tagar harus diisi"
                            valid = false
                        }

                        if (valid) {
                            val catArg = Uri.encode(category.trim())
                            val readArg = Uri.encode(readTime.trim())
                            val tagArg = Uri.encode(tag.trim())

                            val baseRoute =
                                "create_article_content/$catArg/$readArg/$tagArg"

                            val fullRoute = if (articleId != null) {
                                baseRoute + "?articleId=" + Uri.encode(articleId)
                            } else {
                                baseRoute
                            }

                            navController.navigate(fullRoute)
                        }
                    }
                ) {
                    Text("Lanjutkan", color = BluePrimary)
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        Icons.Default.ArrowForwardIos,
                        contentDescription = "Lanjutkan",
                        tint = BluePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CreateArticleMetaScreenPreview() {
    val nav = rememberNavController()
    CreateArticleMetaScreen(navController = nav)
}
