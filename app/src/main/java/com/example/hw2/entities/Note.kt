package com.example.hw2.entities

import androidx.room.*
import java.io.Serializable

@Entity(tableName = "notes")
class Note : Serializable {
    @PrimaryKey(autoGenerate = true)
    private var id: Int = 0

    @ColumnInfo(name = "date_time")
    private var dateTime: String = ""

    @ColumnInfo(name = "note_text")
    private var noteText: String = ""

    @ColumnInfo(name = "image_path")
    private var imagePath: String = ""

    @ColumnInfo(name = "symbols")
    private var symbols: String = ""

    fun getId(): Int {
        return id
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun getDateTime(): String {
        return dateTime
    }

    fun setDateTime(dateTime: String) {
        this.dateTime = dateTime
    }

    fun getNoteText(): String {
        return noteText
    }

    fun setNoteText(noteText: String) {
        this.noteText = noteText
    }

    fun getImagePath(): String {
        return imagePath
    }

    fun setImagePath(imgPath: String) {
        this.imagePath = imgPath
    }

    fun getSymbols(): String {
        return symbols
    }

    fun setSymbols(symbols: String) {
        this.symbols = symbols
    }

    override fun toString(): String {
        return noteText
    }
}