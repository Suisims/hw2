package com.example.hw2.listener

import com.example.hw2.entities.Note

interface NotesListener {
    fun onNoteClicked(note: Note, position: Int)
}