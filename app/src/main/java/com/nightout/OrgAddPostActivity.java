package com.nightout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class OrgAddPostActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference userDbRef;

    ActionBar actionBar;

    //permissions constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;

    //image pick constants
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    //    permissions array
    String[] cameraPermissions;
    String[] storagePermissions;

    //views
    EditText titleEt, descriptionEt, dateEditText, timeEditText;
    ImageView imageIv;
    Button uploadBtn;

    //user info
    String name, email, uid, dp, userType, verified;

    //image picked will be saved in this uri
    Uri image_rui = null;

    //progress bar
    ProgressDialog pd;


    RadioGroup radioInOrOut, radioDayOrNight;
    RadioButton radioDay, radioNight, radioIndoor, radioOutdoor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_add_post);

        Spinner locationSpinner = findViewById(R.id.locationSpinner);

        // Create an ArrayAdapter using the string array and a default spinner layout

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.location_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        locationSpinner.setAdapter(adapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the ActionBar reference
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Add New Event");
            // Enable back button in the action bar
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Find the EditText field for date by its ID
//
//        dateEditText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDatePickerDialog();
//            }
//        });



        //init permissions arrays
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        pd = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUserStatus();

        actionBar.setSubtitle(email);

        //get some info of current user to include in post
        userDbRef = FirebaseDatabase.getInstance().getReference("Users");
        Query query = userDbRef.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    name = "" + ds.child("name").getValue();
                    email = "" + ds.child("email").getValue();
                    dp = "" + ds.child("image").getValue();
                    userType = "" + ds.child("userType").getValue();
                    verified = "" + ds.child("verified").getValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //init views
        titleEt = findViewById(R.id.pTitleEt);
        descriptionEt = findViewById(R.id.pDescriptionEt);
        imageIv = findViewById(R.id.pImageIv);
        radioDayOrNight = findViewById(R.id.radioDayOrNight);
        radioDay = findViewById(R.id.radioDay);
        radioNight = findViewById(R.id.radioNight);
        radioInOrOut = findViewById(R.id.radioInOrOut);
        radioIndoor = findViewById(R.id.radioIndoor);
        radioOutdoor = findViewById(R.id.radioOutdoor);
        uploadBtn = findViewById(R.id.pUploadBtn);
        dateEditText = findViewById(R.id.pDateEt);
        timeEditText = findViewById(R.id.pTimeEt);










// Spinner for location selection
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected item from the spinner
                String selectedLocation = parentView.getItemAtPosition(position).toString();
                // Upload selectedLocation to the database or do something with it
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle case when nothing is selected
            }
        });






        //get image from camera/gallery on click
        imageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show image pick dialog
                showImagePickDialog();
            }
        });


        //upload button click listener
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get data(title, description) from EditTexts
                String date = dateEditText.getText().toString().trim();
                String time = timeEditText.getText().toString().trim();
                String title = titleEt.getText().toString().trim();
                String description = descriptionEt.getText().toString().trim();
                String location = locationSpinner.getSelectedItem().toString().trim();

                if (location.equals("Select The City")) {
                    Toast.makeText(OrgAddPostActivity.this, "Please select the location.", Toast.LENGTH_SHORT).show();
                    return; // Return to exit the function and prevent further execution
                }


                if (TextUtils.isEmpty(title)) {
                    Toast.makeText(OrgAddPostActivity.this, "Enter Title...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(description)) {
                    Toast.makeText(OrgAddPostActivity.this, "Enter description...", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Check which radio button is selected
                boolean postToDayNight = radioDay.isChecked();

                boolean postToInOrOut = radioIndoor.isChecked();



                if (image_rui == null) {
                    //post without image
                    uploadData(title, description, "noImage", date, time, location, postToDayNight, postToInOrOut);
                } else {
                    //post with image
                    uploadData(title, description, String.valueOf(image_rui), date, time, location, postToDayNight, postToInOrOut);
                }
            }
        });
    }




    private void uploadData(String title, String description, String uri, String date, String time, String location, boolean postToDayOrNight, boolean postToInOrOut) {
        pd.setMessage("Publishing post...");
        pd.show();

        //for post-image name, post-id, post-publish-time
        String timeStamp = String.valueOf(System.currentTimeMillis());

        String eDayOrNight = postToDayOrNight ? "Day time" : "Night time"; // Convert boolean to "Group" or "Home"
        String eInOrOut = postToInOrOut ? "Indoor" : "Outdoor"; // Convert boolean to "Group" or "Home"

        String filePathAndName = "Posts/" + "post_" + timeStamp;

        if (!uri.equals("noImage")) {
            //post with image
            Bitmap resizedBitmap = resizeImage(Uri.parse(uri));

            if (resizedBitmap != null) {
                // Convert the resized Bitmap to a ByteArrayOutputStream
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                byte[] resizedImageData = baos.toByteArray();

                StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
                ref.putFile(Uri.parse(uri))
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //image is uploaded to firebase storage, now get it's url
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uriTask.isSuccessful()) ;

                                String downloadUri = uriTask.getResult().toString();

                                if (uriTask.isSuccessful()) {
                                    //url is received upload post to firebase database

                                    HashMap<Object, String> hashMap = new HashMap<>();
                                    //put post info
                                    hashMap.put("uid", uid);
                                    hashMap.put("uName", name);
                                    hashMap.put("uEmail", email);
                                    hashMap.put("uDp", dp);
                                    hashMap.put("eId", timeStamp);
                                    hashMap.put("eTitle", title);
                                    hashMap.put("eDescr", description);
                                    hashMap.put("eImage", downloadUri);
                                    hashMap.put("eTime", timeStamp);
                                    hashMap.put("eDayorNight", postToDayOrNight ? "Day time" : "Night Time");
                                    hashMap.put("eInOrOut", postToInOrOut ? "Indoor" : "Outdoor");
                                    hashMap.put("location",location);
                                    hashMap.put("userType", userType);
                                    hashMap.put("verified", verified);
                                    hashMap.put("date", date);
                                    hashMap.put("time", time);
                                    hashMap.put("interested", "");
                                    hashMap.put("going", "");
                                    hashMap.put("participated", "");
                                    hashMap.put("eLike", "");
                                    hashMap.put("eComment", "");
                                    hashMap.put("eShare", "");

                                    //path to store post data
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Events");
                                    //put data in this ref
                                    ref.child(timeStamp).setValue(hashMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    //added in database
                                                    pd.dismiss();
                                                    Toast.makeText(OrgAddPostActivity.this, "Post published", Toast.LENGTH_SHORT).show();
                                                    //reset views
                                                    titleEt.setText("");
                                                    descriptionEt.setText("");
                                                    imageIv.setImageURI(null);
                                                    image_rui = null;

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    //failed adding post in database
                                                    pd.dismiss();
                                                    Toast.makeText(OrgAddPostActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //failed uploading image
                                pd.dismiss();
                                Toast.makeText(OrgAddPostActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        } else {
            //post without image

            HashMap<Object, String> hashMap = new HashMap<>();
            //put post info
            hashMap.put("uid", uid);
            hashMap.put("uName", name);
            hashMap.put("uEmail", email);
            hashMap.put("uDp", dp);
            hashMap.put("eId", timeStamp);
            hashMap.put("eTitle", title);
            hashMap.put("eDescr", description);
            hashMap.put("eImage", "noImage");
            hashMap.put("eTime", timeStamp);
            hashMap.put("eDayorNight", postToDayOrNight ? "Day time" : "Outdoor");
            hashMap.put("eInOrOut", postToInOrOut ? "Indoor" : "Outdoor");
            hashMap.put("location",location);
            hashMap.put("userType", userType);
            hashMap.put("verified", verified);
            hashMap.put("date", date);
            hashMap.put("time", time);
            hashMap.put("interested", "");
            hashMap.put("going", "");
            hashMap.put("participated", "");
            hashMap.put("eLike", "");
            hashMap.put("eComment", "");
            hashMap.put("eShare", "");


            //path to store post data
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Events");
            //put data in this ref
            ref.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //added in database
                            pd.dismiss();
                            Toast.makeText(OrgAddPostActivity.this, "Post published", Toast.LENGTH_SHORT).show();
                            titleEt.setText("");
                            descriptionEt.setText("");
                            imageIv.setImageURI(null);
                            image_rui = null;

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed adding post in database
                            pd.dismiss();
                            Toast.makeText(OrgAddPostActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private Bitmap resizeImage(Uri uri) {
        try {
            // Load the image from the URI
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);

            // Calculate the new dimensions while maintaining the original aspect ratio
            int originalWidth = originalBitmap.getWidth();
            int originalHeight = originalBitmap.getHeight();

            int targetWidth = 300; // Set your desired width
            int targetHeight = (int) ((float) targetWidth * originalHeight / originalWidth);

            // Resize the image
            return Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showImagePickDialog() {
        //options(camera, gallery) to show in dialog
        String[] options = {"Camera", "Gallery"};

        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image from");
        //set options to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //item click handle
                if (which == 0) {
                    //camera click
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickFromCamera();
                    }
                }
                if (which == 1) {
                    //gallery click
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
                }
            }
        });
        //create and show dialog
        builder.create().show();
    }


    private void pickFromGallery() {
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }


    private void pickFromCamera() {
        //intent to pick image from camera
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Temp Descr");
        image_rui = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_rui);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }


    private boolean checkStoragePermission() {
        //check if storage permission is enabled or not
        //return true if enabled
        //return false if not enabled
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        //request runtime storage permission
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }


    private boolean checkCameraPermission() {
        //check if camera permission is enabled or not
        //return true if enabled
        //return false if not enabled
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission() {
        //request runtime camera permission
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }


    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus();
    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
//            user is signed in stay here
            email = user.getEmail();
            uid = user.getUid();
        } else {
//            user not signed in, go to main activity
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); //goto previous activity
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);


        menu.findItem(R.id.org_action_add_post).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //get item id
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }

    //handle permission results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //this method is called when user press Allow or Deny from permission request dialog
        //here we will handle permission cases (allowed and denied)

        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        //both permission are granted
                        pickFromCamera();
                    } else {
                        //camera or gallery or both permission were denied
//                        Toast.makeText(this, "Camera & Storage both permissions are necessary...", Toast.LENGTH_SHORT).show();
                        pickFromCamera();
                    }
                } else {

                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        //storage permission granted
                        pickFromGallery();
                    } else {
                        //camera or gallery or both permission were denied
//                        Toast.makeText(this, "Storage permissions necessary...", Toast.LENGTH_SHORT).show();
                        pickFromGallery();
                    }
                } else {

                }
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //this method will be called after picking image from camera or gallery
        if (resultCode == RESULT_OK) {

            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //image is picked from gallery, get uri of image
                image_rui = data.getData();

                //set to imageView
                imageIv.setImageURI(image_rui);

                // Adjust the layout parameters of the ImageView
                adjustImageViewLayoutParams();

            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //image is picked from camera, get uri of image

                //set the image to the ImageView
                imageIv.setImageURI(image_rui);

                // Adjust the layout parameters of the ImageView
                adjustImageViewLayoutParams();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void adjustImageViewLayoutParams() {
        // Get the dimensions of the loaded image
        int imageWidth = imageIv.getDrawable().getIntrinsicWidth();
        int imageHeight = imageIv.getDrawable().getIntrinsicHeight();

        // Calculate the desired height based on the image's aspect ratio
        int desiredHeight = (int) ((float) imageIv.getWidth() * imageHeight / imageWidth);

        // Update the ImageView's layout parameters
        ViewGroup.LayoutParams layoutParams = imageIv.getLayoutParams();
        layoutParams.height = desiredHeight;
        imageIv.setLayoutParams(layoutParams);
    }
}