package com.nightout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nightout.notification.Token;

public class DashboardActivity extends AppCompatActivity {

    //firebase auth
    FirebaseAuth firebaseAuth;
    ActionBar actionBar;
    FirebaseUser user;
    String mUID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //        actionbar and its title
//        ActionBar actionbar = getSupportActionBar();
//        actionbar.setTitle("Profile");
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setTitle("Profile");
        }

//        init
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        // Log the user information
        if (user != null) {
            String uid = user.getUid();
            String email = user.getEmail();
            Log.d("CurrentUser", "UID: " + uid);
            Log.d("CurrentUser", "Email: " + email);
        } else {
            Log.d("CurrentUser", "No user is signed in.");
        }


        //bottom navigation
        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setOnItemSelectedListener(selectedListener);

        //bottom navigation
        BottomNavigationView navigationView1 = findViewById(R.id.navigation1);
        navigationView1.setOnItemSelectedListener(selectedListener1);


        // Retrieve the user type from the database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        // Listen for changes to the "verified" status
        userRef.child("userType").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get the current value of "verified" from the database
                if (dataSnapshot.exists()) {
                    String verificationStatus = dataSnapshot.getValue(String.class);

                    if (verificationStatus != null) {
                        // Check if the user is verified or not
                        if (verificationStatus.equals("Organization")) {
                            // User is verified, make the button visible
                            navigationView.getMenu().findItem(R.id.nav_users).setVisible(false);
                            navigationView.getMenu().findItem(R.id.nav_chat).setVisible(false);
                        } else {
                            // User is not verified, hide the button
                            navigationView.getMenu().findItem(R.id.nav_users).setVisible(true);
                            navigationView.getMenu().findItem(R.id.nav_chat).setVisible(true);
                        }
                    } else {
                        // Handle the case where 'verificationStatus' is null
                    }
                } else {
                    // Handle the case where 'dataSnapshot' doesn't exist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });




//home fragment transaction (default, on star)
        assert actionbar!=null;
                actionbar.setTitle("Home"); // Change actionbar title
                HomeFragment fragment1=new HomeFragment();
                FragmentTransaction ft1=getSupportFragmentManager().beginTransaction();
                ft1.replace(R.id.content,fragment1,"");
                ft1.commit();

                checkUserStatus();
                //update token

// Update token
                FirebaseUser currentUser=FirebaseAuth.getInstance().getCurrentUser();
                if(currentUser!=null){
                String mUID=currentUser.getUid();


                // Update token
                FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task->{
                if(task.isSuccessful()){
                String token=task.getResult();
                updateToken(token,mUID); // Use the class-level mUID variable
                }
                });
                }
                }

@Override
protected void onResume(){
        checkUserStatus();
        super.onResume();
        }


public void updateToken(String token,String userId){
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken=new Token(token);
        ref.child(userId).setValue(mToken);
        }
