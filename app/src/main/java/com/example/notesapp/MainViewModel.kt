package com.example.notesapp

import androidx.lifecycle.ViewModel
import com.example.notesapp.data.repository.AuthRepository

class MainViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {
    val isLoggedIn: Boolean
        get() = authRepository.isLoggedIn
}