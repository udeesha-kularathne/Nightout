package com.nightout;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nightout.models.ModelDashboard;

public class OrganizationDashboardActivity extends AppCompatActivity {


    //progress bar
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_dashboard);

        // Find the EditText fields
        EditText editTextMarketingCost = findViewById(R.id.editTextMarketingCost);
        EditText editTextLaborCost = findViewById(R.id.editTextLaborCost);
        EditText editTextUtilitiesCost = findViewById(R.id.editTextUtilitiesCost);
        EditText editTextPersonnelCost = findViewById(R.id.editTextPersonnelCost);
        EditText editTextVenueCost = findViewById(R.id.editTextVenueCost);
        EditText editTextMaterialCost = findViewById(R.id.editTextMaterialCost);
        EditText editTextTicketCount = findViewById(R.id.editTextTicketCount);
        EditText editTextTicketPrice = findViewById(R.id.editTextTicketPrice);
        EditText editTextSaleIncome = findViewById(R.id.editTextSalesIncome);
        EditText editTextSponsorIncome = findViewById(R.id.editTextSponsorIncome);
        // Find other EditText fields

        // In your OrganizationDashboardActivity
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dataRef = database.getReference("DataCost"); // Change to your reference

        // Populate Category Spinner
        Spinner spinnerCategory = findViewById(R.id.spinnerCategory);
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // Populate Month Spinner
        Spinner spinnerMonth = findViewById(R.id.spinnerMonth);
        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(this,
                R.array.months_array, android.R.layout.simple_spinner_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        // In your OrganizationDashboardActivity
        Button btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(view -> {

            String marketingCost = editTextMarketingCost.getText().toString();
            String laborCost = editTextLaborCost.getText().toString();
            String utilitiesCost = editTextUtilitiesCost.getText().toString();
            String personnelCost = editTextPersonnelCost.getText().toString();
            String venueCost = editTextVenueCost.getText().toString();
            String materialCost = editTextMaterialCost.getText().toString();
            String ticketCount = editTextTicketCount.getText().toString();
            String ticketPrice = editTextTicketPrice.getText().toString();
            String salesIncome = editTextSaleIncome.getText().toString();
            String sponsorIncome = editTextSponsorIncome.getText().toString();
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String timeStamp = String.valueOf(System.currentTimeMillis());
            // ... Repeat for other EditText fields

            String selectedCategory = spinnerCategory.getSelectedItem().toString();
            String selectedMonth = spinnerMonth.getSelectedItem().toString();

            if (selectedCategory.equals("Select Category") || selectedMonth.equals("Select The Month")) {
                Toast.makeText(OrganizationDashboardActivity.this, "Please select Category and Month.", Toast.LENGTH_SHORT).show();
                return; // Return to exit the function and prevent further execution
            }

            // Replace empty or null values with 0
            marketingCost = marketingCost.isEmpty() ? "0" : marketingCost;
            laborCost = laborCost.isEmpty() ? "0" : laborCost;
            utilitiesCost = utilitiesCost.isEmpty() ? "0" : utilitiesCost;
            personnelCost = personnelCost.isEmpty() ? "0" : personnelCost;
            venueCost = venueCost.isEmpty() ? "0" : venueCost;
            materialCost = materialCost.isEmpty() ? "0" : materialCost;
            ticketCount = ticketCount.isEmpty() ? "0" : ticketCount;
            ticketPrice = ticketPrice.isEmpty() ? "0" : ticketPrice;
            salesIncome = salesIncome.isEmpty() ? "0" : salesIncome;
            sponsorIncome = sponsorIncome.isEmpty() ? "0" : sponsorIncome;

// Perform calculations
            double totalCost = Double.parseDouble(marketingCost) +
                    Double.parseDouble(laborCost) +
                    Double.parseDouble(utilitiesCost) +
                    Double.parseDouble(personnelCost) +
                    Double.parseDouble(venueCost) +
                    Double.parseDouble(materialCost);

            double ticketTotal = Double.parseDouble(ticketCount) * Double.parseDouble(ticketPrice);

            double totalIncome = Double.parseDouble(salesIncome) + Double.parseDouble(sponsorIncome);

            double Profit = totalIncome + ticketTotal - totalCost;
            String totalProfit = String.valueOf(Profit);

// Create the ModelDashboard instance with other data
            ModelDashboard data = new ModelDashboard(
                    marketingCost, laborCost, utilitiesCost, personnelCost, venueCost,
                    materialCost, ticketCount, ticketPrice, salesIncome, sponsorIncome, Profit,
                    selectedCategory, selectedMonth, uid);
            // Set the total profit in the data instance
//            data.setTotalProfit(totalProfit);

            // Push the data to the database
            dataRef.push().setValue(data);


            dataRef.child(timeStamp).setValue(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //added in database
                            if (pd != null && pd.isShowing()) {
                                pd.dismiss();
                            }
                            Toast.makeText(OrganizationDashboardActivity.this, "Cost Published", Toast.LENGTH_SHORT).show();
                            //reset views
                            editTextMarketingCost.setText("");
                            editTextLaborCost.setText("");
                            editTextUtilitiesCost.setText("");
                            editTextPersonnelCost.setText("");
                            editTextVenueCost.setText("");
                            editTextMaterialCost.setText("");
                            editTextTicketCount.setText("");
                            editTextTicketPrice.setText("");
                            editTextSaleIncome.setText("");
                            editTextSponsorIncome.setText("");


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed adding post in database
                            pd.dismiss();
                            Toast.makeText(OrganizationDashboardActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
