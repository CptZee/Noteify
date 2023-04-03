package com.example.noteify;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;

import com.example.noteify.Database.Database;
import com.example.noteify.Database.NoteHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Database db = new Database(getApplicationContext());
        db.initDatabase();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.auth_container, new SplashFragment())
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