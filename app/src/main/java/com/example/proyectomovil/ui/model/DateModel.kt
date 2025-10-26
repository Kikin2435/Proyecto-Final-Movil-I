
package com.example.proyectomovil.ui.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatTimestampToDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

fun formatTimestampToTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
