package com.example.bookmanagement

import android.content.Context
import android.widget.Toast


//utility koltin page
fun showToast(context: Context?, message: String) {
    Toast.makeText(
        context,
        message,
        Toast.LENGTH_LONG
    ).show();
}

