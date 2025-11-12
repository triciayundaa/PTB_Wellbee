package com.example.wellbee.frontend.components

import android.app.DatePickerDialog
import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

fun showDatePicker(
    context: Context,
    onDateSelected: (String) -> Unit,
    format: String = "dd/MM/yyyy"
) {
    val calendar = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, dayOfMonth)

            val sdf = SimpleDateFormat(format, Locale("id", "ID"))
            val formattedDate = sdf.format(selectedCalendar.time)
            onDateSelected(formattedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}
