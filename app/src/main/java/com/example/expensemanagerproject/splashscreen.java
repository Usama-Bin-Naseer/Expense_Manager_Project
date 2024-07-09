package com.example.expensemanagerproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class splashscreen extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 1300;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        auth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    // User is signed in, navigate to MainActivity
                    Intent mainIntent = new Intent(splashscreen.this, com.example.expensemanagerproject.MainActivity.class);
                    startActivity(mainIntent);
                } else {
                    // No user is signed in, navigate to LoginActivity
                    Intent loginIntent = new Intent(splashscreen.this, com.example.expensemanagerproject.login.class);
                    startActivity(loginIntent);
                }
                finish();
            }
        }, SPLASH_TIME_OUT);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
