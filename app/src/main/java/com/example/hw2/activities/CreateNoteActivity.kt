package com.example.hw2.activities

import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.hw2.R
import com.example.hw2.database.NoteDatabase
import com.example.hw2.entities.Note
import java.util.*

class CreateNoteActivity : AppCompatActivity() {

    private var mSymbols: TextView? = null
    private var mNoteText: EditText? = null
    private var mTime: TextView? = null
    private var defaultNote: Note? = null
    private var time: String? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_createnote)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        mTime = findViewById(R.id.time)

        val currentDateTime = LocalDateTime.now()
        mTime?.text = currentDateTime.format(
            DateTimeFormatter.ofPattern(
                "dd MMMM HH:mm",
                Locale.getDefault()
            )
        )
        time = currentDateTime.format(
            DateTimeFormatter.ofPattern(
                "dd MMMM HH:mm",
                Locale.getDefault()
            )
        ).toString()

        mSymbols = findViewById(R.id.symbols)

        findViewById<EditText>(R.id.text).addTextChangedListener(mTextEditorWatcher)
        findViewById<Button>(R.id.back).setOnClickListener {
            onBackPressed()
        }
        mNoteText = findViewById(R.id.text)

        findViewById<Button>(R.id.add).setOnClickListener {
            saveNote()
        }

        if (intent.getBooleanExtra("isViewOrUpdate", false)) {
            defaultNote = intent.getSerializableExtra("note") as Note?
            setViewOrUpdate()
        }
    }

    private fun setViewOrUpdate() {
        mSymbols?.text = defaultNote?.getSymbols()
        mNoteText?.setText(defaultNote?.getNoteText())
        mTime?.text = defaultNote?.getDateTime()
    }

    private val mTextEditorWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val symbols = s.length.toString() + " символов"
            mSymbols!!.text = symbols
        }

        override fun afterTextChanged(s: Editable) {}
    }

    private fun saveNote() {
        if (mNoteText?.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Note is empty", Toast.LENGTH_SHORT).show()
            return
        }
        val note = Note()
        note.setNoteText(mNoteText?.text.toString())
        note.setDateTime(time.toString())

        if (defaultNote != null) {
            note.setId(defaultNote!!.getId())
        }

        class SaveNoteTask : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg params: Void?): Void? {
                NoteDatabase.getAppDataBase(applicationContext)?.noteDao()?.insertNote(note)
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                val intent = Intent(applicationContext, MainActivity::class.java)
                setResult(1, intent)
                finish()
            }
        }
        SaveNoteTask().execute()
    }
}