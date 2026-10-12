package com.example.notesapp.ui.editor

import com.example.notesapp.data.model.Note
import com.example.notesapp.data.repository.NoteRepository
import com.example.notesapp.ui.base.BaseViewModel
import com.example.notesapp.util.toTagList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class NoteEditorViewModel(
    private val repository: NoteRepository = NoteRepository()
) : BaseViewModel() {

    // The note being edited (stays empty for a brand new note)
    private var currentNote = Note()
    private var loadedId: String? = null

    private val loadedChannel = Channel<Note>(Channel.BUFFERED)
    val loadedNote = loadedChannel.receiveAsFlow()

    private val savedChannel = Channel<Unit>(Channel.BUFFERED)
    val saved = savedChannel.receiveAsFlow()

    fun load(noteId: String) {
        if (noteId.isEmpty() || noteId == loadedId) return
        loadedId = noteId
        launchSafely {
            val note = repository.getNote(noteId)
            if (note == null) {
                postMessage("Note not found")
            } else {
                currentNote = note
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
            tags = tagsInput.toTagList()
        )
        launchSafely {
            repository.saveNote(note)
            savedChannel.send(Unit)
        }
    }
}