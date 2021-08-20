package com.mohammadmawed.ebayclonemvvvm.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mohammadmawed.ebayclonemvvvm.R;

public class SplashScreenFragment extends Fragment {

    private static final int SPLASH_SCREEN_RUN_TIME_OUT = 1000 ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_ui, container, false);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Navigation.findNavController(requireView()).navigate(R.id.action_splashScreenFragment_to_mainUIFragment);
                } else {
                    // No user is signed in
                    Navigation.findNavController(requireView()).navigate(R.id.action_splashScreenFragment_to_loginFragment);
                }
            }
        }, SPLASH_SCREEN_RUN_TIME_OUT);

        return view;
    }
}
