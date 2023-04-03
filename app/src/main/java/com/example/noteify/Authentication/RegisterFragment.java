package com.example.noteify.Authentication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.noteify.Data.User;
import com.example.noteify.Database.UserHelper;
import com.example.noteify.R;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterFragment extends Fragment {
    public RegisterFragment() {
        super(R.layout.register_fragment);
    }

    private TextInputLayout username, password, password2;
    private UserHelper userHelper;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        username = view.findViewById(R.id.register_username);
        password = view.findViewById(R.id.register_password);
        password2 = view.findViewById(R.id.register_password2);
        TextView login = view.findViewById(R.id.register_login);
        Button button = view.findViewById(R.id.register_button);
        userHelper = new UserHelper(getContext());

        login.setOnClickListener(v->{
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.auth_container, new LoginFragment())
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
            if(password2.getEditText().getText().toString().isEmpty()) {
                password2.setError("Field cannot be empty!");
                hasInvalidField = true;
            }
            if(!password.getEditText().getText().toString().equals(password2.getEditText().getText().toString())) {
                Log.i("PasswordHelper", "Password1 is: " + password.getEditText().getText().toString() + " and Password2: " + password2.getEditText().getText().toString());
                password2.setError("Password does not match!");
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
            if(password.getEditText().getText().toString().length() < 5) {
                password.setError("Password must at least be 5 characters long!");
                hasInvalidField = true;
            }
            if(hasInvalidField)
                return;
            new checkAccountTask().execute(username.getEditText().getText().toString());
        });
    }

    private class checkAccountTask extends AsyncTask<String, Void, Boolean>{
        @Override
        protected void onPostExecute(Boolean isUnique) {
            super.onPostExecute(isUnique);
            if(!isUnique)
                return;
            User user = new User();
            user.setUsername(username.getEditText().getText().toString());
            user.setPassword(password.getEditText().getText().toString());
            user.setAdmin(true); //Set to true to make the registering user an admin
            userHelper.insert(user);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.auth_container, new LoginFragment())
                    .commit();
        }

        @Override
        protected Boolean doInBackground(String... Strings) {
            boolean isUnique = true;
            for(User user : userHelper.get())
                if(Strings[0].equals(user.getUsername())){
                    isUnique = false;
                    username.setError("Username is already in use!");
                }
            return isUnique;
        }
    }
}
