package com.example.notesapp.ui.notes

import android.view.MenuItem
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.example.notesapp.R
import com.example.notesapp.data.model.Note
import com.example.notesapp.databinding.FragmentNotesListBinding
import com.example.notesapp.databinding.ItemTagChipBinding
import com.example.notesapp.ui.base.BaseFragment
import com.example.notesapp.ui.editor.NoteEditorFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class NotesListFragment : BaseFragment<FragmentNotesListBinding>(FragmentNotesListBinding::inflate) {

    override val viewModel: NotesViewModel by viewModels()

    private val adapter = NoteAdapter { note, action -> onNoteAction(note, action) }

    override fun setupUi() {
        binding.notesRecycler.adapter = adapter

        binding.toolbar.inflateMenu(R.menu.menu_notes)
        binding.toolbar.setOnMenuItemClickListener { onMenuItem(it) }

        binding.searchInput.doAfterTextChanged { viewModel.setQuery(it?.toString().orEmpty()) }
        binding.addFab.setOnClickListener { openEditor(noteId = "") }
    }

    override fun observeViewModel() {
        observe(viewModel.uiState) { render(it) }
        // Only rebuild the tag chips when the tags or the selected tag change
        observe(viewModel.uiState.map { it.tags to it.selectedTag }.distinctUntilChanged()) { (tags, selected) ->
            renderTagChips(tags, selected)
        }
    }

    private fun render(state: NotesUiState) {
        adapter.submitList(state.notes)

        val titleRes = if (state.showArchived) R.string.archive_title else R.string.notes_title
        val switchRes = if (state.showArchived) R.string.notes_title else R.string.archive_title
        binding.toolbar.setTitle(titleRes)
        binding.toolbar.menu.findItem(R.id.action_toggle_archive)?.setTitle(switchRes)

        binding.progress.isVisible = state.isLoading
        binding.addFab.isVisible = !state.showArchived

        val emptyMessage = when {
            state.isFiltering -> R.string.empty_no_match
            state.showArchived -> R.string.empty_archive
            else -> R.string.empty_notes
        }
        binding.emptyText.setText(emptyMessage)
        binding.emptyText.isVisible = !state.isLoading && state.notes.isEmpty()
    }

    private fun renderTagChips(tags: List<String>, selectedTag: String?) {
        binding.tagChipGroup.removeAllViews()
        binding.tagScroll.isVisible = tags.isNotEmpty()

        addTagChip(getString(R.string.all_tags), selectedTag == null) { viewModel.selectTag(null) }
        tags.forEach { tag ->
            addTagChip("#$tag", tag == selectedTag) { viewModel.selectTag(tag) }
        }
    }

    private fun addTagChip(label: String, selected: Boolean, onClick: () -> Unit) {
        val chip = ItemTagChipBinding.inflate(layoutInflater, binding.tagChipGroup, false).root
        chip.text = label
        chip.isChecked = selected
        chip.setOnClickListener { onClick() }
        binding.tagChipGroup.addView(chip)
    }

    private fun onMenuItem(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_toggle_archive -> {
            viewModel.toggleArchivedView()
            true
        }
        R.id.action_logout -> {
            viewModel.logout()
            navigate(R.id.action_list_to_login)
            true
        }
        else -> false
    }

    private fun onNoteAction(note: Note, action: NoteAction) {
        when (action) {
            NoteAction.OPEN -> openEditor(note.id)
            NoteAction.TOGGLE_PIN -> viewModel.togglePin(note)
            NoteAction.TOGGLE_ARCHIVE -> viewModel.toggleArchive(note)
            NoteAction.DELETE -> confirmDelete(note)
        }
    }

    private fun openEditor(noteId: String) {
        navigate(R.id.action_list_to_editor, bundleOf(NoteEditorFragment.ARG_NOTE_ID to noteId))
    }

    private fun confirmDelete(note: Note) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_title)
            .setMessage(R.string.delete_message)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ -> viewModel.delete(note) }
            .show()
    }
}