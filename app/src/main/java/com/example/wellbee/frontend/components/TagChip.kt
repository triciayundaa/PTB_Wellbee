package com.example.wellbee.frontend.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wellbee.ui.theme.BluePrimary

@Composable
fun TagChip(
    text: String,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = { Text(text) },
        shape = RoundedCornerShape(12.dp),
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) BluePrimary.copy(alpha = 0.2f) else BluePrimary.copy(alpha = 0.08f),
            labelColor = if (selected) BluePrimary else BluePrimary.copy(alpha = 0.8f)
        ),
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}
