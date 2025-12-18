package com.example.wellbee.frontend.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wellbee.data.model.EducationViewModel
import com.example.wellbee.frontend.education.MyArticlesScreen
import com.example.wellbee.frontend.screens.Edukasi.*

@Composable
fun EducationNavGraph() {
    val eduNavController: NavHostController = rememberNavController()
    val context = LocalContext.current

    // 🔹 SOLUSI UTAMA: Inisialisasi ViewModel di sini agar di-share ke semua screen
    // Ini memastikan data yang di-upload di Preview langsung muncul di MyArticlesScreen
    val sharedViewModel = remember { EducationViewModel(context) }

    NavHost(
        navController = eduNavController,
        startDestination = "education_list"
    ) {

        /* ───────────── LIST / BOOKMARK / ARTIKEL SAYA ───────────── */

        composable("education_list") {
            EducationScreen(
                navController = eduNavController,
                viewModel = sharedViewModel // Kirim viewModel yang sama
            )
        }

        composable("bookmark") {
            BookmarkScreen(
                navController = eduNavController,
                viewModel = sharedViewModel // Kirim viewModel yang sama
            )
        }

        composable("my_articles") {
            MyArticlesScreen(
                navController = eduNavController,
                viewModel = sharedViewModel // Kirim viewModel yang sama
            )
        }

        /* ───────────── FLOW BUAT / EDIT ARTIKEL ───────────── */

        // STEP 1 — META
        composable(
            route = "create_article_meta?articleId={articleId}",
            arguments = listOf(
                navArgument("articleId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            CreateArticleMetaScreen(
                navController = eduNavController,
                viewModel = sharedViewModel, // Gunakan sharedViewModel agar data edit muncul
                articleId = backStackEntry.arguments?.getString("articleId")
            )
        }

        // STEP 2 — CONTENT
        composable(
            route = "create_article_content/{category}/{readTime}/{tag}" +
                    "?articleId={articleId}",
            arguments = listOf(
                navArgument("category") { type = NavType.StringType },
                navArgument("readTime") { type = NavType.StringType },
                navArgument("tag") { type = NavType.StringType },
                navArgument("articleId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            CreateArticleContentScreen(
                navController = eduNavController,
                viewModel = sharedViewModel, // Gunakan sharedViewModel
                category = backStackEntry.arguments?.getString("category") ?: "",
                readTime = backStackEntry.arguments?.getString("readTime") ?: "",
                tag = backStackEntry.arguments?.getString("tag") ?: "",
                articleId = backStackEntry.arguments?.getString("articleId")
            )
        }

        // STEP 3 — PREVIEW
        composable(
            route = "create_article_preview/{category}/{readTime}/{tag}/{title}/{content}" +
                    "?imageUri={imageUri}&articleId={articleId}",
            arguments = listOf(
                navArgument("category") { type = NavType.StringType },
                navArgument("readTime") { type = NavType.StringType },
                navArgument("tag") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType },
                navArgument("content") { type = NavType.StringType },
                navArgument("imageUri") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("articleId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            CreateArticlePreviewScreen(
                navController = eduNavController,
                viewModel = sharedViewModel, // Gunakan sharedViewModel untuk proses upload/update
                category = backStackEntry.arguments?.getString("category") ?: "",
                readTime = backStackEntry.arguments?.getString("readTime") ?: "",
                tag = backStackEntry.arguments?.getString("tag") ?: "",
                title = backStackEntry.arguments?.getString("title") ?: "",
                content = backStackEntry.arguments?.getString("content") ?: "",
                imageUri = backStackEntry.arguments?.getString("imageUri"),
                articleId = backStackEntry.arguments?.getString("articleId")
            )
        }

        /* ───────────── DETAIL ARTIKEL (BACKEND) ───────────── */

        composable(
            route = "article_detail/{articleId}?source={source}",
            arguments = listOf(
                navArgument("articleId") { type = NavType.StringType },
                navArgument("source") {
                    type = NavType.StringType
                    defaultValue = "public"
                }
            )
        ) { backStackEntry ->
            val articleIdStr = backStackEntry.arguments?.getString("articleId")
            val articleId = articleIdStr?.toIntOrNull()
            val source = backStackEntry.arguments?.getString("source") ?: "public"

            // Gunakan sharedViewModel di sini juga agar sinkron
            LaunchedEffect(source) {
                if (source == "my") sharedViewModel.loadMyArticles()
                else sharedViewModel.loadArticles()
            }

            if (articleId == null) {
                ArticleDetailScreen(
                    navController = eduNavController,
                    articleId = "",
                    title = "Artikel tidak valid",
                    category = "Umum",
                    readTime = "-",
                    imageRes = null,
                    imageUrl = null,
                    content = "ID artikel tidak ditemukan."
                )
                return@composable
            }

            if (source == "my") {
                val article = sharedViewModel.myArticles.find { it.id == articleId }
                if (article != null) {
                    ArticleDetailScreen(
                        navController = eduNavController,
                        articleId = article.id.toString(),
                        title = article.judul,
                        category = article.kategori ?: "Umum",
                        readTime = article.waktuBaca ?: "-",
                        imageRes = null,
                        imageUrl = article.gambarUrl,
                        content = article.isi,
                        isUserArticle = true,
                        authorName = article.authorName ?: "Kamu",
                        uploadedDate = article.tanggalUpload
                    )
                }
            } else {
                val article = sharedViewModel.articles.find { it.id == articleId }
                if (article != null) {
                    ArticleDetailScreen(
                        navController = eduNavController,
                        articleId = article.id.toString(),
                        title = article.judul,
                        category = article.kategori ?: "Umum",
                        readTime = article.waktuBaca ?: "-",
                        imageRes = null,
                        imageUrl = article.gambarUrl,
                        content = article.isi,
                        isUserArticle = article.jenis == "user",
                        authorName = article.authorName,
                        uploadedDate = article.tanggal
                    )
                }
            }
        }
    }
}