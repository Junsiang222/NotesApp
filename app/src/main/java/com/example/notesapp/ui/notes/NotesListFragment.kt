package com.example.notesapp.ui.notes

import androidx.fragment.app.viewModels
import com.example.notesapp.databinding.FragmentNotesListBinding
import com.example.notesapp.ui.base.BaseFragment

class NotesListFragment : BaseFragment<FragmentNotesListBinding>(FragmentNotesListBinding::inflate) {

    override val viewModel: NotesViewModel by viewModels()

    override fun setupUi() {
        // Notes list will be built in the next step
    }
}