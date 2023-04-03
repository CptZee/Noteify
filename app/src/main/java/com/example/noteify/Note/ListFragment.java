package com.example.noteify.Note;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteify.Adapter.NoteAdapter;
import com.example.noteify.Data.Note;
import com.example.noteify.Database.NoteHelper;
import com.example.noteify.NoteActivity;
import com.example.noteify.R;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {
    public ListFragment() {
        super(R.layout.list_fragment);
    }

    private RecyclerView list;
    private TextView note_data_loader, note_all_users, note_all;
    private SharedPreferences preferences;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        boolean showingAll = false;
        list = view.findViewById(R.id.note_list);
        note_data_loader = view.findViewById(R.id.note_data_loader);
        note_all_users = view.findViewById(R.id.note_all_users);
        note_all = view.findViewById(R.id.note_all);
        list.setLayoutManager(new LinearLayoutManager(getContext()));

        preferences = getActivity().getSharedPreferences("Noteify",Context.MODE_PRIVATE);
        showForSingleUser();
        Log.i("AdminHelper", "Current account is admin? " + preferences.getBoolean("userIsAdmin", false));
        if(preferences.getBoolean("userIsAdmin", false))
            note_all_users.setVisibility(View.VISIBLE);


        EditText search = view.findViewById(R.id.note_search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //null
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {AccountParam param = new AccountParam(preferences.getBoolean("userIsAdmin", false), preferences.getInt("userID", 0), search.getText().toString());
                AccountParam params = new AccountParam(preferences.getBoolean("userIsAdmin", false), preferences.getInt("userID", 0), search.getText().toString());
                if(preferences.getBoolean("userIsAdmin", false) && showingAll)
                    new initSearchListTask().execute(params);
                else
                    new initSearchTask().execute(params);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //null
            }
        });

        note_all.setTypeface(note_all.getTypeface(), Typeface.BOLD);
        note_all.setOnClickListener(v->{
            note_all.setTypeface(note_all.getTypeface(), Typeface.BOLD);
            note_all_users.setTypeface(note_all.getTypeface(), Typeface.NORMAL);
            showForSingleUser();
        });

        note_all_users.setOnClickListener((v->{
            note_all_users.setTypeface(note_all.getTypeface(), Typeface.BOLD);
            note_all.setTypeface(note_all.getTypeface(), Typeface.NORMAL);
            showForAllUsers();
        }));

        Button button = view.findViewById(R.id.note_add_button);
        button.setOnClickListener(v->{
            Fragment fragment = new NoteFragment();
            Bundle args = new Bundle();
            args.putBoolean("isNew", true);
            fragment.setArguments(args);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.note_container, fragment)
                    .commit();
        });
    }

    private void showForSingleUser(){
        new initListTask().execute(new AccountParam(preferences.getBoolean("userIsAdmin", false), preferences.getInt("userID", 0), null));
    }

    private void showForAllUsers(){
        new initListAllTask().execute(new AccountParam(preferences.getBoolean("userIsAdmin", false), preferences.getInt("userID", 0), null));
    }

    private class AccountParam{
        private String title;
        private boolean isAdmin;
        private int ID;

        public AccountParam(boolean isAdmin, int ID, String title) {
            this.title = title;
            this.isAdmin = isAdmin;
            this.ID = ID;
        }
    }

    private class initSearchTask extends  AsyncTask<AccountParam, Void, List<Note>>{

        @Override
        protected List<Note> doInBackground(AccountParam... accountParams) {
            AccountParam param = accountParams[0];
            boolean isAdmin = param.isAdmin;
            int ID = param.ID;
            String title = param.title;
            List<Note> list = new ArrayList<>();
            NoteHelper noteHelper = new NoteHelper(getContext());
            for(Note note: noteHelper.get()){
                if(!isAdmin) {
                    if (note.getOwnerID() == ID)
                        if(note.getTitle().toLowerCase().contains(title.toLowerCase()))
                            list.add(note);
                }else
                    if(note.getTitle().toLowerCase().contains(title.toLowerCase()))
                        list.add(note);
            }

            Log.i("ListAdapter", "List size is: " + list.size());
            for(Note note: list){
                Log.i("ListAdapter", note.getTitle());
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<Note> notes) {
            super.onPostExecute(notes);
            if(notes.size() < 1)
                note_data_loader.setText("No notes found!");
            else
                note_data_loader.setVisibility(View.INVISIBLE);
            NoteAdapter adapter = new NoteAdapter(notes, (NoteActivity) getActivity());
            list.setAdapter(adapter);
        }
    }

    private class initListTask extends AsyncTask<AccountParam, Void, List<Note>>{

        @Override
        protected List<Note> doInBackground(AccountParam... accountParams) {
            AccountParam param = accountParams[0];
            boolean isAdmin = param.isAdmin;
            int ID = param.ID;
            List<Note> list = new ArrayList<>();
            NoteHelper noteHelper = new NoteHelper(getContext());
            for(Note note: noteHelper.get()){
                if(!isAdmin) {
                    if (note.getOwnerID() == ID)
                        list.add(note);
                }else
                    list.add(note);
            }

            Log.i("ListAdapter", "List size is: " + list.size());
            for(Note note: list){
                Log.i("ListAdapter", note.getTitle());
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<Note> notes) {
            super.onPostExecute(notes);
            if(notes.size() < 1)
                note_data_loader.setText("No notes found! Create one now!");
            else
                note_data_loader.setVisibility(View.INVISIBLE);
            NoteAdapter adapter = new NoteAdapter(notes, (NoteActivity) getActivity());
            list.setAdapter(adapter);
        }
    }

    private class initListAllTask extends AsyncTask<AccountParam, Void, List<Note>>{

        @Override
        protected List<Note> doInBackground(AccountParam... accountParams) {
            AccountParam param = accountParams[0];
            List<Note> list = new ArrayList<>();
            NoteHelper noteHelper = new NoteHelper(getContext());
            for(Note note: noteHelper.get()){
                list.add(note);
            }

            Log.i("ListAdapter", "List size is: " + list.size());
            for(Note note: list){
                Log.i("ListAdapter", note.getTitle());
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<Note> notes) {
            super.onPostExecute(notes);
            if(notes.size() < 1)
                note_data_loader.setText("No notes found! Create one now!");
            else
                note_data_loader.setVisibility(View.INVISIBLE);
            NoteAdapter adapter = new NoteAdapter(notes, (NoteActivity) getActivity());
            list.setAdapter(adapter);
        }
    }

    private class initSearchListTask extends  AsyncTask<AccountParam, Void, List<Note>>{

        @Override
        protected List<Note> doInBackground(AccountParam... accountParams) {
            AccountParam param = accountParams[0];
            boolean isAdmin = param.isAdmin;
            int ID = param.ID;
            String title = param.title;
            List<Note> list = new ArrayList<>();
            NoteHelper noteHelper = new NoteHelper(getContext());
            for(Note note: noteHelper.get()){
                if(note.getTitle().toLowerCase().contains(title.toLowerCase()))
                    list.add(note);
            }

            Log.i("ListAdapter", "List size is: " + list.size());
            for(Note note: list){
                Log.i("ListAdapter", note.getTitle());
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<Note> notes) {
            super.onPostExecute(notes);
            if(notes.size() < 1)
                note_data_loader.setText("No notes found!");
            else
                note_data_loader.setVisibility(View.INVISIBLE);
            NoteAdapter adapter = new NoteAdapter(notes, (NoteActivity) getActivity());
            list.setAdapter(adapter);
        }
    }
}
