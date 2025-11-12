package com.example.wellbee.frontend.screens.Edukasi

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.wellbee.R
import com.example.wellbee.frontend.components.EducationTopBarWithBack
import com.example.wellbee.ui.theme.BluePrimary
import com.example.wellbee.ui.theme.GrayBackground

@Composable
fun CreateArticlePreviewScreen(
    navController: NavHostController,
    category: String,
    readTime: String,
    tag: String,
    title: String,
    content: String,
    articleId: String? = null   // 🔹 diturunkan dari Content (opsional)
) {
    val showInfo = remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.background(BluePrimary)
            ) {
                EducationTopBarWithBack(
                    title = "Education",
                    onBackClick = { navController.popBackStack() }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Preview",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        // 🔹 SIMPAN SEBAGAI DRAFT → lanjut ke MyArticlesScreen
                        TextButton(
                            onClick = {
                                MyArticleRepository.upsertFromPreview(
                                    category = category,
                                    readTime = readTime,
                                    tag = tag,
                                    title = title,
                                    content = content,
                                    status = MyArticleStatus.DRAFT
                                )

                                navController.navigate("my_articles") {
                                    popUpTo("education_list") {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                            }
                        ) {
                            Text("Tambahkan draft", color = Color.White)
                        }

                        // 🔹 UPLOAD ARTIKEL → simpan & balik ke Education
                        IconButton(
                            onClick = {
                                MyArticleRepository.upsertFromPreview(
                                    category = category,
                                    readTime = readTime,
                                    tag = tag,
                                    title = title,
                                    content = content,
                                    status = MyArticleStatus.UPLOADED
                                )

                                navController.popBackStack(
                                    "education_list",
                                    inclusive = false
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Upload,
                                contentDescription = "Upload",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
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

            Card(
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = BluePrimary
                    )

                    Spacer(Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$category • $readTime",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "19 Oktober 2025",
                            fontSize = 12.sp,
                            color = BluePrimary
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    if (tag.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = BluePrimary.copy(alpha = 0.08f)
                        ) {
                            Text(
                                text = "#$tag",
                                color = BluePrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = content,
                        fontSize = 14.sp,
                        color = Color(0xFF333333),
                        lineHeight = 20.sp
                    )
                }
            }
        }

        if (showInfo.value != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF355A84)),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = showInfo.value ?: "",
                            color = Color.White,
                            fontSize = 13.sp
                        )
                        Spacer(Modifier.width(12.dp))
                        TextButton(onClick = { showInfo.value = null }) {
                            Text("Tutup", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CreateArticlePreviewScreenPreview() {
    val nav = rememberNavController()
    CreateArticlePreviewScreen(
        navController = nav,
        category = "Kesehatan Fisik",
        readTime = "5 menit",
        tag = "Produktif",
        title = "Tips Tidur Nyenyak dan Nyaman",
        content = """
            Ini adalah contoh isi artikel untuk preview.

            Kamu bisa menulis beberapa paragraf di sini agar tampilan preview mirip dengan artikel asli.
        """.trimIndent()
    )
}
