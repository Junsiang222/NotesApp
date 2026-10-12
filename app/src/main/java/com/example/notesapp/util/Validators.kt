package com.example.notesapp.util

import android.util.Patterns

object Validators {

    private const val MIN_PASSWORD_LENGTH = 6

    fun nameError(name: String): String? =
        if (name.isBlank()) "Name is required" else null

    fun emailError(email: String): String? = when {
        email.isBlank() -> "Email is required"
        !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> "Enter a valid email"
        else -> null
    }

    fun passwordError(password: String): String? = when {
        password.isEmpty() -> "Password is required"
        password.length < MIN_PASSWORD_LENGTH -> "At least $MIN_PASSWORD_LENGTH characters"
        else -> null
    }

    fun confirmError(password: String, confirm: String): String? =
        if (password != confirm) "Passwords do not match" else null
}