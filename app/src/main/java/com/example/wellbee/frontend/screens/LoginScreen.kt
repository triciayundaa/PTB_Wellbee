package com.example.wellbee.frontend.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.wellbee.data.AuthRepository
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavHostController) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authRepo = AuthRepository(context)

    val BluePrimary = Color(0xFF0E4DA4)

    val inputColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = BluePrimary,
        unfocusedBorderColor = Color.Black,
        focusedLabelColor = BluePrimary,
        unfocusedLabelColor = Color.Black,
        cursorColor = BluePrimary,
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("Hey there,", color = BluePrimary, fontWeight = FontWeight.Bold)
        Text("Welcome Back", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            colors = inputColors
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = inputColors
        )

        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = { navController.navigate("reset_password") },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Forgot your password?", color = BluePrimary)
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    scope.launch {
                        val result = authRepo.login(email, password)
                        if (result.isSuccess) {
                            navController.navigate("main") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            Toast.makeText(context, "Email atau password salah", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Isi semua bagian!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = BluePrimary,
                contentColor = Color.White
            )
        ) {
            Text("Login")
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Don’t have an account? ", color = Color.Black)
            TextButton(
                onClick = { navController.navigate("register") },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Register",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BluePrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}