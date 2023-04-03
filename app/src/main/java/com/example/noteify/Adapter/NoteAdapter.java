package com.example.noteify.Adapter;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteify.Data.Note;
import com.example.noteify.Database.NoteHelper;
import com.example.noteify.Note.NoteFragment;
import com.example.noteify.NoteActivity;
import com.example.noteify.R;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private List<Note> list;
    private NoteActivity activity;

    public NoteAdapter(List<Note> list, NoteActivity activity) {
        this.list = list;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.note_card, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTitle().setText(list.get(position).getTitle());
        viewHolder.getButton().setOnClickListener(v->{
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("Are you sure you want to delete note entitled \"" + list.get(position).getTitle() + "\"")
                    .setPositiveButton("Yes", (dialog, id) -> {
                        NoteHelper helper = new NoteHelper(activity);
                        helper.remove(list.get(position));
                        list.remove(position);
                        notifyDataSetChanged();
                    })
                    .setNegativeButton("No", (dialog, id) -> {

                    }).create().show();
        });
        viewHolder.getLayout().setOnClickListener(v->{
            Fragment fragment = new NoteFragment();
            Bundle args = new Bundle();
            args.putBoolean("isNew", false);
            fragment.setArguments(args);
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.note_container, fragment)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final ImageView button;
        private final ConstraintLayout layout;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.card_title);
            button = view.findViewById(R.id.card_delete);
            layout = view.findViewById(R.id.card_container);
        }

        public TextView getTitle() {
            return title;
        }

        public ImageView getButton() {
            return button;
        }

        public ConstraintLayout getLayout() {
            return layout;
        }
    }
}
