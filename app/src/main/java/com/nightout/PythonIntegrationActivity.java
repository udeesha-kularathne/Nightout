//package com.nightout;
//
//import android.os.Bundle;
//import androidx.appcompat.app.AppCompatActivity;
//import com.chaquo.python.PyObject;
//import com.chaquo.python.Python;
//import com.chaquo.python.android.AndroidPlatform;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.nightout.R;
//
//import java.nio.charset.StandardCharsets;
//
//public class PythonIntegrationActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_python_integration);
//
//        if (!Python.isStarted()) {
//            Python.start(new AndroidPlatform(this));
//        }
//
//        // Initialize Firebase
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageRef = storage.getReference();
//
//        // Get a reference to the CSV file in Firebase Storage
//        StorageReference csvFileRef = storageRef.child("behavior.csv");
//
//        // Download the CSV data
//        csvFileRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                // Convert byte array to string using UTF-8 encoding
//                String csvData = new String(bytes, StandardCharsets.UTF_8);
//
//                // Load Python module and call function
//                Python python = Python.getInstance();
//                PyObject module = python.getModule("behavior_classifier");
//                PyObject trainAndPredictFunction = module.get("train_and_predict");
//
//                // Call the Python function with the CSV data
//                PyObject result = trainAndPredictFunction.call(csvData);
//
//                // Extract prediction and accuracy from the result
//                PyObject predictionObj = result.get(0);
//                PyObject accuracyObj = result.get(1);
//
//                System.out.println("Prediction Object Type: " + predictionObj.getClass().getName());
//                System.out.println("Accuracy Object Type: " + accuracyObj.getClass().getName());
//
//                String prediction = predictionObj.toString();
//                double accuracy = accuracyObj.toDouble();
//
//                System.out.println("Predicted behavior: " + prediction);
//                System.out.println("Accuracy: " + accuracy);
//
//                System.out.println("Predicted behavior: " + prediction);
//                System.out.println("Accuracy: " + accuracy);
//            }
//        });
//    }
//}
