package com.example.smartpark.utils

import java.text.SimpleDateFormat
import java.util.*

fun Long.toFormattedDateTime(): String =
    try {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(this))
    } catch (_: Exception) {
        this.toString()
    }
