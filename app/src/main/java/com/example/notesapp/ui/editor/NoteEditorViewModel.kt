package com.example.notesapp.ui.editor

import com.example.notesapp.data.model.Note
import com.example.notesapp.data.repository.NoteRepository
import com.example.notesapp.ui.base.BaseViewModel
import com.example.notesapp.ui.common.NoteColour
import com.example.notesapp.util.toTagList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class NoteEditorViewModel(
    private val repository: NoteRepository = NoteRepository()
) : BaseViewModel() {

    // The note being edited (stays empty for a brand new note)
    private var currentNote = Note()
    private var loadedId: String? = null

    private val _colour = MutableStateFlow(NoteColour.BLUE)
    val colour: StateFlow<NoteColour> = _colour.asStateFlow()

    private val loadedChannel = Channel<Note>(Channel.BUFFERED)
    val loadedNote = loadedChannel.receiveAsFlow()

    private val savedChannel = Channel<Unit>(Channel.BUFFERED)
    val saved = savedChannel.receiveAsFlow()

    fun selectColour(colour: NoteColour) {
        _colour.value = colour
    }

    fun load(noteId: String) {
        if (noteId.isEmpty() || noteId == loadedId) return
        loadedId = noteId
        launchSafely {
            val note = repository.getNote(noteId)
            if (note == null) {
                postMessage("Note not found")
            } else {
                currentNote = note
                _colour.value = NoteColour.fromKey(note.colour)
                loadedChannel.send(note)
            }
        }
    }

    fun save(title: String, content: String, tagsInput: String) {
        if (title.isBlank() && content.isBlank()) {
            postMessage("Write a title or some content first")
            return
        }
        val note = currentNote.copy(
            title = title.trim(),
            content = content.trim(),
            tags = tagsInput.toTagList(),
            colour = _colour.value.key
        )
        launchSafely {
            repository.saveNote(note)
            savedChannel.send(Unit)
        }
    }
}