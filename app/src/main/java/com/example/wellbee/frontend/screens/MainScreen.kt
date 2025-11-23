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
import com.example.wellbee.frontend.navigation.MentalNavGraph     // 🔹 dari branch Nailah
import com.example.wellbee.frontend.screens.Edukasi.ArticleDetailScreen
import com.example.wellbee.frontend.screens.Edukasi.EducationArticles
import com.example.wellbee.frontend.screens.Edukasi.MyArticleRepository
import com.example.wellbee.frontend.screens.Fisik.PhysicalHealthScreen
import com.example.wellbee.frontend.screens.Home.HomeScreen

@SuppressLint("ComposableDestinationInComposeScope")
@Composable
fun MainScreen(parentNavController: NavHostController) {

    // nav controller untuk bottom nav
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = bottomNavController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {

            NavHost(
                navController = bottomNavController,
                startDestination = "home"
            ) {
                // HOME
                composable("home") {
                    HomeScreen(navController = bottomNavController)
                }

                // EDUCATION
                composable("education") {
                    EducationNavGraph()
                }

                // MENTAL (ambil dari branch Nailah)
                composable("mental") {
                    MentalNavGraph(parentNavController = bottomNavController)
                }

                // PHYSICAL
                composable("physical") {
                    PhysicalHealthScreen()
                }

                // ======================
                // ARTICLE DETAIL ROUTE
                // ======================
                composable(
                    route = "article_detail/{articleId}"
                ) { backStackEntry ->
                    val articleId = backStackEntry.arguments?.getString("articleId")

                    // artikel bawaan
                    val articleStatic = EducationArticles.articles.find { it.id == articleId }

                    // artikel user
                    val articleUser = articleId?.let { MyArticleRepository.findById(it) }

                    when {
                        articleStatic != null -> {
                            ArticleDetailScreen(
                                navController = bottomNavController,
                                articleId = articleStatic.id,
                                title = articleStatic.title,
                                category = articleStatic.categories.firstOrNull() ?: "Umum",
                                readTime = articleStatic.readTime,
                                imageRes = articleStatic.imageRes,
                                content = articleStatic.content
                            )
                        }

                        articleUser != null -> {
                            ArticleDetailScreen(
                                navController = bottomNavController,
                                articleId = articleUser.id,
                                title = articleUser.title,
                                category = articleUser.category,
                                readTime = articleUser.readTime,
                                imageRes = null,
                                content = articleUser.content
                            )
                        }

                        else -> {
                            ArticleDetailScreen(
                                navController = bottomNavController,
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
