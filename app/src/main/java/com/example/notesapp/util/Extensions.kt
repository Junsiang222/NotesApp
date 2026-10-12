package com.example.notesapp.util

import android.widget.EditText

fun EditText.textValue(): String = text?.toString().orEmpty()