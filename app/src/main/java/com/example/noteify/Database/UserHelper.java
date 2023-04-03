package com.example.noteify.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import com.example.noteify.Data.User;

import java.util.ArrayList;
import java.util.List;

public class UserHelper extends SQLiteOpenHelper {
    public UserHelper(Context context) {
        super(context, "db_Noteify", null, 1);
    }

    private final String TABLENAME = "tbl_Users";

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLENAME + "(" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT," +
                    "password TEXT," +
                    "isAdmin INTEGER," +
                    "archived INTEGER)");
        }catch (SQLiteException e){
            Log.e("DB_Helper", "Unable to create " + TABLENAME, e.getCause());
        }
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        try{
            db.execSQL("DROP TABLE " + TABLENAME);
            onCreate(db);
        }catch (SQLiteException e){
            Log.e("DB_Helper", "Unable to create " + TABLENAME, e.getCause());
        }
        db.close();
    }

    public List<User> get(){
        SQLiteDatabase db = getReadableDatabase();
        List<User> list = new ArrayList<>();
        try{
            Cursor cursor = db.rawQuery("SELECT ID, username, password, isAdmin FROM " + TABLENAME + " WHERE archived = ?",
                    new String[]{String.valueOf(0)});
            while (cursor.moveToNext())
                list.add(prepareData(cursor));
        }catch (SQLiteException e){
            Log.e("DB_Helper", "Unable to retrieve data from the " + TABLENAME, e.getCause());
        }
        return list;
    }

    public User get(int ID){
        SQLiteDatabase db = getReadableDatabase();
        User data = null;
        try{
            Cursor cursor = db.rawQuery("SELECT ID, username, password, isAdmin FROM " + TABLENAME + " WHERE ID = ?",
                    new String[]{String.valueOf(ID)});
            while (cursor.moveToNext())
                data = prepareData(cursor);
        }catch (SQLiteException e){
            Log.e("DB_Helper", "Unable to retrieve data from the " + TABLENAME, e.getCause());
        }
        return data;
    }

    public void insert(User data){
        new insertTask().execute(new DataParam(getWritableDatabase(), data, prepareData(data)));
    }

    public void update(User data){
        new updateTask().execute(new DataParam(getWritableDatabase(), data, prepareData(data)));
    }

    public void remove(User data){
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

    private ContentValues prepareData(User data){
        ContentValues content = new ContentValues();
        if(data.getUsername() != null)
            content.put("username", data.getUsername());
        if (data.getPassword() != null)
            content.put("password", data.getPassword());
        content.put("isAdmin", data.isAdmin());
        content.put("archived", 0);
        return content;
    }

    private User prepareData(Cursor cursor){
        User data = new User();
        data.setID(cursor.getInt(0));
        data.setUsername(cursor.getString(1));
        data.setPassword(cursor.getString(2));
        if(cursor.getInt(3) == 1)
            data.setAdmin(true);
        else
            data.setAdmin(false);

        return data;
    }

    private class DataParam{
        private SQLiteDatabase db;
        private User data;
        private ContentValues content;
        public DataParam(SQLiteDatabase db, User data, ContentValues content) {
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
            try{
                db.insert(TABLENAME, null, content);
            }catch (SQLiteException e){
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
            User data = param.data;
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
    private class removeTask extends AsyncTask<DataParam, Void, Void>{
        @Override
        protected Void doInBackground(DataParam... dataParams) {
            DataParam param = dataParams[0];
            SQLiteDatabase db = param.db;
            User data = param.data;
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
