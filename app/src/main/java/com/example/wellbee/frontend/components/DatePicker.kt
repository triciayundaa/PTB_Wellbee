package com.example.wellbee.frontend.components

import android.app.DatePickerDialog
import android.content.Context
import java.util.*

fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()

    val dialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            // Format MySQL yg benar → yyyy-MM-dd
            val mysqlDate = String.format("%04d-%02d-%02d", year, month + 1, day)
            onDateSelected(mysqlDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    dialog.show()
}

