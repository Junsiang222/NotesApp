package com.example.notesapp.ui.auth

import com.example.notesapp.data.repository.AuthRepository
import com.example.notesapp.ui.base.BaseViewModel
import com.example.notesapp.util.Validators
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

data class AuthFieldErrors(
    val name: String? = null,
    val email: String? = null,
    val password: String? = null,
    val confirm: String? = null
) {
    val hasError: Boolean
        get() = name != null || email != null || password != null || confirm != null
}

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : BaseViewModel() {

    private val _fieldErrors = MutableStateFlow(AuthFieldErrors())
    val fieldErrors: StateFlow<AuthFieldErrors> = _fieldErrors.asStateFlow()

    private val successChannel = Channel<Unit>(Channel.BUFFERED)
    val authSuccess = successChannel.receiveAsFlow()

    fun login(email: String, password: String) {
        val errors = AuthFieldErrors(
            email = Validators.emailError(email),
            password = Validators.passwordError(password)
        )
        submit(errors) { repository.login(email.trim(), password) }
    }

    fun register(name: String, email: String, password: String, confirm: String) {
        val errors = AuthFieldErrors(
            name = Validators.nameError(name),
            email = Validators.emailError(email),
            password = Validators.passwordError(password),
            confirm = Validators.confirmError(password, confirm)
        )
        submit(errors) { repository.register(name.trim(), email.trim(), password) }
    }

    /** Check the input first, then call Firebase, then tell the screen it worked. */
    private fun submit(errors: AuthFieldErrors, action: suspend () -> Unit) {
        _fieldErrors.value = errors
        if (errors.hasError) return
        launchSafely {
            action()
            successChannel.send(Unit)
        }
    }
}