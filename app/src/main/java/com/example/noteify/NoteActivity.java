package com.example.noteify;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
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
    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to close the application?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    this.finish();
                })
                .setNegativeButton("No", (dialog, id) -> {

                }).create().show();
    }
}