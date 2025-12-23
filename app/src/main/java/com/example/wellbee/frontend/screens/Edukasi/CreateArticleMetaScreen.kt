@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.wellbee.frontend.screens.Edukasi

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.wellbee.data.model.EducationViewModel
import com.example.wellbee.frontend.components.EducationTopBarWithBack
import com.example.wellbee.ui.theme.BluePrimary
import com.example.wellbee.ui.theme.GrayBackground

@Composable
fun CreateArticleMetaScreen(
    navController: NavHostController,
    viewModel: EducationViewModel,
    articleId: String? = null
) {
    var categoryError by remember { mutableStateOf<String?>(null) }
    var readTimeError by remember { mutableStateOf<String?>(null) }
    var tagError by remember { mutableStateOf<String?>(null) }

    var expanded by remember { mutableStateOf(false) }

    // List kategori dari ViewModel, dengan fallback jika kosong
    val categoryOptions = if (viewModel.categories.isNotEmpty()) viewModel.categories else listOf(
        "Kesehatan Mental", "Kesehatan Fisik", "Tips Sehat", "Nutrisi", "Produktivitas"
    )

    val myArticles = viewModel.myArticles

    LaunchedEffect(Unit) {
        viewModel.loadCategories()
        if (articleId != null) {
            viewModel.loadMyArticles()
        }
    }

    LaunchedEffect(myArticles, articleId) {
        if (articleId != null) {
            val idInt = articleId.toIntOrNull()
            val article = myArticles.find { it.id == idInt }
            article?.let {

                if (viewModel.draftCategory.isEmpty()) viewModel.draftCategory = it.kategori ?: ""
                if (viewModel.draftTag.isEmpty()) viewModel.draftTag = it.tag ?: ""
                if (viewModel.draftReadTime.isEmpty()) {
                    viewModel.draftReadTime = it.waktuBaca?.replace(Regex("[^0-9]"), "") ?: ""
                }
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
                .padding(16.dp)
        ) {
            Text(
                text = if (articleId == null) "Informasi Artikel" else "Edit Informasi Artikel",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = BluePrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD9E1F0).copy(alpha = 0.7f))
            ) {
                Column(Modifier.padding(20.dp)) {

                    Text("Kategori Artikel", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF21436B))
                    Spacer(Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        TextField(

                            value = viewModel.draftCategory,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Pilih kategori", color = Color.Gray) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
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
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            categoryOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(text = option, color = Color.Black) },
                                    onClick = {

                                        viewModel.draftCategory = option
                                        expanded = false
                                        categoryError = null
                                    }
                                )
                            }
                        }
                    }
                    categoryError?.let { Text(it, color = Color.Red, fontSize = 12.sp) }

                    Spacer(Modifier.height(16.dp))

                    Text("Estimasi Waktu Baca", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF21436B))
                    Spacer(Modifier.height(8.dp))
                    TextField(

                        value = viewModel.draftReadTime,
                        onValueChange = { input ->
                            val digitsOnly = input.filter { it.isDigit() }

                            viewModel.draftReadTime = digitsOnly
                            readTimeError = null
                        },
                        placeholder = { Text("0", color = Color.Gray) },
                        suffix = {
                            Text("menit", color = Color.Gray, fontSize = 14.sp)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                    readTimeError?.let { Text(it, color = Color.Red, fontSize = 12.sp) }

                    Spacer(Modifier.height(16.dp))


                    Text("Tag Artikel", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF21436B))
                    Spacer(Modifier.height(8.dp))
                    TextField(

                        value = viewModel.draftTag,
                        onValueChange = {

                            viewModel.draftTag = it
                            tagError = null
                        },
                        placeholder = { Text("Contoh: Nutrisi", color = Color.Gray) },
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
                    tagError?.let { Text(it, color = Color.Red, fontSize = 12.sp) }
                }
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    var valid = true

                    if (viewModel.draftCategory.isBlank()) { categoryError = "Kategori harus diisi"; valid = false }
                    if (viewModel.draftReadTime.isBlank()) { readTimeError = "Waktu baca harus diisi"; valid = false }
                    if (viewModel.draftTag.isBlank()) { tagError = "Tag harus diisi"; valid = false }

                    if (valid) {

                        val route = if (articleId != null) {
                            "create_article_content?articleId=$articleId"
                        } else {
                            "create_article_content"
                        }
                        navController.navigate(route)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Lanjutkan", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
}