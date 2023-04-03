package com.example.noteify.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import com.example.noteify.Data.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteHelper extends SQLiteOpenHelper {
    public NoteHelper(Context context) {
        super(context, "db_Noteify", null, 1);
    }

    private final String TABLENAME = "tbl_Notes";

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLENAME + "(" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "ownerID INTEGER," +
                    "title TEXT," +
                    "text TEXT," +
                    "archived INTEGER)");
        }catch (SQLiteException e){
            Log.e("DB_Helper", "Unable to create " + TABLENAME, e.getCause());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        try{
            db.execSQL("DROP TABLE " + TABLENAME);
            onCreate(db);
        }catch (SQLiteException e){
            Log.e("DB_Helper", "Unable to create " + TABLENAME, e.getCause());
        }
    }

    public List<Note> get(){
        SQLiteDatabase db = getReadableDatabase();
        List<Note> list = new ArrayList<>();
        try{
            Cursor cursor = db.rawQuery("SELECT ID, ownerID, title, text FROM " + TABLENAME + " WHERE archived = ?",
                    new String[]{String.valueOf(0)});
            while (cursor.moveToNext())
                list.add(prepareData(cursor));
        }catch (SQLiteException e){
            Log.e("DB_Helper", "Unable to retrieve data from the " + TABLENAME, e.getCause());
        }
        return list;
    }

    public Note get(int ID){
        SQLiteDatabase db = getReadableDatabase();
        Note data = null;
        try{
            Cursor cursor = db.rawQuery("SELECT ID, ownerID, title, text FROM " + TABLENAME + " WHERE ID = ?",
                    new String[]{String.valueOf(ID)});
            while (cursor.moveToNext()) {
                data = prepareData(cursor);
            };
        }catch (SQLiteException e){
            Log.e("DB_Helper", "Unable to retrieve data from the " + TABLENAME, e.getCause());
        }
        return data;
    }

    public Note get(String title){
        SQLiteDatabase db = getReadableDatabase();
        Note data = null;
        try{
            Cursor cursor = db.rawQuery("SELECT ID, ownerID, title, text FROM " + TABLENAME + " WHERE title = ?",
                    new String[]{title});
            while (cursor.moveToNext())
                data = prepareData(cursor);
        }catch (SQLiteException e){
            Log.e("DB_Helper", "Unable to retrieve data from the " + TABLENAME, e.getCause());
        }
        return data;
    }

    public void insert(Note data){
        new insertTask().execute(new DataParam(getWritableDatabase(), data, prepareData(data)));
    }

    public void update(Note data){
        new updateTask().execute(new DataParam(getWritableDatabase(), data, prepareData(data)));
    }

    public void remove(Note data){
        new removeTask().execute(new DataParam(getWritableDatabase(), data, prepareData(data)));
    }

    public int getID(){
        SQLiteDatabase db = getReadableDatabase();
        int data = 0;
        try{
            Cursor cursor = db.rawQuery("SELECT MAX(ID) FROM " + TABLENAME,
                    null);
            while (cursor.moveToNext())
                data = cursor.getInt(0);
            data++;
        }catch (SQLiteException e){
            Log.e("DB_Helper", "Unable to retrieve data from the " + TABLENAME, e.getCause());
        }
        return data;
    }

    private ContentValues prepareData(Note data){
        ContentValues content = new ContentValues();
        if(data.getOwnerID() != 0)
            content.put("ownerID", data.getOwnerID());
        if(data.getTitle() != null)
            content.put("title", data.getTitle());
        if (data.getText() != null)
            content.put("text", data.getText());
        content.put("archived", 0);
        return content;
    }

    private Note prepareData(Cursor cursor){
        Note data = new Note();
        data.setID(cursor.getInt(0));
        data.setOwnerID(cursor.getInt(1));
        data.setTitle(cursor.getString(2));
        data.setText(cursor.getString(3));
        return data;
    }

    private class DataParam{
        private SQLiteDatabase db;
        private Note data;
        private ContentValues content;
        public DataParam(SQLiteDatabase db, Note data, ContentValues content) {
            this.db = db;
            this.data = data;
            this.content = content;
        }
    }

    private class insertTask extends AsyncTask<DataParam, Void, Void> {
        @Override
        protected Void doInBackground(DataParam... dataParams) {
            DataParam param = dataParams[0];
            SQLiteDatabase db = param.db;
            ContentValues content = param.content;
            Log.i("DB_Helper", "Data to insert has the title of " + content.get("title"));
            try{
                db.insert(TABLENAME, null, content);
                Log.i("DB_Helper", "Successfully inserted into the " + TABLENAME);
            }catch (Exception e){
                Log.e("DB_Helper", "Unable to insert data into " + TABLENAME, e.getCause());
            }
            db.close();
            return null;
        }
    }
    private class updateTask extends AsyncTask<DataParam, Void, Void>{
        @Override
        protected Void doInBackground(DataParam... dataParams) {
            DataParam param = dataParams[0];
            SQLiteDatabase db = param.db;
            Note data = param.data;
            ContentValues content = param.content;
            try{
                db.update(TABLENAME, content, "ID = ?",
                        new String[]{String.valueOf(data.getID())});
            }catch (SQLiteException e){
                Log.e("DB_Helper", "Unable to update data from the " + TABLENAME, e.getCause());
            }
            db.close();
            return null;
        }
    }
    private class removeTask extends AsyncTask<DataParam, Void, Void> {
        @Override
        protected Void doInBackground(DataParam... dataParams) {
            DataParam param = dataParams[0];
            SQLiteDatabase db = param.db;
            Note data = param.data;
            ContentValues content = param.content;
            content.put("archived", 1);
            try{
                db.update(TABLENAME, content, "ID = ?",
                        new String[]{String.valueOf(data.getID())});
            }catch (SQLiteException e){
                Log.e("DB_Helper", "Unable to remove data from the " + TABLENAME, e.getCause());
            }
            db.close();
            return null;
        }
    }
}
