@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.wellbee.frontend.screens.Edukasi

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.wellbee.data.RetrofitClient
import com.example.wellbee.data.model.EducationViewModel
import com.example.wellbee.frontend.components.EducationTopBarWithBack
import com.example.wellbee.ui.theme.BluePrimary
import com.example.wellbee.ui.theme.GrayBackground

@Composable
fun CreateArticleContentScreen(
    navController: NavHostController,
    viewModel: EducationViewModel,
    category: String,
    readTime: String,
    tag: String,
    articleId: String? = null
) {
    val context = LocalContext.current

    var titleError by remember { mutableStateOf<String?>(null) }
    var contentError by remember { mutableStateOf<String?>(null) }

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()


    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val path = MediaStore.Images.Media.insertImage(
                context.contentResolver,
                bitmap,
                "Wellbee_${System.currentTimeMillis()}",
                null
            )
            viewModel.draftImageUri = Uri.parse(path)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) viewModel.draftImageUri = uri
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch()
        } else {
            Toast.makeText(context, "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    val myArticles = viewModel.myArticles

    LaunchedEffect(Unit) {
        if (articleId != null) {
            viewModel.loadMyArticles()
        }
    }

    LaunchedEffect(myArticles, articleId) {
        if (articleId != null) {
            val idInt = articleId.toIntOrNull()
            val article = myArticles.find { it.id == idInt }
            article?.let {

                if (viewModel.draftTitle.isEmpty()) {
                    viewModel.draftTitle = it.judul
                }

                if (viewModel.draftContent.isEmpty()) {
                    viewModel.draftContent = it.isi
                }

                if (viewModel.draftImageUri == null && !it.gambarUrl.isNullOrEmpty()) {
                    val fullUrl = if (it.gambarUrl!!.startsWith("http")) {
                        it.gambarUrl
                    } else {
                        val base = RetrofitClient.BASE_URL.removeSuffix("/")
                        val path = if (it.gambarUrl!!.startsWith("/")) it.gambarUrl else "/${it.gambarUrl}"
                        "$base$path"
                    }
                    viewModel.draftImageUri = Uri.parse(fullUrl)
                }
            }
        }
    }


    Scaffold(
        topBar = {
            EducationTopBarWithBack(
                title = "Isi Konten",
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
                .padding(24.dp)
        ) {

            Text(
                text = if (articleId == null) "Tulis Artikelmu" else "Edit Konten Artikel",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1B3B6B),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(20.dp)) {

                    Text("Judul Artikel", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF21436B))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = viewModel.draftTitle,
                        onValueChange = {
                            viewModel.draftTitle = it
                            titleError = null
                        },
                        placeholder = { Text("Tulis judul menarik...") },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BluePrimary,
                            unfocusedTextColor = Color.Black,
                            focusedTextColor = Color.Black
                        )
                    )
                    titleError?.let { Text(it, color = Color.Red, fontSize = 12.sp) }

                    Spacer(Modifier.height(20.dp))

                    Text("Gambar Sampul", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF21436B))
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF0F4F8))
                            .clickable { showBottomSheet = true },
                        contentAlignment = Alignment.Center
                    ) {
                        if (viewModel.draftImageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(viewModel.draftImageUri),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.CameraAlt, null, tint = BluePrimary, modifier = Modifier.size(40.dp))
                                Text("Tambah Gambar", color = BluePrimary, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Text("Isi Artikel", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF21436B))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = viewModel.draftContent,
                        onValueChange = {
                            viewModel.draftContent = it
                            contentError = null
                        },
                        placeholder = { Text("Tuliskan detail informasi di sini...") },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black),
                        minLines = 8,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BluePrimary,
                            unfocusedTextColor = Color.Black,
                            focusedTextColor = Color.Black
                        )
                    )
                    contentError?.let { Text(it, color = Color.Red, fontSize = 12.sp) }
                }
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    if (viewModel.draftTitle.isBlank()) titleError = "Judul wajib diisi"
                    if (viewModel.draftContent.isBlank()) contentError = "Isi artikel wajib diisi"

                    if (viewModel.draftTitle.isNotBlank() && viewModel.draftContent.isNotBlank()) {
                        val route = if (articleId != null) {
                            "create_article_preview?articleId=$articleId"
                        } else {
                            "create_article_preview"
                        }
                        navController.navigate(route)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BluePrimary,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Visibility, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Lihat Preview", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }


    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = Color.White,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(bottom = 32.dp, start = 24.dp, end = 24.dp, top = 8.dp)
            ) {
                Text(
                    text = "Pilih Sumber Gambar",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = Color(0xFF1B3B6B),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                ListItem(
                    headlineContent = {
                        Text("Ambil Foto dari Kamera", color = Color.Black, fontWeight = FontWeight.Medium)
                    },
                    leadingContent = { Icon(Icons.Default.CameraAlt, null, tint = BluePrimary) },
                    colors = ListItemDefaults.colors(containerColor = Color.White),
                    modifier = Modifier.clickable {
                        showBottomSheet = false
                        val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                            cameraLauncher.launch()
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                )

                ListItem(
                    headlineContent = {
                        Text("Pilih dari Galeri", color = Color.Black, fontWeight = FontWeight.Medium)
                    },
                    leadingContent = { Icon(Icons.Default.Image, null, tint = BluePrimary) },
                    colors = ListItemDefaults.colors(containerColor = Color.White),
                    modifier = Modifier.clickable {
                        showBottomSheet = false
                        galleryLauncher.launch("image/*")
                    }
                )
            }
        }
    }
}