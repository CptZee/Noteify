package com.example.noteify;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.noteify.Authentication.LoginFragment;


public class SplashFragment extends Fragment {
    public SplashFragment() {
        super(R.layout.splash_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //text.animate().translationY(text.getHeight()).setDuration(300);
        new Handler().postDelayed(()->{
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.auth_container, new LoginFragment())
                    .commit();
        }, 2000);
    }
}
