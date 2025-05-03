package com.example.utils

import android.widget.EditText

fun EditText.toTrimString(): String {
    return this.text.toString().trim()
}