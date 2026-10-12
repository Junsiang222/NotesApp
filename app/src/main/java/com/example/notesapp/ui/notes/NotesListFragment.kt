package com.example.notesapp.ui.notes

import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.example.notesapp.R
import com.example.notesapp.databinding.FragmentNotesListBinding
import com.example.notesapp.ui.base.BaseFragment
import com.example.notesapp.ui.editor.NoteEditorFragment

class NotesListFragment : BaseFragment<FragmentNotesListBinding>(FragmentNotesListBinding::inflate) {

    override val viewModel: NotesViewModel by viewModels()

    private val adapter = NoteAdapter { note -> openEditor(note.id) }

    override fun setupUi() {
        binding.notesRecycler.adapter = adapter

        binding.toolbar.inflateMenu(R.menu.menu_notes)
        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_logout) {
                viewModel.logout()
                navigate(R.id.action_list_to_login)
                true
            } else {
                false
            }
        }

        binding.addFab.setOnClickListener { openEditor(noteId = "") }
    }

    override fun observeViewModel() {
        observe(viewModel.uiState) { state ->
            adapter.submitList(state.notes)
            binding.progress.isVisible = state.isLoading
            binding.emptyText.setText(R.string.empty_notes)
            binding.emptyText.isVisible = !state.isLoading && state.notes.isEmpty()
        }
    }

    private fun openEditor(noteId: String) {
        navigate(R.id.action_list_to_editor, bundleOf(NoteEditorFragment.ARG_NOTE_ID to noteId))
    }
}