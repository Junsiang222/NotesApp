package com.example.notesapp.ui.common

import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.example.notesapp.R
import com.example.notesapp.databinding.ItemColourDotBinding

/** Draws one round button per colour. The selected one gets a thick dark border. */
fun LinearLayout.renderColourDots(
    inflater: LayoutInflater,
    colours: List<NoteColour>,
    selected: NoteColour?,
    onClick: (NoteColour) -> Unit
) {
    removeAllViews()
    val density = resources.displayMetrics.density
    colours.forEach { colour ->
        val dot = ItemColourDotBinding.inflate(inflater, this, false).root
        val isSelected = colour == selected
        dot.setCardBackgroundColor(ContextCompat.getColor(context, colour.colorRes))
        dot.strokeColor = ContextCompat.getColor(
            context,
            if (isSelected) R.color.text_primary else R.color.border
        )
        dot.strokeWidth = ((if (isSelected) 3 else 1) * density).toInt()
        dot.contentDescription = colour.key
        dot.setOnClickListener { onClick(colour) }
        addView(dot)
    }
}