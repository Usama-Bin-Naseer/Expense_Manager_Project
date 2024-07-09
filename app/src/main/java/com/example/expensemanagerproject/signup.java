package com.example.expensemanagerproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class signup extends AppCompatActivity {

    TextView tvLogin;
    Button btnSignup;
    TextInputEditText etName, etPassword, etCPassword;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();

        tvLogin.setOnClickListener(v->{
            startActivity(new Intent(signup.this, com.example.expensemanagerproject.login.class));
            finish();
        });

        btnSignup.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String password = etPassword.getText().toString();
            String cpassword=etCPassword.getText().toString();

            if (TextUtils.isEmpty(name)) {
                etName.setError("Enter the name");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Enter the password");
                return;
            }

            if (TextUtils.isEmpty(cpassword)) {
                etPassword.setError("Enter the confirm password");
                return;
            }

            if(!password.equals(cpassword))
            {
                etPassword.setError("password mis-match");
                etCPassword.setError("password mis-match");
                return;
            }

            auth.createUserWithEmailAndPassword(name, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(signup.this, "User created", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(signup.this, com.example.expensemanagerproject.MainActivity.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(signup.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });



        });

    }

    private void init()
    {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signup), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvLogin = findViewById(R.id.tvLogin);
        btnSignup = findViewById(R.id.btnSignup);
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        etCPassword = findViewById(R.id.etConfirmPassword);
        auth = FirebaseAuth.getInstance();
    }
}