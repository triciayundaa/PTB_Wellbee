package com.example.wellbee.frontend.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.navigation.navArgument
import com.example.wellbee.data.SessionManager
import com.example.wellbee.data.model.EducationViewModel
import com.example.wellbee.data.model.PublicArticleDto
import com.example.wellbee.data.model.MyArticleDto
import com.example.wellbee.frontend.screens.WelcomeScreen
import com.example.wellbee.frontend.screens.RegisterScreen
import com.example.wellbee.frontend.screens.LoginScreen
import com.example.wellbee.frontend.screens.ResetPasswordScreen
import com.example.wellbee.frontend.screens.MainScreen
import com.example.wellbee.frontend.screens.ProfileScreen
import com.example.wellbee.frontend.screens.Edukasi.ArticleDetailScreen
import com.example.wellbee.frontend.screens.Mental.DetailDiaryScreen
import com.example.wellbee.frontend.screens.Mental.JournalListScreen
import com.example.wellbee.ui.theme.BluePrimary

@Composable
fun NavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)

    // Tentukan startDestination secara dinamis berdasarkan status login
    val startDest = if (sessionManager.isLoggedIn()) "main" else "welcome"

    NavHost(
        navController = navController,
        startDestination = startDest
    ) {
        // --- AUTH SCREENS ---
        composable("welcome") { WelcomeScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("reset_password") { ResetPasswordScreen(navController) }

        // --- MAIN SCREEN (Dashboard) ---
        composable("main") {
            MainScreen(parentNavController = navController)
        }

        // --- PROFILE SCREEN ---
        composable("profile") {
            ProfileScreen(navController = navController)
        }

        // --- RUTE KHUSUS NOTIFIKASI FISIK (FIXED: ANTI CRASH & TAMPILAN BENAR) ---
        composable("global_sport_screen") {
            val localNavController = androidx.navigation.compose.rememberNavController()

            androidx.compose.material3.Scaffold(
                bottomBar = {
                    com.example.wellbee.frontend.components.BottomNavigationBar(
                        navController = navController
                    )
                }
            ) { innerPadding ->
                androidx.compose.foundation.layout.Box(
                    modifier = androidx.compose.ui.Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .background(androidx.compose.ui.graphics.Color(0xFFF7F9FB))
                ) {
                    com.example.wellbee.frontend.navigation.PhysicalNavGraph(
                        navController = localNavController
                    )
                }
            }
        }

        // --- RUTE KHUSUS NOTIFIKASI MENTAL (PENTING AGAR TIDAK CRASH SAAT KLIK NOTIF) ---
        composable(
            route = "detail_diary/{journalId}",
            arguments = listOf(navArgument("journalId") { type = NavType.IntType })
        ) { backStackEntry ->
            val journalId = backStackEntry.arguments?.getInt("journalId") ?: -1
            DetailDiaryScreen(
                navController = navController,
                journalId = journalId
            )
        }

        composable("journal_list") {
            JournalListScreen(navController = navController)
        }

        // --- 🛡️ JARING PENGAMAN NAVBAR (ANTI CRASH) ---
        composable("home") {
            LaunchedEffect(Unit) {
                navController.navigate("main") {
                    popUpTo("main") { inclusive = true }
                }
            }
        }

        composable("mental") {
            LaunchedEffect(Unit) {
                navController.navigate("main") { popUpTo("main") { inclusive = true } }
            }
        }

        composable("education") {
            LaunchedEffect(Unit) {
                navController.navigate("main") { popUpTo("main") { inclusive = true } }
            }
        }

        composable("physical") {
            LaunchedEffect(Unit) {
                navController.navigate("global_sport_screen") { launchSingleTop = true }
            }
        }

        // --- DETAIL ARTIKEL (GLOBAL ROUTE) ---
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

            val sharedViewModel = remember { EducationViewModel(context) }

            LaunchedEffect(articleId, source) {
                if (source == "my") sharedViewModel.loadMyArticles()
                else sharedViewModel.loadArticles()
            }

            if (articleId == null) {
                ArticleDetailScreen(
                    navController = navController,
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
                val isPublic = article is PublicArticleDto
                ArticleDetailScreen(
                    navController = navController,
                    articleId = articleId.toString(),
                    title = if (isPublic) (article as PublicArticleDto).judul else (article as MyArticleDto).judul,
                    category = if (isPublic) ((article as PublicArticleDto).kategori ?: "Umum") else ((article as MyArticleDto).kategori ?: "Umum"),
                    readTime = if (isPublic) ((article as PublicArticleDto).waktuBaca ?: "-") else ((article as MyArticleDto).waktuBaca ?: "-"),
                    imageUrl = if (isPublic) (article as PublicArticleDto).gambarUrl else (article as MyArticleDto).gambarUrl,
                    content = if (isPublic) (article as PublicArticleDto).isi else (article as MyArticleDto).isi,
                    isUserArticle = source == "my" || (isPublic && (article as PublicArticleDto).jenis == "user"),
                    authorName = if (isPublic) (article as PublicArticleDto).authorName else (article as MyArticleDto).authorName,
                    uploadedDate = if (isPublic) (article as PublicArticleDto).tanggal else (article as MyArticleDto).tanggalUpload
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BluePrimary)
                }
            }
        }
    }
}