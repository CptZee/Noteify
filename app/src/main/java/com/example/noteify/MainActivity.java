package com.example.noteify;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.noteify.Database.Database;

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
}