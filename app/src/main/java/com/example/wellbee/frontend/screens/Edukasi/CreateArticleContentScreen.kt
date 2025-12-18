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

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    var titleError by remember { mutableStateOf<String?>(null) }
    var contentError by remember { mutableStateOf<String?>(null) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // State untuk Bottom Sheet
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    /** ================= IMAGE LAUNCHERS ================= */

    // 1. Launcher untuk Kamera
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
            selectedImageUri = Uri.parse(path)
        }
    }

    // 2. Launcher untuk Galeri
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) selectedImageUri = uri
    }

    // 3. Launcher untuk meminta izin kamera
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch()
        } else {
            Toast.makeText(context, "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    /** ================= LOGIKA LOAD DATA (MODE EDIT) ================= */
    val myArticles = viewModel.myArticles

    LaunchedEffect(Unit) {
        if (articleId != null) {
            viewModel.loadMyArticles()
        }
    }

    LaunchedEffect(myArticles) {
        if (articleId != null) {
            val idInt = articleId.toIntOrNull()
            val article = myArticles.find { it.id == idInt }
            article?.let {
                if (title.isEmpty()) title = it.judul
                if (content.isEmpty()) content = it.isi
                if (selectedImageUri == null && !it.gambarUrl.isNullOrEmpty()) {
                    selectedImageUri = Uri.parse(it.gambarUrl)
                }
            }
        }
    }

    /** ================= UI ================= */
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

                    /** JUDUL **/
                    Text("Judul Artikel", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF21436B))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it; titleError = null },
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

                    /** GAMBAR SAMPUL **/
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
                        if (selectedImageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(selectedImageUri),
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

                    /** ISI KONTEN **/
                    Text("Isi Artikel", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF21436B))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it; contentError = null },
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

            /** BUTTON PREVIEW **/
            Button(
                onClick = {
                    if (title.isBlank()) titleError = "Judul wajib diisi"
                    if (content.isBlank()) contentError = "Isi artikel wajib diisi"

                    if (title.isNotBlank() && content.isNotBlank()) {
                        val baseRoute = "create_article_preview/" +
                                "${Uri.encode(category)}/" +
                                "${Uri.encode(readTime)}/" +
                                "${Uri.encode(tag)}/" +
                                "${Uri.encode(title)}/" +
                                "${Uri.encode(content)}"

                        val imageArg = Uri.encode(selectedImageUri?.toString() ?: "")
                        val fullRoute = if (articleId != null) {
                            "$baseRoute?imageUri=$imageArg&articleId=$articleId"
                        } else {
                            "$baseRoute?imageUri=$imageArg"
                        }
                        navController.navigate(fullRoute)
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

    /** ================= MODAL BOTTOM SHEET (KAMERA / GALERI) ================= */
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = Color.White, // Memastikan background sheet putih
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White) // Penegasan background putih
                    .padding(bottom = 32.dp, start = 24.dp, end = 24.dp, top = 8.dp)
            ) {
                Text(
                    text = "Pilih Sumber Gambar",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = Color(0xFF1B3B6B),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Opsi Kamera
                ListItem(
                    headlineContent = {
                        Text("Ambil Foto dari Kamera", color = Color.Black, fontWeight = FontWeight.Medium)
                    },
                    leadingContent = { Icon(Icons.Default.CameraAlt, null, tint = BluePrimary) },
                    colors = ListItemDefaults.colors(containerColor = Color.White), // 🔹 Perbaikan warna di sini
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

                // Opsi Galeri
                ListItem(
                    headlineContent = {
                        Text("Pilih dari Galeri", color = Color.Black, fontWeight = FontWeight.Medium)
                    },
                    leadingContent = { Icon(Icons.Default.Image, null, tint = BluePrimary) },
                    colors = ListItemDefaults.colors(containerColor = Color.White), // 🔹 Perbaikan warna di sini
                    modifier = Modifier.clickable {
                        showBottomSheet = false
                        galleryLauncher.launch("image/*")
                    }
                )
            }
        }
    }
}