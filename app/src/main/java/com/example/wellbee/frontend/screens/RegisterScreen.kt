package com.example.wellbee.frontend.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.wellbee.data.AuthRepository
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController: NavHostController) {
    // Siapkan Repository & State
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { AuthRepository(context) }

    var name by remember { mutableStateOf("") }     // Akan dikirim sebagai username
    var phone by remember { mutableStateOf("") }    // (Hiasan UI saja, belum masuk DB)
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
        Text("Hey there,", style = MaterialTheme.typography.titleMedium)
        Text("Create an Account", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        // Input Fields
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name (Username)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = confirm,
            onValueChange = { confirm = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        // Tombol Register
        Button(
            onClick = {
                // 1. Validasi Input Kosong
                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "Harap isi Nama, Email, dan Password", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // 2. Validasi Password Match
                if (password != confirm) {
                    Toast.makeText(context, "Password tidak sama!", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isLoading = true

                // 3. Proses Register ke Backend
                scope.launch {
                    // Kita kirim 'name' sebagai 'username' ke backend
                    val result = repository.register(username = name, email = email, pass = password, phone = phone)

                    isLoading = false

                    if (result.isSuccess) {
                        Toast.makeText(context, "Registrasi Berhasil! Silakan Login.", Toast.LENGTH_LONG).show()
                        // Pindah ke halaman Login
                        navController.navigate("login")
                    } else {
                        // Tampilkan error (misal: Email sudah terdaftar)
                        Toast.makeText(context, "Gagal: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
            } else {
                Text("Register")
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Already have an Account?",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.width(4.dp))
            TextButton(
                onClick = { navController.navigate("login") },
                enabled = !isLoading
            ) {
                Text("Login")
            }
        }
    }
}