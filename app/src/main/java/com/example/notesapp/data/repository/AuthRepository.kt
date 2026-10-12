package com.example.notesapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    val isLoggedIn: Boolean
        get() = auth.currentUser != null

    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun register(name: String, email: String, password: String) {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val profile = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()
        result.user?.updateProfile(profile)?.await()
    }

    fun logout() {
        auth.signOut()
    }
}