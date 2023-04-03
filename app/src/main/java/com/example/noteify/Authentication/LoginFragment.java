package com.example.noteify.Authentication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.noteify.Data.User;
import com.example.noteify.Database.UserHelper;
import com.example.noteify.NoteActivity;
import com.example.noteify.R;
import com.google.android.material.textfield.TextInputLayout;

public class LoginFragment extends Fragment {
    public LoginFragment() {
        super(R.layout.login_fragment);
    }

    private TextInputLayout username, password;
    private TextView signUp;

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        username = view.findViewById(R.id.login_username);
        password = view.findViewById(R.id.login_password);
        signUp = view.findViewById(R.id.login_signup);
        Button button = view.findViewById(R.id.register_button);

        signUp.setOnClickListener(v->{
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.auth_container, new RegisterFragment())
                    .commit();
        });
        button.setOnClickListener(v->{
            boolean hasInvalidField = false;
            if(username.getEditText().getText().toString().isEmpty()) {
                username.setError("Field cannot be empty!");
                hasInvalidField = true;
            }
            if(password.getEditText().getText().toString().isEmpty()) {
                password.setError("Field cannot be empty!");
                hasInvalidField = true;
            }
            if(username.getEditText().getText().toString().length() < 4) {
                username.setError("Username must at least be 4 characters long!");
                hasInvalidField = true;
            }
            if(username.getEditText().getText().toString().length() > 15) {
                username.setError("Username must at be 15 characters at most!");
                hasInvalidField = true;
            }
            if(hasInvalidField)
                return;
            User user = new User();
            user.setUsername(username.getEditText().getText().toString());
            user.setPassword(password.getEditText().getText().toString());
            new loginTask().execute(user);
        });
    }

    private class loginTask extends AsyncTask<User, Void, User>{
        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            if(user != null){
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Noteify", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("userID", user.getID());
                editor.putBoolean("userIsAdmin", user.isAdmin());
                editor.commit();
                Log.i("AdminHelper", "Current account is admin? " + sharedPreferences.getBoolean("userIsAdmin", false));
                startActivity(new Intent(getContext(), NoteActivity.class));
                getActivity().finish();
                return;
            }
            username.setError("Invalid username!");
            password.setError("Invalid password!");
            Toast.makeText(getContext(), "Invalid username or password!", Toast.LENGTH_SHORT);
        }

        @Override
        protected User doInBackground(User... users) {
            User user = users[0];
            int ID = 0;
            UserHelper userHelper = new UserHelper(getContext());
            for(User u : userHelper.get()){
                Log.i("AuthLogger", "Matching accounts...");
                if(user.getUsername().equals(u.getUsername()) && user.getPassword().equals(u.getPassword())){
                    Log.i("AuthLogger", "Account match!");
                    return user;
                }
            }
            Log.i("AuthLogger", "Account to parse is " + ID);
            return null;
        }
    }
}
