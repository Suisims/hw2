package com.example.hw2.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.hw2.dao.NoteDao
import com.example.hw2.entities.Note

@Database(entities = [Note::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {
        var INSTANCE: NoteDatabase? = null

        fun getAppDataBase(context: Context): NoteDatabase? {
            if (INSTANCE == null) {
                synchronized(NoteDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        NoteDatabase::class.java,
                        "note_db"
                    ).build()
                }
            }
            return INSTANCE
        }
        fun destroyDataBase() {
            INSTANCE = null
        }
    }
}