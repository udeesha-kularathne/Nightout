package com.nightout;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class FirebaseToCSVExporter {

    public interface CSVExportListener {
        void onCSVExportSuccess(String csvData);
        void onCSVExportFailure(String errorMessage);
    }

    public void exportAllUserDataToCSV(Context context, DatabaseReference usersReference, CSVExportListener exportListener) {
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> csvRows = new ArrayList<>();

                // Create CSV header
                csvRows.add("email,uid,name,onlineStatus,typingTo,phone,image,cover,userType");

                // Iterate through each user's data
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Log.d("Debug", "User Snapshot: " + userSnapshot.toString());
                    Log.d("Debug", "Loop iteration for user: " + userSnapshot.getKey());
                    String email = userSnapshot.child("email").getValue(String.class);
                    Log.d("Debug", "Email for user: " + email);
                    String uid = userSnapshot.child("uid").getValue(String.class);
                    String name = userSnapshot.child("name").getValue(String.class);
                    String onlineStatus = userSnapshot.child("onlineStatus").getValue(String.class);
                    String typingTo = userSnapshot.child("typingTo").getValue(String.class);
                    String phone = userSnapshot.child("phone").getValue(String.class);
                    String image = userSnapshot.child("image").getValue(String.class);
                    String cover = userSnapshot.child("cover").getValue(String.class);
                    String userType = userSnapshot.child("userType").getValue(String.class);

                    // Format the data into a CSV row
                    String csvRow = email + "," + uid + "," + name + "," + onlineStatus + "," + typingTo + ","
                            + phone + "," + image + "," + cover + "," + userType;

                    csvRows.add(csvRow);
                }
                // Join CSV rows with newline
                String csvData = String.join("\n", csvRows);

                if (exportListener != null) {
                    exportListener.onCSVExportSuccess(csvData);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (exportListener != null) {
                    exportListener.onCSVExportFailure("Database error: " + databaseError.getMessage());
                }
            }
        });
    }
}
