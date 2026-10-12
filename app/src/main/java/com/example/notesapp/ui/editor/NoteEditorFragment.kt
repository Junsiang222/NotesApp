package com.example.notesapp.ui.editor

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.notesapp.R
import com.example.notesapp.data.model.Note
import com.example.notesapp.databinding.FragmentNoteEditorBinding
import com.example.notesapp.ui.base.BaseFragment
import com.example.notesapp.util.textValue
import com.example.notesapp.util.toTagInput

class NoteEditorFragment : BaseFragment<FragmentNoteEditorBinding>(FragmentNoteEditorBinding::inflate) {

    override val viewModel: NoteEditorViewModel by viewModels()

    override fun setupUi() {
        val noteId = arguments?.getString(ARG_NOTE_ID).orEmpty()
        viewModel.load(noteId)

        binding.toolbar.setTitle(if (noteId.isEmpty()) R.string.new_note else R.string.edit_note)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.saveButton.setOnClickListener {
            viewModel.save(
                title = binding.titleInput.textValue(),
                content = binding.contentInput.textValue(),
                tagsInput = binding.tagsInput.textValue()
            )
        }
    }

    override fun observeViewModel() {
        observe(viewModel.loadedNote) { fillForm(it) }
        observe(viewModel.saved) { findNavController().popBackStack() }
    }

    private fun fillForm(note: Note) {
        binding.titleInput.setText(note.title)
        binding.contentInput.setText(note.content)
        binding.tagsInput.setText(note.tags.toTagInput())
    }

    override fun onLoadingChanged(loading: Boolean) {
        binding.progress.isVisible = loading
        binding.saveButton.isEnabled = !loading
    }

    companion object {
        const val ARG_NOTE_ID = "noteId"
    }
}