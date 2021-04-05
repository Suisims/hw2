package com.example.hw2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hw2.R
import com.example.hw2.entities.Note
import com.example.hw2.listener.NotesListener

class NotesAdapter(private var notes: List<Note>, private var notesListener: NotesListener) :
    RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {


    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var noteText: TextView? = itemView.findViewById(R.id.note_text)
        private var noteDate: TextView? = itemView.findViewById(R.id.note_date)
        var layoutNote: LinearLayout? = itemView.findViewById(R.id.layoutNote)

        fun setNote(note: Note) {
            noteText?.text = note.getNoteText()
            noteDate?.text = note.getDateTime()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_container_note, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.setNote(notes[position])
        holder.layoutNote?.setOnClickListener {
            notesListener.onNoteClicked(notes[position], position)
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}