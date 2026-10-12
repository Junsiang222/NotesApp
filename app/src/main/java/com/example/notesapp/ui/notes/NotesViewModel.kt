package com.example.notesapp.ui.notes

import androidx.lifecycle.viewModelScope
import com.example.notesapp.data.model.Note
import com.example.notesapp.data.repository.AuthRepository
import com.example.notesapp.data.repository.NoteRepository
import com.example.notesapp.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class NotesUiState(
    val notes: List<Note> = emptyList(),
    val isLoading: Boolean = true
)

class NotesViewModel(
    private val noteRepository: NoteRepository = NoteRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            noteRepository.observeNotes()
                .catch {
                    postMessage(it.localizedMessage ?: "Could not load notes")
                    _uiState.value = NotesUiState(isLoading = false)
                }
                .collect { notes ->
                    _uiState.value = NotesUiState(notes = notes, isLoading = false)
                }
        }
    }

    fun logout() = authRepository.logout()
}