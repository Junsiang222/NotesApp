package com.example.notesapp.util

import android.text.format.DateFormat
import android.widget.EditText

fun EditText.textValue(): String = text?.toString().orEmpty()

/** "Work, #study  ideas" becomes [work, study, ideas] */
fun String.toTagList(): List<String> =
    split(",", " ")
        .map { it.trim().removePrefix("#").lowercase() }
        .filter { it.isNotEmpty() }
        .distinct()

/** For the editor text field: "work, study" */
fun List<String>.toTagInput(): String = joinToString(", ")

/** For note cards: "#work  #study" */
fun List<String>.toHashtags(): String = joinToString("  ") { "#$it" }

fun Long.toDisplayDate(): String =
    DateFormat.format("d MMM", this).toString()