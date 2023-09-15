package com.nightout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nightout.FirebaseToCSVExporter;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    //Views
    EditText mEmailEt, mPasswordEt;
    Button mRegisterBtn;
    TextView mHaveAccountTv;

    RadioGroup radioGroupType;
    RadioButton radioButtonOrganization, radioButtonIndividual;

    //progressbar to display while registering user
    ProgressDialog progressDialog;

    //Declare and instance of FirebaseAuth
    private FirebaseAuth mAuth;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Find and set up the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        actionbar and its title
//        ActionBar actionbar = getSupportActionBar();
//        actionbar.setTitle("Create Account");

        //enable back button
//        actionbar.setDisplayHomeAsUpEnabled(true);
//        actionbar.setDisplayShowHomeEnabled(true);

        //init
        mEmailEt = findViewById(R.id.emailEt);
        mPasswordEt = findViewById(R.id.passwordEt);
        mRegisterBtn = findViewById(R.id.registerBtn);
        mHaveAccountTv = findViewById(R.id.have_accountTv);

        radioGroupType = findViewById(R.id.radioGroupType);
        radioButtonOrganization = findViewById(R.id.radioButtonOrganization);
        radioButtonIndividual = findViewById(R.id.radioButtonIndividual);


        // In the onCreate() method, initialize the FirebaseAuth Instance
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");

        //handle register btn click
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //input email.password
                String email = mEmailEt.getText().toString().trim();
                String password = mPasswordEt.getText().toString().trim();
                //Validate
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    //set error and focus to email edittext
                    mEmailEt.setError("Invalid EMail");
                    mEmailEt.setFocusable(true);
                } else if (password.length() < 6) {
                    //set error and focus to password edittext
                    mPasswordEt.setError("Password length at least 6 characters");
                    mPasswordEt.setFocusable(true);
                } else {
                    registerUser(email, password); //Register the User
                    showQuestionnaireModal();
                }
            }
        });


        //handle login textview click listener
        mHaveAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });


    }


    private void showQuestionnaireModal() {
    }

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private String filePath; // Declare filePath at the class level


    private void registerUser(String email, String password) {
        //email and password pattern is valid, shows progress dialog and start registering user
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, dismiss dialog and start register activity
                            progressDialog.dismiss();

                            FirebaseUser user = mAuth.getCurrentUser();
                            //Get user email and uid from auth
                            String email = user.getEmail();
                            String uid = user.getUid();
                            String userType = radioButtonOrganization.isChecked() ? "Organization" : "Individual";
                            //when user is registered, store user info in firebase realtime database too
                            //using HashMap
                            HashMap<Object, String> hashMap = new HashMap<>();
                            //put info in hashmap
                            hashMap.put("email", email);
                            hashMap.put("uid", uid);
                            hashMap.put("name", "");// will add later ( e.g. edit profile)
                            hashMap.put("onlineStatus", " online");// will add later ( e.g. edit profile)
                            hashMap.put("typingTo", " noOne");// will add later ( e.g. edit profile)
                            hashMap.put("phone", "");// will add later ( e.g. edit profile)
                            hashMap.put("image", "");// will add later ( e.g. edit profile)
                            hashMap.put("cover", "");// will add later ( e.g. edit profile)
                            hashMap.put("userType", userType); // Store the selected user type in the database
                            hashMap.put("verified", "notverified"); // Store the selected user type in the database
                            hashMap.put("following", ""); // Store the selected user type in the database
                            hashMap.put("followers", ""); // Store the selected user type in the database

                            //firebase database instance
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            //path to store user data named "Users"
                            DatabaseReference reference = database.getReference("Users");
                            //put data within hashmap in database
                            reference.child(uid).setValue(hashMap);


                            // Direct the user to different activities based on user type
                            if ("Organization".equals(userType)) {
                                // User is an Organization, redirect to OrganizationDashboardActivity
                                startActivity(new Intent(RegisterActivity.this, QuestionOrganizationActivity.class));
                            } else if ("Individual".equals(userType)) {
                                // User is an Individual, redirect to IndividualDashboardActivity
                                startActivity(new Intent(RegisterActivity.this, QuestionIndividualActivity.class));
                            }

                            DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("Users");
                            usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    List<User> userList = new ArrayList<>();

                                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                        User user = userSnapshot.getValue(User.class);
                                        userList.add(user);
                                    }

                                    // Pass the userList to the CSV export method
                                    exportUserDataToCSV(userList);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle database error
                                }
                            });

                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //error, dismiss progress dialog and get and show the error message
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }
    private void exportUserDataToCSV(List<User> userList) {
        List<String> csvRows = new ArrayList<>();
        csvRows.add("email,uid,name,onlineStatus,typingTo,phone,image,cover,userType");

        for (User user : userList) {
            String csvRow = user.email + "," + user.uid + "," + user.name + "," + user.onlineStatus + ","
                    + user.typingTo + "," + user.phone + "," + user.image + "," + user.cover + "," + user.userType;
            csvRows.add(csvRow);
        }

        String csvData = TextUtils.join("\n", csvRows);

        // Now, you can upload the CSV data to Firebase Storage
        saveCSVToFirebaseStorage(csvData);
    }

    // Helper method to save CSV data to a file
    //saveCSVToFile method was changed to saveCSVToFirebaseStorage
    private void saveCSVToFirebaseStorage(String csvData) {
        // Create a reference to the file you want to upload
        StorageReference csvFileRef = storageRef.child("userData.csv");

        // Convert CSV data to bytes
        byte[] csvBytes = csvData.getBytes();

        // Upload the file to Firebase Storage
        UploadTask uploadTask = csvFileRef.putBytes(csvBytes);

        // Register observers to listen for when the upload is done or if it fails
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(this, "User data exported to Firebase Storage", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to export user data to Firebase Storage", Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // go previous activity
        return super.onSupportNavigateUp();
    }


}