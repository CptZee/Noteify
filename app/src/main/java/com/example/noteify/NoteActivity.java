package com.example.noteify;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.noteify.Note.ListFragment;

public class NoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Log.i("FormLogger", "Now showing the main menu");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.note_container, new ListFragment())
                .commit();
    }
}