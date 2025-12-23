package com.example.wellbee.frontend.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wellbee.data.FisikRepository
import com.example.wellbee.data.model.EducationViewModel
import com.example.wellbee.frontend.components.BottomNavigationBar
import com.example.wellbee.frontend.navigation.EducationNavGraph
import com.example.wellbee.frontend.navigation.MentalNavGraph
import com.example.wellbee.frontend.screens.Edukasi.ArticleDetailScreen
import com.example.wellbee.frontend.screens.Fisik.PhysicalHealthScreen
import com.example.wellbee.frontend.screens.Home.HomeScreen
import com.example.wellbee.ui.theme.BluePrimary
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

@SuppressLint("ComposableDestinationInComposeScope")
@Composable
fun MainScreen(parentNavController: NavHostController) {

    val context = LocalContext.current

    val repo = remember { FisikRepository(context) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            if (!token.isNullOrEmpty()) {
                android.util.Log.d("FCM_TOKEN", "🏠 MainScreen: Token HP Ditemukan: $token")
                scope.launch {
                    repo.syncFcmToken(token)
                }
            }
        }.addOnFailureListener {
            android.util.Log.e("FCM_TOKEN", "🏠 MainScreen: Gagal ambil token")
        }
    }

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
                    HomeScreen(navController = parentNavController)
                }
                composable("education") {
                    EducationNavGraph()
                }
                composable("mental") {
                    MentalNavGraph(parentNavController = bottomNavController)
                }
                composable("physical") {
                    PhysicalHealthScreen(parentNavController = bottomNavController)
                }
                composable(
                    route = "article_detail/{articleId}",
                    arguments = listOf(
                        navArgument("articleId") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val articleIdStr = backStackEntry.arguments?.getString("articleId")
                    val articleIdInt = articleIdStr?.toIntOrNull()

                    val context = LocalContext.current
                    val viewModel = remember { EducationViewModel(context) }

                    LaunchedEffect(articleIdStr) {
                        viewModel.loadArticles()
                    }

                    val articles = viewModel.articles
                    val isLoading = viewModel.isLoading

                    val article = if (articleIdInt != null) {
                        articles.find { it.id == articleIdInt }
                    } else null

                    when {
                        article != null -> {
                            ArticleDetailScreen(
                                navController = bottomNavController,
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
                        isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = BluePrimary)
                            }
                        }
                        else -> {
                            ArticleDetailScreen(
                                navController = bottomNavController,
                                articleId = articleIdStr ?: "",
                                title = "Artikel tidak ditemukan",
                                category = "Umum",
                                readTime = "-",
                                imageRes = null,
                                imageUrl = null,
                                content = "Maaf, artikel yang Anda pilih tidak tersedia.",
                                isUserArticle = false,
                                authorName = null,
                                uploadedDate = null
                            )
                        }
                    }
                }
            }
        }
    }
}