package com.example.expensemanagerproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Adapter expenseAdapter;
    private List<addexpense.Expense> expenseList;
    private FirebaseFirestore db;
    private TextView tvIncome, tvTotalSpent, tvBalance;

    private double totalIncome = 0;
    private double totalExpenses = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tvIncome = findViewById(R.id.tvIncome);
        tvTotalSpent = findViewById(R.id.tvTotalSpent);
        tvBalance = findViewById(R.id.tvBalance);

        expenseList = new ArrayList<>();
        expenseAdapter = new Adapter(expenseList);
        recyclerView.setAdapter(expenseAdapter);

        db = FirebaseFirestore.getInstance();

        loadIncome();
        loadExpenses();

        TextView addExpenseTextView = findViewById(R.id.addexpense);
        addExpenseTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, addexpense.class);
                startActivity(intent);
            }
        });

        TextView addIncomeTextView = findViewById(R.id.addincome);
        addIncomeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, addincome.class);
                startActivity(intent);
            }
        });
    }

    private void loadIncome() {
        db.collection("incomes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        totalIncome = 0; // Reset totalIncome before summing up
                        for (DocumentSnapshot document : task.getResult()) {
                            if (document.exists()) {
                                totalIncome += Double.parseDouble(document.getString("Salary"));
                            }
                        }
                        tvIncome.setText("Income: " + totalIncome + " PKR");
                        updateBalance();
                    } else {
                        Log.w("MainActivity", "Error getting income.", task.getException());
                    }
                });
    }

    private void loadExpenses() {
        db.collection("expenses")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w("MainActivity", "Listen failed.", error);
                            return;
                        }

                        expenseList.clear();
                        totalExpenses = 0; // Reset totalExpenses before summing up

                        if (value != null) {
                            for (QueryDocumentSnapshot doc : value) {
                                addexpense.Expense expense = doc.toObject(addexpense.Expense.class);
                                expenseList.add(expense);
                                totalExpenses += Double.parseDouble(expense.amount);
                            }
                            expenseAdapter.notifyDataSetChanged();
                            tvTotalSpent.setText("Total Spent: " + totalExpenses + " PKR");
                            updateBalance();
                        }
                    }
                });
    }

    private void updateBalance() {
        double balance = totalIncome - totalExpenses;
        tvBalance.setText("Balance: " + balance + " PKR");
    }
}