package com.madgeeks.floorcounter.Utils

import android.app.PendingIntent
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.madgeeks.floorcounter.R
import java.time.LocalDateTime

fun log(string: String) {
    println("${LocalDateTime.now()}: $string")
}

fun getPendingIntentFlag(): Int = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_ONE_SHOT

fun AppCompatActivity.showMissingPermissionsSnackbar() {
    Snackbar.make(
        findViewById(R.id.container),
        "Please grant permission from App Settings",
        Snackbar.LENGTH_LONG
    ).show()
}