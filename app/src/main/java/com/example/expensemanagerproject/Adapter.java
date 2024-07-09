package com.example.expensemanagerproject;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ExpenseViewHolder> {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<addexpense.Expense> expenseList;

    public Adapter(List<addexpense.Expense> expenseList) {
        this.expenseList = expenseList;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_adapter, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        addexpense.Expense expense = expenseList.get(position);
        holder.tvCategory.setText(expense.category);
        holder.tvSpendInformation.setText(expense.spendInformation);
        holder.tvAmount.setText(expense.amount + " PKR");

        holder.itemView.setOnLongClickListener(view -> {
            showUpdateDialog(holder.itemView.getContext(), expense, position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    private void showUpdateDialog(Context context, addexpense.Expense expense, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.update_expense, null);
        builder.setView(dialogView);

        EditText updateAmount = dialogView.findViewById(R.id.Updateamount);
        EditText updateSpendingInformation = dialogView.findViewById(R.id.Updatespendinformation);
        EditText updateCategory = dialogView.findViewById(R.id.Updatecategory);

        // Set current values
        updateAmount.setText(expense.amount);
        updateSpendingInformation.setText(expense.spendInformation);
        updateCategory.setText(expense.category);

        // Set up the buttons
        builder.setPositiveButton("Update", (dialog, which) -> {
            // Collect data
            String newAmount = updateAmount.getText().toString();
            String newInfo = updateSpendingInformation.getText().toString();
            String newCategory = updateCategory.getText().toString();

            // Update the model
            updateExpenseInFirestore(expense, newAmount, newInfo, newCategory, position);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.setNeutralButton("Delete", (dialog, which) -> {
            // Delete the expense
            deleteExpenseFromFirestore(expense.documentId, position);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateExpenseInFirestore(addexpense.Expense expenseToUpdate, String amount, String spendInformation, String category, int position) {
        expenseToUpdate.amount = amount;
        expenseToUpdate.spendInformation = spendInformation;
        expenseToUpdate.category = category;

        // Use the document ID to update the specific document
        db.collection("expenses").document(expenseToUpdate.documentId)
                .set(expenseToUpdate)
                .addOnSuccessListener(aVoid -> {
                    expenseList.set(position, expenseToUpdate);
                    notifyItemChanged(position);
                    Log.d("Update", "DocumentSnapshot successfully updated!");
                })
                .addOnFailureListener(e -> Log.w("Update", "Error updating document", e));
    }

    private void deleteExpenseFromFirestore(String documentId, int position) {
        // Use the document ID to delete the specific document
        db.collection("expenses").document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    if (position >= 0 && position < expenseList.size()) {
                        expenseList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, expenseList.size());
                        Log.d("Delete", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(e -> Log.w("Delete", "Error deleting document", e));
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvSpendInformation, tvAmount;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvSpendInformation = itemView.findViewById(R.id.tvSpendInformation);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }
}
