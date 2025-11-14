package com.example.wellbee.frontend.screens.mental

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.wellbee.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(navController: NavHostController) {

    var selectedTrigger by remember { mutableStateOf("Pilih Pemicu") }
    var customTrigger by remember { mutableStateOf(TextFieldValue("")) }
    var diaryText by remember { mutableStateOf(TextFieldValue("")) }
    var expanded by remember { mutableStateOf(false) }
    var showSavedPopup by remember { mutableStateOf(false) }

    val triggers = listOf("Tugas", "Pertemanan", "Lainnya")
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF105490))
                    .padding(top = 40.dp, bottom = 20.dp, start = 12.dp, end = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Mental Health",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(40.dp))
                }
            }

            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search here...") },
                shape = RoundedCornerShape(30.dp)
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .animateContentSize()
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Dear Diary",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF105490)
                    )
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = null,
                        tint = Color(0xFF105490)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFD9F2E6), RoundedCornerShape(20.dp))
                        .clickable { expanded = true }
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedTrigger,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF105490)
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_down),
                            contentDescription = null,
                            tint = Color(0xFF105490)
                        )
                    }
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .background(Color(0xFFD9F2E6))
                ) {
                    triggers.forEach { trigger ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    trigger,
                                    color = Color(0xFF105490),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            onClick = {
                                selectedTrigger = trigger
                                expanded = false
                                if (trigger != "Lainnya") {
                                    customTrigger = TextFieldValue("")
                                }
                            }
                        )
                    }
                }

                if (selectedTrigger == "Lainnya") {
                    Spacer(Modifier.height(12.dp))
                    TextField(
                        value = customTrigger,
                        onValueChange = { customTrigger = it },
                        placeholder = {
                            Text("Lainnya...", color = Color(0xFF7A8D92))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFD9F2E6), RoundedCornerShape(20.dp)),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color(0xFF105490),
                            fontSize = 16.sp
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFD9F2E6),
                            unfocusedContainerColor = Color(0xFFD9F2E6),
                            cursorColor = Color(0xFF105490)
                        )
                    )
                }

                Spacer(Modifier.height(12.dp))

                TextField(
                    value = diaryText,
                    onValueChange = { diaryText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .shadow(5.dp, RoundedCornerShape(20.dp))
                        .background(Color.White, RoundedCornerShape(20.dp)),
                    placeholder = {
                        Text(
                            "Type here...",
                            color = Color(0xFF7A8D92),
                            textAlign = TextAlign.Start
                        )
                    },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color(0xFF105490),
                        fontSize = 16.sp
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = Color(0xFF105490)
                    )
                )

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        showSavedPopup = true
                        scope.launch {
                            delay(2000)
                            showSavedPopup = false
                            navController.navigate("journal_list")
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(end = 10.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF105490))
                ) {
                    Text("Save", color = Color.White, fontSize = 16.sp)
                }

                Spacer(Modifier.height(25.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFFD9F2E6))
                    ) {
                        Icon(Icons.Filled.CameraAlt, null, tint = Color(0xFF105490))
                        Spacer(Modifier.width(6.dp))
                        Text("Camera", color = Color(0xFF105490))
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = {},
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFFD9F2E6))
                    ) {
                        Icon(Icons.Filled.Mic, null, tint = Color(0xFF105490))
                        Spacer(Modifier.width(6.dp))
                        Text("Voice", color = Color(0xFF105490))
                    }
                }

                Spacer(Modifier.height(50.dp))
            }
        }

        if (showSavedPopup) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color(0x88000000)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(Color(0xFFD0E4F2)),
                    modifier = Modifier
                        .padding(40.dp)
                        .fillMaxWidth(0.75f)
                ) {
                    Column(
                        Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF105490),
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Saved",
                            color = Color(0xFF105490),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
