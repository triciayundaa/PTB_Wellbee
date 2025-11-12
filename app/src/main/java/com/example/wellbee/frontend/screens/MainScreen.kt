package com.example.wellbee.frontend.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wellbee.frontend.components.BottomNavigationBar
import com.example.wellbee.frontend.navigation.EducationNavGraph
import com.example.wellbee.frontend.screens.Edukasi.ArticleDetailScreen
import com.example.wellbee.frontend.screens.Edukasi.EducationArticles
import com.example.wellbee.frontend.screens.Edukasi.MyArticleRepository   // 🔹 import ini
import com.example.wellbee.frontend.screens.Fisik.PhysicalHealthScreen
import com.example.wellbee.frontend.screens.Home.HomeScreen
import com.example.wellbee.frontend.screens.Mental.MentalHealthScreen

@SuppressLint("ComposableDestinationInComposeScope")
@Composable
fun MainScreen(parentNavController: NavHostController) {
    // NavController khusus untuk bottom nav
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = bottomNavController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {

            NavHost(
                navController = bottomNavController,
                startDestination = "home"
            ) {
                composable("home") {
                    HomeScreen(navController = bottomNavController)
                }

                composable("education") {
                    EducationNavGraph()
                }

                composable("mental") { MentalHealthScreen() }
                composable("physical") { PhysicalHealthScreen() }

                // 🔹 Detail artikel yang dibuka dari HOME
                composable(
                    route = "article_detail/{articleId}"
                ) { backStackEntry ->
                    val articleId = backStackEntry.arguments?.getString("articleId")

                    // 1. coba cari di artikel bawaan
                    val articleStatic = EducationArticles.articles.find { it.id == articleId }
                    // 2. kalau tidak ketemu, coba cari di artikel user
                    val articleUser = if (articleId != null) {
                        MyArticleRepository.findById(articleId)
                    } else null

                    when {
                        articleId != null && articleStatic != null -> {
                            ArticleDetailScreen(
                                navController = bottomNavController,   // 🔹 pakai bottomNavController
                                articleId = articleId,
                                title = articleStatic.title,
                                category = articleStatic.categories.firstOrNull() ?: "Umum",
                                readTime = articleStatic.readTime,
                                imageRes = articleStatic.imageRes,
                                content = articleStatic.content
                            )
                        }

                        articleId != null && articleUser != null -> {
                            ArticleDetailScreen(
                                navController = bottomNavController,   // 🔹 sama di sini
                                articleId = articleId,
                                title = articleUser.title,
                                category = articleUser.category,
                                readTime = articleUser.readTime,
                                imageRes = null,              // belum ada gambar user
                                content = articleUser.content
                            )
                        }

                        else -> {
                            ArticleDetailScreen(
                                navController = bottomNavController,   // 🔹 dan di sini juga
                                articleId = articleId ?: "",
                                title = "Artikel tidak ditemukan",
                                category = "Umum",
                                readTime = "-",
                                imageRes = null,
                                content = "Maaf, artikel yang Anda pilih tidak tersedia."
                            )
                        }
                    }
                }
            }
        }
    }
}