private NavigationBarView.OnItemSelectedListener selectedListener=
        new NavigationBarView.OnItemSelectedListener(){
@Override
public boolean onNavigationItemSelected(@NonNull MenuItem item){
        ActionBar actionbar=getSupportActionBar(); // Declare actionbar here

        if(actionbar!=null){
        switch(item.getItemId()){
        case R.id.nav_home:
        // Home fragment transaction
        actionbar.setTitle("Home"); // Change actionbar title
        HomeFragment fragment1=new HomeFragment();
        FragmentTransaction ft1=getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content,fragment1,"");
        ft1.commit();
        return true;
        case R.id.nav_profile:
        // Profile fragment transaction
        actionbar.setTitle("Profile"); // Change actionbar title
        ProfileFragment fragment2=new ProfileFragment();
        FragmentTransaction ft2=getSupportFragmentManager().beginTransaction();
        ft2.replace(R.id.content,fragment2,"");
        ft2.commit();
        return true;
        case R.id.nav_users:
        // Users fragment transaction
        actionbar.setTitle("People you may know"); // Change actionbar title
        contentsimilarFragment fragment3=new contentsimilarFragment();
        FragmentTransaction ft3=getSupportFragmentManager().beginTransaction();
        ft3.replace(R.id.content,fragment3,"");
        ft3.commit();
        return true;

//
//                            case R.id.nav_users:
//                                // Start SimilarActivity
//                                Intent similarIntent1 = new Intent(DashboardActivity.this, AddGroupPostsActivity.class);
//                                startActivity(similarIntent1);

//
//                            case R.id.nav_chat:
//                                // Start SimilarActivity
//                                Intent similarIntent = new Intent(DashboardActivity.this, SimilarOrgActivity.class);
//                                startActivity(similarIntent);
////                                return true;

        case R.id.nav_chat:
        // Users fragment transaction
        actionbar.setTitle("Recommend for you");
        CustomContentFragment fragment4=new CustomContentFragment();
        FragmentTransaction ft4=getSupportFragmentManager().beginTransaction();
        ft4.replace(R.id.content,fragment4,"");
        ft4.commit();
        return true;

//                                case R.id.nav_chat:
//                                // Users fragment transaction
//                                actionbar.setTitle("Custom Content");
//                                VerifiedPostsFragment fragment4 = new VerifiedPostsFragment();
//                                FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
//                                ft4.replace(R.id.content, fragment4, "");
//                                ft4.commit();
//                                return true;


        case R.id.nav_groups:
        // Groups fragment transaction
        actionbar.setTitle("Communities"); // Change actionbar title
        GroupListFragment fragment5=new GroupListFragment();
        FragmentTransaction ft5=getSupportFragmentManager().beginTransaction();
        ft5.replace(R.id.content,fragment5); // Replace R.id.fragmentContainer with your actual container ID
        ft5.commit();
        return true;
//
//                            case R.id.nav_groups:
//                                // Start SimilarActivity
//                                Intent similarIntent1 = new Intent(DashboardActivity.this, GroupListActivity.class);
//                                startActivity(similarIntent1);
//                                return true;


        }
        }

        return false;
        }
        };

private NavigationBarView.OnItemSelectedListener selectedListener1=
        new NavigationBarView.OnItemSelectedListener(){
@Override
public boolean onNavigationItemSelected(@NonNull MenuItem item){
        ActionBar actionbar=getSupportActionBar(); // Declare actionbar here

        if(actionbar!=null){
        switch(item.getItemId()){
//                            case R.id.nav_home:
//                                // Home fragment transaction
//                                actionbar.setTitle("Home"); // Change actionbar title
//                                HomeFragment fragment1 = new HomeFragment();
//                                FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
//                                ft1.replace(R.id.content, fragment1, "");
//                                ft1.commit();
//                                return true;
        case R.id.nav_profile:
        // Profile fragment transaction
        case R.id.nav_groups:
        // Start SimilarActivity
        Intent similarIntent1=new Intent(DashboardActivity.this,DashboardGraphActivity.class);
        startActivity(similarIntent1);
        return true;
        case R.id.nav_orgevents:
        // Groups fragment transaction
        actionbar.setTitle("Events"); // Change actionbar title
        OrganizerEventsFragment fragment8=new OrganizerEventsFragment();
        FragmentTransaction ft8=getSupportFragmentManager().beginTransaction();
        ft8.replace(R.id.content,fragment8); // Replace R.id.fragmentContainer with your actual container ID
        ft8.commit();
        return true;


        case R.id.nav_customcontent:
        // Users fragment transaction
        actionbar.setTitle("Review");
        ReviewFragment fragment9=new ReviewFragment();
        FragmentTransaction ft9=getSupportFragmentManager().beginTransaction();
        ft9.replace(R.id.content,fragment9,"");
        ft9.commit();
        return true;

        case R.id.nav_similar:
        // Users fragment transaction
        actionbar.setTitle("Explore");
        VerifiedPostsFragment fragment10=new VerifiedPostsFragment();
        FragmentTransaction ft10=getSupportFragmentManager().beginTransaction();
        ft10.replace(R.id.content,fragment10,"");
        ft10.commit();
        return true;


        }
        }

        return false;
        }
        };


private void checkUserStatus(){
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user!=null){
//            user is signed in stay here
        //set email of logged in user
        //mProfileTv.setText(user.getEmail());
        mUID=user.getUid();

        SharedPreferences sp=getSharedPreferences("SP_USER",MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("Current_USERID",mUID);
        editor.apply();
        }
        else{
//            user not signed in, go to main activity
        startActivity(new Intent(DashboardActivity.this,MainActivity.class));
        finish();
        }
        }

@Override
public void onBackPressed(){
        super.onBackPressed();
        finish();
        }

@Override
protected void onStart(){
        //check on start of app
        checkUserStatus();
        super.onStart();
        }


        }