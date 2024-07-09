package com.example.expensemanagerproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class addexpense extends AppCompatActivity {

    private EditText etAmount, etSpendInformation, etCategory;
    private Button btnSave;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addexpense);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        etAmount = findViewById(R.id.etamount);
        etSpendInformation = findViewById(R.id.etspendinformation);
        etCategory = findViewById(R.id.etcategory);
        btnSave = findViewById(R.id.btnsave);

        // Set save button click listener
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExpense();
            }
        });
    }

    private void saveExpense() {
        String amount = etAmount.getText().toString().trim();
        String spendInformation = etSpendInformation.getText().toString().trim();
        String category = etCategory.getText().toString().trim();

        if (TextUtils.isEmpty(amount) || TextUtils.isEmpty(spendInformation) || TextUtils.isEmpty(category)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        DocumentReference newExpenseRef = db.collection("expenses").document();
        String documentId = newExpenseRef.getId();

        Expense expense = new Expense(documentId, amount, spendInformation, category);

        newExpenseRef.set(expense)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(addexpense.this, "Expense saved", Toast.LENGTH_SHORT).show();
                    clearFields();

                    Intent intent = new Intent(addexpense.this, MainActivity.class);
                    startActivity(intent);

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(addexpense.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearFields() {
        etAmount.setText("");
        etSpendInformation.setText("");
        etCategory.setText("");
    }

    public static class Expense {
        public String documentId;
        public String amount;
        public String spendInformation;
        public String category;
        public String description;

        public Expense() {
        }

        public Expense(String documentId, String amount, String spendInformation, String category) {
            this.documentId = documentId;
            this.amount = amount;
            this.spendInformation = spendInformation;
            this.category = category;
        }
    }
}
