package com.example.hw2.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.hw2.R
import com.example.hw2.adapters.NotesAdapter
import com.example.hw2.database.NoteDatabase
import com.example.hw2.entities.Note
import com.example.hw2.listener.NotesListener


class MainActivity : AppCompatActivity(), NotesListener {

    private lateinit var notesRecyclerView: RecyclerView
    private lateinit var noteList: List<Note>
    private lateinit var notesAdapter: NotesAdapter
    private var noteClickedPosition: Int = -1

    private var REQUEST_CODE_ADD_NOTE: Int = 1
    private var REQUEST_CODE_UPDATE_NOTE: Int = 2
    private var REQUEST_CODE_SHOW_NOTES: Int = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val spinner = findViewById<Spinner>(R.id.spinner)
        val arrayAdapter = ArrayAdapter(
            this,
            R.layout.spinner_layout, resources.getStringArray(R.array.toolbar)
        )
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter


        // add note
        findViewById<Button>(R.id.add_note).setOnClickListener {
            val intent = Intent(applicationContext, CreateNoteActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_NOTE)
        }

        notesRecyclerView = findViewById(R.id.notes)
        notesRecyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)


        val itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // left
                if (direction == 4) {
                    if (viewHolder.adapterPosition % 2 == 0) {
                        deleteNote(viewHolder.adapterPosition)
                    } else {
                        getNotes(REQUEST_CODE_SHOW_NOTES)
                    }
                // right
                } else if (direction == 8) {
                    if (viewHolder.adapterPosition % 2 == 1) {
                        deleteNote(viewHolder.adapterPosition)
                    } else {
                        getNotes(REQUEST_CODE_SHOW_NOTES)
                    }
                } else {
                    getNotes(REQUEST_CODE_SHOW_NOTES)
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(notesRecyclerView)

        noteList = ArrayList()
        notesAdapter = NotesAdapter(noteList, this)
        notesRecyclerView.adapter = notesAdapter

        getNotes(REQUEST_CODE_SHOW_NOTES)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 1 && requestCode == REQUEST_CODE_ADD_NOTE) {
            getNotes(REQUEST_CODE_ADD_NOTE)
        } else if (requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == 1) {
            if (data != null) {
                getNotes(REQUEST_CODE_UPDATE_NOTE)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun getNotes(requestCode: Int) {

        @SuppressLint("StaticFieldLeak")
        class GetNotesTask : AsyncTask<Void, Void, List<Note>>() {
            override fun doInBackground(vararg params: Void?): List<Note>? {
                return NoteDatabase.getAppDataBase(applicationContext)?.noteDao()?.getAllNotes()
            }

            override fun onPostExecute(notes: List<Note>?) {
                when (requestCode) {
                    REQUEST_CODE_SHOW_NOTES -> {
                        (noteList as MutableList<Note>).clear()
                        if (notes != null) {
                            (noteList as MutableList<Note>).addAll(notes)
                        }
                        notesAdapter.notifyDataSetChanged()
                    }
                    REQUEST_CODE_ADD_NOTE -> {
                        (noteList as MutableList<Note>).add(0, notes?.get(0)!!)
                        notesAdapter.notifyItemInserted(0)
                        notesRecyclerView.smoothScrollToPosition(0)
                    }
                    REQUEST_CODE_UPDATE_NOTE -> {
                        (noteList as MutableList<Note>).removeAt(noteClickedPosition)
                        if (notes != null) {
                            (noteList as MutableList<Note>).add(
                                noteClickedPosition,
                                notes[noteClickedPosition]
                            )
                        }
                        notesAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
        GetNotesTask().execute()
    }

    private fun deleteNote(position: Int) {
        @SuppressLint("StaticFieldLeak")
        class DeleteNoteTask : AsyncTask<Void, Void, List<Note>>() {
            override fun doInBackground(vararg params: Void?): List<Note>? {
                NoteDatabase.getAppDataBase(applicationContext)?.noteDao()
                    ?.deleteNote(noteList[position])
                return null
            }

            override fun onPostExecute(notes: List<Note>?) {
                getNotes(REQUEST_CODE_SHOW_NOTES)
            }
        }
        DeleteNoteTask().execute()
    }

    override fun onNoteClicked(note: Note, position: Int) {
        noteClickedPosition = position
        val intent = Intent(applicationContext, CreateNoteActivity::class.java)
        intent.putExtra("isViewOrUpdate", true)
        intent.putExtra("note", note)
        startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE)
    }
}
