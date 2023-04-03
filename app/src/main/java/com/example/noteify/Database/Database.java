package com.example.noteify.Database;

import android.content.Context;
import android.os.AsyncTask;

public class Database {
    private Context context;

    public Database(Context context) {
        this.context = context;
    }
    public void initDatabase(){
        new initDatabaseTask().execute();
    }

    private class initDatabaseTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            NoteHelper noteHelper = new NoteHelper(context);
            UserHelper userHelper = new UserHelper(context);

            noteHelper.onCreate(noteHelper.getWritableDatabase());
            userHelper.onCreate(userHelper.getWritableDatabase());
            return null;
        }
    }
}
