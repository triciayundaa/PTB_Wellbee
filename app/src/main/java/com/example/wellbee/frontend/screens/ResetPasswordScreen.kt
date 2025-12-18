package com.example.wellbee.frontend.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.wellbee.data.RetrofitClient
import com.example.wellbee.data.model.ResetPasswordRequest
import com.example.wellbee.ui.theme.WellbeeTheme
import kotlinx.coroutines.launch

// Perbarui file ResetPasswordScreen.kt Anda
@Composable
fun ResetPasswordScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val apiService = remember { RetrofitClient.getInstance(context) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Enter New Password", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("New Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = confirm, onValueChange = { confirm = it }, label = { Text("Confirm Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                // 1. Validasi Input
                if (password != confirm) {
                    android.widget.Toast.makeText(context, "Password tidak cocok!", android.widget.Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (email.isEmpty() || password.isEmpty()) {
                    android.widget.Toast.makeText(context, "Harap isi semua bidang!", android.widget.Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // 2. Eksekusi API
                isLoading = true
                scope.launch {
                    try {
                        val response = apiService.resetPassword(
                            ResetPasswordRequest(
                                email,
                                password
                            )
                        )
                        if (response.isSuccessful) {
                            android.widget.Toast.makeText(context, "Password berhasil diperbarui!", android.widget.Toast.LENGTH_LONG).show()
                            navController.navigate("login") {
                                popUpTo("reset_password") { inclusive = true }
                            }
                        } else {
                            android.widget.Toast.makeText(context, "Gagal: Email tidak ditemukan", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        android.widget.Toast.makeText(context, "Terjadi kesalahan koneksi", android.widget.Toast.LENGTH_SHORT).show()
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading // Matikan tombol saat loading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp), // Ukuran diatur di sini
                    color = androidx.compose.ui.graphics.Color.White,
                    strokeWidth = 2.dp // Opsional: agar garis tidak terlalu tebal di ukuran kecil
                )
            } else {
                Text(text = "Save")
            }
        }
    }
}
