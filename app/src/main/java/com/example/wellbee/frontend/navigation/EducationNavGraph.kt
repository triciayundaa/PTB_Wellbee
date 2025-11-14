package com.example.wellbee.frontend.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wellbee.frontend.screens.Edukasi.ArticleDetailScreen
import com.example.wellbee.frontend.screens.Edukasi.BookmarkScreen
import com.example.wellbee.frontend.screens.Edukasi.CreateArticleContentScreen
import com.example.wellbee.frontend.screens.Edukasi.CreateArticleMetaScreen
import com.example.wellbee.frontend.screens.Edukasi.CreateArticlePreviewScreen
import com.example.wellbee.frontend.screens.Edukasi.EducationArticles
import com.example.wellbee.frontend.screens.Edukasi.EducationScreen
import com.example.wellbee.frontend.screens.Edukasi.MyArticleRepository
import com.example.wellbee.frontend.screens.Edukasi.MyArticlesScreen

@Composable
fun EducationNavGraph() {
    // NavController khusus modul edukasi
    val eduNavController: NavHostController = rememberNavController()

    NavHost(
        navController = eduNavController,
        startDestination = "education_list"
    ) {
        // 🔹 Halaman utama daftar artikel edukasi
        composable("education_list") {
            EducationScreen(navController = eduNavController)
        }

        // 🔹 Halaman bookmark (Artikel Tersimpan)
        composable("bookmark") {
            BookmarkScreen(navController = eduNavController)
        }

        // 🔹 Halaman "Artikel Saya"
        composable("my_articles") {
            MyArticlesScreen(navController = eduNavController)
        }

        // 🔹 STEP 1 – CreateArticleMetaScreen (buat baru / edit)
        // articleId opsional → null = buat baru, ada nilai = edit
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
                articleId = articleId
            )
        }

        // 🔹 STEP 2 – CreateArticleContentScreen
        //    articleId opsional (diteruskan dari meta kalau mode edit)
        composable(
            route = "create_article_content/{category}/{readTime}/{tag}?articleId={articleId}",
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
            val category = backStackEntry.arguments?.getString("category") ?: ""
            val readTime = backStackEntry.arguments?.getString("readTime") ?: ""
            val tag = backStackEntry.arguments?.getString("tag") ?: ""
            val articleId = backStackEntry.arguments?.getString("articleId")

            CreateArticleContentScreen(
                navController = eduNavController,
                category = category,
                readTime = readTime,
                tag = tag,
                articleId = articleId
            )
        }

        // 🔹 STEP 3 – CreateArticlePreviewScreen
        //    articleId opsional juga (diteruskan terus dari content)
        composable(
            route = "create_article_preview/{category}/{readTime}/{tag}/{title}/{content}?articleId={articleId}",
            arguments = listOf(
                navArgument("category") { type = NavType.StringType },
                navArgument("readTime") { type = NavType.StringType },
                navArgument("tag") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType },
                navArgument("content") { type = NavType.StringType },
                navArgument("articleId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            val readTime = backStackEntry.arguments?.getString("readTime") ?: ""
            val tag = backStackEntry.arguments?.getString("tag") ?: ""
            val title = backStackEntry.arguments?.getString("title") ?: ""
            val content = backStackEntry.arguments?.getString("content") ?: ""
            val articleId = backStackEntry.arguments?.getString("articleId")

            CreateArticlePreviewScreen(
                navController = eduNavController,
                category = category,
                readTime = readTime,
                tag = tag,
                title = title,
                content = content,
                articleId = articleId
            )
        }

        // 🔹 Halaman detail artikel (dipakai di modul edukasi)
        composable(
            route = "article_detail/{articleId}"
        ) { backStackEntry ->
            val articleId = backStackEntry.arguments?.getString("articleId")

            // 1. coba cari di artikel bawaan (dummy/static)
            val articleStatic = EducationArticles.articles.find { it.id == articleId }
            // 2. kalau tidak ketemu, coba cari di artikel user
            val articleUser = if (articleId != null) {
                MyArticleRepository.findById(articleId)
            } else null

            when {
                // 🔹 Artikel bawaan
                articleId != null && articleStatic != null -> {
                    ArticleDetailScreen(
                        navController = eduNavController,
                        articleId = articleId,
                        title = articleStatic.title,
                        category = articleStatic.categories.firstOrNull() ?: "Umum",
                        readTime = articleStatic.readTime,
                        imageRes = articleStatic.imageRes,
                        content = articleStatic.content
                        // isUserArticle / authorName / uploadedDate pakai default (null)
                    )
                }

                // 🔹 Artikel buatan user
                articleId != null && articleUser != null -> {
                    ArticleDetailScreen(
                        navController = eduNavController,
                        articleId = articleId,
                        title = articleUser.title,
                        category = articleUser.category,
                        readTime = articleUser.readTime,
                        imageRes = null,              // belum ada gambar user
                        content = articleUser.content,
                        isUserArticle = true,          // ⬅️ ini yang bikin "Penulis: Kamu"
                        authorName = "Kamu",
                        uploadedDate = articleUser.uploadedDate
                    )
                }

                // 🔹 Fallback kalau tidak ditemukan
                else -> {
                    ArticleDetailScreen(
                        navController = eduNavController,
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
