package com.example.notesapp.data.repository

import com.example.notesapp.data.model.Note
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NoteRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    // Each user's notes live in users/{uid}/notes
    private fun notesCollection(): CollectionReference {
        val uid = auth.currentUser?.uid ?: error("You are not logged in")
        return db.collection("users").document(uid).collection("notes")
    }

    /** Live list of notes: updates automatically whenever Firestore changes. */
    fun observeNotes(): Flow<List<Note>> = callbackFlow {
        val registration = notesCollection()
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val notes = snapshot?.documents
                    ?.mapNotNull { doc -> doc.toObject(Note::class.java)?.copy(id = doc.id) }
                    .orEmpty()
                trySend(notes)
            }
        awaitClose { registration.remove() }
    }

    suspend fun getNote(noteId: String): Note? {
        val doc = notesCollection().document(noteId).get().await()
        return doc.toObject(Note::class.java)?.copy(id = doc.id)
    }

    /** Creates a new note when id is empty, otherwise updates the existing one. */
    suspend fun saveNote(note: Note) {
        val now = System.currentTimeMillis()
        val collection = notesCollection()
        if (note.id.isEmpty()) {
            collection.add(note.copy(createdAt = now, updatedAt = now)).await()
        } else {
            collection.document(note.id).set(note.copy(updatedAt = now)).await()
        }
    }

    suspend fun setPinned(noteId: String, pinned: Boolean) {
        updateFields(noteId, mapOf("pinned" to pinned))
    }

    suspend fun setArchived(noteId: String, archived: Boolean) {
        updateFields(
            noteId,
            mapOf("archived" to archived, "updatedAt" to System.currentTimeMillis())
        )
    }

    suspend fun deleteNote(noteId: String) {
        notesCollection().document(noteId).delete().await()
    }

    private suspend fun updateFields(noteId: String, fields: Map<String, Any>) {
        notesCollection().document(noteId).update(fields).await()
    }
}