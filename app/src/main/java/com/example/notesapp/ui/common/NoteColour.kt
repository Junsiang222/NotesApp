package com.example.notesapp.ui.common

import androidx.annotation.ColorRes
import com.example.notesapp.R

enum class NoteColour(val key: String, @ColorRes val colorRes: Int) {
    BLUE("blue", R.color.note_blue),
    GREEN("green", R.color.note_green),
    YELLOW("yellow", R.color.note_yellow),
    ORANGE("orange", R.color.note_orange),
    PINK("pink", R.color.note_pink),
    PURPLE("purple", R.color.note_purple),
    WHITE("white", R.color.note_white);

    companion object {
        fun fromKey(key: String): NoteColour = entries.firstOrNull { it.key == key } ?: BLUE
    }
}