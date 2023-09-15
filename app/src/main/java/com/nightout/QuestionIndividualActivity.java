package com.nightout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nightout.DashboardActivity;
import com.nightout.OrganizationDashboardActivity;

public class QuestionIndividualActivity extends AppCompatActivity {

    // Declare your UI elements here
    private RadioGroup Question1RadioGroup, Question3RadioGroup, Question6RadioGroup, Question7RadioGroup;

    private CheckBox checkBoxAnswer1, checkBoxAnswer2, checkBoxAnswer3, checkBoxAnswer4, checkBoxAnswer5
            , checkBoxAnswer6, checkBoxAnswer7, checkBoxAnswer8, checkBoxAnswer9
            , checkBoxAnswer10, checkBoxAnswer11, checkBoxAnswer12, checkBoxAnswer13,
            checkBoxAnswer14;
    private Button submitButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_individual);

        // Initialize your UI elements here
        Question1RadioGroup = findViewById(R.id.Question1RadioGroup);
        Question3RadioGroup = findViewById(R.id.Question3RadioGroup);
        Question6RadioGroup = findViewById(R.id.Question6RadioGroup);
        Question7RadioGroup = findViewById(R.id.Question7RadioGroup);

        // Initialize more UI elements for other questions
        checkBoxAnswer1 = findViewById(R.id.checkBoxCategory2_1);
        checkBoxAnswer2 = findViewById(R.id.checkBoxCategory2_2);
        checkBoxAnswer3 = findViewById(R.id.checkBoxCategory2_3);
        checkBoxAnswer4 = findViewById(R.id.checkBoxCategory2_4);
        checkBoxAnswer5 = findViewById(R.id.checkBoxCategory2_5);
        checkBoxAnswer6 = findViewById(R.id.checkBoxCategory2_6);
        checkBoxAnswer7 = findViewById(R.id.checkBoxCategory2_7);
        checkBoxAnswer8 = findViewById(R.id.checkBoxCategory2_8);
        checkBoxAnswer9 = findViewById(R.id.checkBoxCategory2_9);
        checkBoxAnswer10 = findViewById(R.id.checkBoxCategory2_10);
        checkBoxAnswer11 = findViewById(R.id.checkBoxCategory2_11);
        checkBoxAnswer12 = findViewById(R.id.checkBoxCategory2_12);
        checkBoxAnswer13 = findViewById(R.id.checkBoxCategory2_13);
        checkBoxAnswer14 = findViewById(R.id.checkBoxCategory2_14);

        // Populate Location Spinner
        Spinner spinnerLocation = findViewById(R.id.Question2Spinner);
        ArrayAdapter<CharSequence> locationAdapter = ArrayAdapter.createFromResource(this,
                R.array.location_array, android.R.layout.simple_spinner_item);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(locationAdapter);

        // Populate Age Spinner
        Spinner spinnerAge = findViewById(R.id.Question4Spinner);
        ArrayAdapter<CharSequence> AgeAdapter = ArrayAdapter.createFromResource(this,
                R.array.Age_array, android.R.layout.simple_spinner_item);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAge.setAdapter(AgeAdapter);


        // Initialize more CheckBox fields for other categories
        submitButton = findViewById(R.id.submitButton);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Set click listener for the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve answers from UI elements
                String answer1 = getSelectedRadioText(Question1RadioGroup);
                String answer3 = getSelectedRadioText(Question3RadioGroup);
                String answer6 = getSelectedRadioText(Question6RadioGroup);
                String answer7 = getSelectedRadioText(Question7RadioGroup);



                // Retrieve selected categories from CheckBox fields
                String answer5 = getSelectedCategories();

                String answer2 = spinnerLocation.getSelectedItem().toString();
                String answer4 = spinnerAge.getSelectedItem().toString();

                // Store answers in the database
                storeAnswersInDatabase(answer1, answer2, answer3, answer4, answer5, answer6, answer7);



                // Redirect to appropriate activity (Dashboard, etc.)
                redirectBasedOnUserType();
            }
        });
    }

    private String getSelectedRadioText(RadioGroup radioGroup) {
        int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
        if (selectedRadioButton != null) {
            return selectedRadioButton.getText().toString();
        }
        return "";
    }

    private String getSelectedCategories() {
        StringBuilder selectedCategories = new StringBuilder();
        if (checkBoxAnswer1.isChecked()) {
            selectedCategories.append("Exhibition, ");
        }
        if (checkBoxAnswer2.isChecked()) {
            selectedCategories.append("Concerts, ");
        }
        if (checkBoxAnswer3.isChecked()) {
            selectedCategories.append("Exhibition, ");
        }
        if (checkBoxAnswer4.isChecked()) {
            selectedCategories.append("Music, ");
        }
        if (checkBoxAnswer5.isChecked()) {
            selectedCategories.append("Festival, ");
        }
        if (checkBoxAnswer6.isChecked()) {
            selectedCategories.append("Travel, ");
        }
        if (checkBoxAnswer7.isChecked()) {
            selectedCategories.append("Sale, ");
        }
        if (checkBoxAnswer8.isChecked()) {
            selectedCategories.append("Competition, ");
        }
        if (checkBoxAnswer9.isChecked()) {
            selectedCategories.append("Concerts, ");
        }
        if (checkBoxAnswer10.isChecked()) {
            selectedCategories.append("Shows, ");
        }
        if (checkBoxAnswer11.isChecked()) {
            selectedCategories.append("Food, ");
        }
        if (checkBoxAnswer12.isChecked()) {
            selectedCategories.append("Sports, ");
        }
        if (checkBoxAnswer13.isChecked()) {
            selectedCategories.append("Shows, ");
        }
        if (checkBoxAnswer14.isChecked()) {
            selectedCategories.append("Sports, ");
        }
        // Add more CheckBox checks for other categories
        return selectedCategories.toString();
    }

    private void storeAnswersInDatabase(String answer1, String answer2, String answer3, String answer4, String answer5, String answer6, String answer7) {
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Users").child(uid);

        // Store answers in the database under a "Questionnaire" node
        reference.child("IndividualQuestions").child("answer1").setValue(answer1);
        reference.child("IndividualQuestions").child("answer2").setValue(answer2);
        reference.child("IndividualQuestions").child("answer3").setValue(answer3);
        reference.child("IndividualQuestions").child("answer4").setValue(answer4);
        reference.child("IndividualQuestions").child("answer5").setValue(answer5);
        reference.child("IndividualQuestions").child("answer6").setValue(answer6);
        reference.child("IndividualQuestions").child("answer7").setValue(answer7);



    }

    private void redirectBasedOnUserType() {
        FirebaseUser user = mAuth.getCurrentUser();
        String userType = "Individual"; // Assuming this user type for this example

        if ("Organization".equals(userType)) {
            startActivity(new Intent(QuestionIndividualActivity.this, OrganizationDashboardActivity.class));
        } else if ("Individual".equals(userType)) {
            startActivity(new Intent(QuestionIndividualActivity.this, DashboardActivity.class));
        }

        finish();
    }
}
