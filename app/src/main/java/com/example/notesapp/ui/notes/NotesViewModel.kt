package com.example.notesapp.ui.notes

import androidx.lifecycle.viewModelScope
import com.example.notesapp.data.model.Note
import com.example.notesapp.data.repository.AuthRepository
import com.example.notesapp.data.repository.NoteRepository
import com.example.notesapp.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class NotesUiState(
    val notes: List<Note> = emptyList(),
    val tags: List<String> = emptyList(),
    val selectedTag: String? = null,
    val query: String = "",
    val showArchived: Boolean = false,
    val isLoading: Boolean = true
) {
    val isFiltering: Boolean get() = query.isNotBlank() || selectedTag != null
}

class NotesViewModel(
    private val noteRepository: NoteRepository = NoteRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : BaseViewModel() {

    // null = still waiting for the first answer from Firestore
    private val allNotes = MutableStateFlow<List<Note>?>(null)
    private val query = MutableStateFlow("")
    private val selectedTag = MutableStateFlow<String?>(null)
    private val showArchived = MutableStateFlow(false)

    // Combine everything: filter by archive/tag/search, then pinned notes first
    val uiState: StateFlow<NotesUiState> = combine(
        allNotes, query, selectedTag, showArchived
    ) { notes, q, tag, archived ->
        val inScope = notes.orEmpty().filter { it.archived == archived }
        val visible = inScope
            .filter { note -> tag == null || tag in note.tags }
            .filter { note -> matchesQuery(note, q) }
            .sortedWith(compareByDescending<Note> { it.pinned }.thenByDescending { it.updatedAt })

        NotesUiState(
            notes = visible,
            tags = inScope.flatMap { it.tags }.distinct().sorted(),
            selectedTag = tag,
            query = q,
            showArchived = archived,
            isLoading = notes == null
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NotesUiState())

    init {
        viewModelScope.launch {
            noteRepository.observeNotes()
                .catch { postMessage(it.localizedMessage ?: "Could not load notes") }
                .collect { allNotes.value = it }
        }
    }

    private fun matchesQuery(note: Note, query: String): Boolean =
        query.isBlank() ||
                note.title.contains(query, ignoreCase = true) ||
                note.content.contains(query, ignoreCase = true)

    fun setQuery(text: String) {
        query.value = text
    }

    fun selectTag(tag: String?) {
        selectedTag.value = tag
    }

    fun toggleArchivedView() {
        showArchived.value = !showArchived.value
        selectedTag.value = null
    }

    fun togglePin(note: Note) = launchSafely(showLoading = false) {
        noteRepository.setPinned(note.id, !note.pinned)
    }

    fun toggleArchive(note: Note) = launchSafely(showLoading = false) {
        noteRepository.setArchived(note.id, !note.archived)
        postMessage(if (note.archived) "Note restored" else "Note archived")
    }

    fun delete(note: Note) = launchSafely(showLoading = false) {
        noteRepository.deleteNote(note.id)
        postMessage("Note deleted")
    }

    fun logout() = authRepository.logout()
}