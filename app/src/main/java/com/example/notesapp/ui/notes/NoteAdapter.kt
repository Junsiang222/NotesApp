package com.example.notesapp.ui.notes

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.R
import com.example.notesapp.data.model.Note
import com.example.notesapp.databinding.ItemNoteBinding
import com.example.notesapp.util.toDisplayDate
import com.example.notesapp.util.toHashtags

enum class NoteAction { OPEN, TOGGLE_PIN, TOGGLE_ARCHIVE, DELETE }

class NoteAdapter(
    private val onAction: (Note, NoteAction) -> Unit
) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(NoteDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NoteViewHolder(
        private val binding: ItemNoteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note) {
            val context = binding.root.context

            binding.titleText.text = note.title.ifBlank { context.getString(R.string.untitled) }
            binding.contentText.text = note.content
            binding.contentText.isVisible = note.content.isNotBlank()
            binding.tagsText.text = note.tags.toHashtags()
            binding.tagsText.isVisible = note.tags.isNotEmpty()
            binding.dateText.text = note.updatedAt.toDisplayDate()

            // Pin icon is amber when pinned, grey when not
            val pinColor = if (note.pinned) R.color.pin_active else R.color.pin_inactive
            binding.pinButton.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, pinColor))

            binding.root.setOnClickListener { onAction(note, NoteAction.OPEN) }
            binding.pinButton.setOnClickListener { onAction(note, NoteAction.TOGGLE_PIN) }
            binding.moreButton.setOnClickListener { showPopup(it, note) }
        }

        private fun showPopup(anchor: View, note: Note) {
            val popup = PopupMenu(anchor.context, anchor)
            popup.inflate(R.menu.menu_note_item)
            popup.menu.findItem(R.id.action_archive)
                .setTitle(if (note.archived) R.string.restore else R.string.archive)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_archive -> onAction(note, NoteAction.TOGGLE_ARCHIVE)
                    R.id.action_delete -> onAction(note, NoteAction.DELETE)
                }
                true
            }
            popup.show()
        }
    }

    private object NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Note, newItem: Note) = oldItem == newItem
    }
}