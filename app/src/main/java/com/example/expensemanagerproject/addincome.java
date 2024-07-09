package com.example.expensemanagerproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class addincome extends AppCompatActivity {

    private EditText etsalary, etmonth;
    private Button btnSave;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addincome);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        etsalary = findViewById(R.id.etsalary);
        etmonth = findViewById(R.id.etmonth);
        btnSave = findViewById(R.id.btnsave);

        // Set save button click listener
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveIncome();
            }
        });
    }

    private void saveIncome() {
        String salary = etsalary.getText().toString().trim();
        String month = etmonth.getText().toString().trim();

        if (TextUtils.isEmpty(salary) || TextUtils.isEmpty(month)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Income income = new Income(salary, month);

        db.collection("incomes")
                .add(income)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(addincome.this, "Income saved", Toast.LENGTH_SHORT).show();
                    clearFields();
                    Intent intent = new Intent(addincome.this, MainActivity.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(addincome.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearFields() {
        etsalary.setText("");
        etmonth.setText("");
    }

    public static class Income {
        public String Salary;
        public String Month;

        // Default constructor required for calls to DataSnapshot.getValue(Income.class)
        public Income() {
        }

        public Income(String salary, String month) {
            this.Salary = salary;
            this.Month = month;
        }
    }
}
