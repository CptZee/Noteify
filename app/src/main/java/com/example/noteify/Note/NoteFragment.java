package com.example.noteify.Note;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.noteify.Data.Note;
import com.example.noteify.Database.NoteHelper;
import com.example.noteify.R;
import com.google.android.material.textfield.TextInputLayout;

public class NoteFragment extends Fragment {
    public NoteFragment() {
        super(R.layout.view_fragment);
    }
    private TextInputLayout title;
    private EditText body;
    private NoteHelper helper;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        helper = new NoteHelper(getContext());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Noteify", Context.MODE_PRIVATE);
        int ID = sharedPreferences.getInt("userID", 0);
        boolean isNew = getArguments().getBoolean("isNew");
        int noteID = getArguments().getInt("noteID");
        title = view.findViewById(R.id.view_title);
        body = view.findViewById(R.id.view_body);
        Button saveButton = view.findViewById(R.id.view_button);
        Button backButton = view.findViewById(R.id.view_back_button);

        backButton.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.note_container, new ListFragment())
                    .commit();
        });

        saveButton.setOnClickListener(v->{
            Note note = new Note();
            Log.i("NoteLogger", "Button was clicked!");
            if(title.getEditText().getText().toString().isEmpty()){
                title.setError("Tittle cannot be empty!");
                return;
            }
            if(isNew){
                Log.i("NoteLogger", "Note is new");
                note.setTitle(title.getEditText().getText().toString());
                note.setText(body.getText().toString());
                note.setOwnerID(ID);
                helper.insert(note);
                Toast.makeText(getContext(), "Successfully created a note entitled \"" + note.getTitle()  +"\"", Toast.LENGTH_SHORT).show();
            }else{
                Log.i("NoteLogger", "Note is not new");
                note.setID(noteID);
                note.setTitle(title.getEditText().getText().toString());
                note.setText(body.getText().toString());
                helper.update(note);
                Toast.makeText(getContext(), "Successfully updated the note entitled \"" + note.getTitle()  +"\"", Toast.LENGTH_SHORT).show();
            }
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.note_container, new ListFragment())
                    .commit();
        });

        if(!isNew)
            new initModification().execute(noteID);
    }
    private class initModification extends AsyncTask<Integer, Void, Note>{
        @Override
        protected void onPostExecute(Note note) {
            title.getEditText().setText(note.getTitle());
            body.setText(note.getText());
            super.onPostExecute(note);
        }

        @Override
        protected Note doInBackground(Integer... integers) {
            Note note = helper.get(integers[0]);
            return note;
        }
    }
}
