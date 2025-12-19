package com.example.wellbee.frontend.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.wellbee.ui.theme.BluePrimary

@Composable
fun EducationNavGraph() {
    val eduNavController: NavHostController = rememberNavController()
    val context = LocalContext.current

    // 🔹 ViewModel di-share untuk seluruh alur pembuatan artikel
    val sharedViewModel = remember { EducationViewModel(context) }

    NavHost(
        navController = eduNavController,
        startDestination = "education_list"
    ) {

        /* ───────────── LIST / BOOKMARK / ARTIKEL SAYA ───────────── */

        composable("education_list") {
            EducationScreen(navController = eduNavController, viewModel = sharedViewModel)
        }

        composable("bookmark") {
            BookmarkScreen(navController = eduNavController, viewModel = sharedViewModel)
        }

        composable("my_articles") {
            MyArticlesScreen(navController = eduNavController, viewModel = sharedViewModel)
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
            val articleId = backStackEntry.arguments?.getString("articleId")

            CreateArticleMetaScreen(
                navController = eduNavController,
                viewModel = sharedViewModel,
                articleId = articleId
            )
        }

        // STEP 2 — CONTENT
        // 🔹 PERBAIKAN: Parameter navigasi dibuat opsional karena data utama ada di ViewModel
        composable(
            route = "create_article_content?articleId={articleId}",
            arguments = listOf(
                navArgument("articleId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            CreateArticleContentScreen(
                navController = eduNavController,
                viewModel = sharedViewModel,
                articleId = backStackEntry.arguments?.getString("articleId"),
                // Parameter kategori dll tidak perlu dikirim lewat URL lagi karena sudah ada di sharedViewModel
                category = sharedViewModel.draftCategory,
                readTime = sharedViewModel.draftReadTime,
                tag = sharedViewModel.draftTag
            )
        }

        // STEP 3 — PREVIEW
        composable(
            route = "create_article_preview?articleId={articleId}",
            arguments = listOf(
                navArgument("articleId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            CreateArticlePreviewScreen(
                navController = eduNavController,
                viewModel = sharedViewModel,
                articleId = backStackEntry.arguments?.getString("articleId"),
                // Mengambil data langsung dari draf ViewModel
                category = sharedViewModel.draftCategory,
                readTime = sharedViewModel.draftReadTime,
                tag = sharedViewModel.draftTag,
                title = sharedViewModel.draftTitle,
                content = sharedViewModel.draftContent
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

            LaunchedEffect(articleId, source) {
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
                    content = "ID artikel tidak ditemukan."
                )
                return@composable
            }

            val article = if (source == "my") {
                sharedViewModel.myArticles.find { it.id == articleId }
            } else {
                sharedViewModel.articles.find { it.id == articleId }
            }

            if (article != null) {
                val isPublic = article is com.example.wellbee.data.model.PublicArticleDto
                ArticleDetailScreen(
                    navController = eduNavController,
                    articleId = articleId.toString(),
                    title = if (isPublic) (article as com.example.wellbee.data.model.PublicArticleDto).judul else (article as com.example.wellbee.data.model.MyArticleDto).judul,
                    category = if (isPublic) ((article as com.example.wellbee.data.model.PublicArticleDto).kategori ?: "Umum") else ((article as com.example.wellbee.data.model.MyArticleDto).kategori ?: "Umum"),
                    readTime = if (isPublic) ((article as com.example.wellbee.data.model.PublicArticleDto).waktuBaca ?: "-") else ((article as com.example.wellbee.data.model.MyArticleDto).waktuBaca ?: "-"),
                    imageUrl = if (isPublic) (article as com.example.wellbee.data.model.PublicArticleDto).gambarUrl else (article as com.example.wellbee.data.model.MyArticleDto).gambarUrl,
                    content = if (isPublic) (article as com.example.wellbee.data.model.PublicArticleDto).isi else (article as com.example.wellbee.data.model.MyArticleDto).isi,
                    isUserArticle = source == "my" || (isPublic && (article as com.example.wellbee.data.model.PublicArticleDto).jenis == "user"),
                    authorName = if (isPublic) (article as com.example.wellbee.data.model.PublicArticleDto).authorName else (article as com.example.wellbee.data.model.MyArticleDto).authorName,
                    uploadedDate = if (isPublic) (article as com.example.wellbee.data.model.PublicArticleDto).tanggal else (article as com.example.wellbee.data.model.MyArticleDto).tanggalUpload
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BluePrimary)
                }
            }
        }
    }
}