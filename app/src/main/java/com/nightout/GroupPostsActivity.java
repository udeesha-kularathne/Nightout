package com.nightout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nightout.MainActivity;
import com.nightout.adapters.AdapterGroup;
import com.nightout.adapters.AdapterPosts;
import com.nightout.models.ModelGroup;
import com.nightout.models.ModelPost;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class GroupPostsActivity extends AppCompatActivity {

    // Firebase auth
    FirebaseAuth firebaseAuth;

    ImageView imageIv, coverIv, badgeImageView;
    TextView gNameTv, gCategoryTv;
    String groupCategory;
    RecyclerView recyclerView;
    List<ModelGroup> groupList;
    List<ModelPost> postList;
    AdapterGroup adaptergroup;
    AdapterPosts adapterPosts;

    String gid;
    String uid;

//    AdapterPosts adapterPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_post);
        firebaseAuth = FirebaseAuth.getInstance();


        Button addPostButton = findViewById(R.id.addPostButton);

        // Retrieve the group data from the intent
        ModelGroup group = (ModelGroup) getIntent().getSerializableExtra("groupData");

        String groupName = getIntent().getStringExtra("gName");
        groupCategory = getIntent().getStringExtra("gCategory");
        Log.d("meka thama ewwe", "ewanawa: " + groupCategory);

        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the AddGroupPostsActivity
                Intent intent = new Intent(GroupPostsActivity.this, AddGroupPostsActivity.class);
                intent.putExtra("groupCategory", groupCategory); // Pass the group category if needed
                startActivity(intent);
            }
        });

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            // Get the ActionBar reference
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle("Group");
                // Enable back button in the action bar
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }

            // Inside your group fragment when the user clicks the add post button

            imageIv = findViewById(R.id.image);
            coverIv = findViewById(R.id.coverIv);
            gNameTv = findViewById(R.id.gNameTv);
            gCategoryTv = findViewById(R.id.gCategoryTv);
            recyclerView = findViewById(R.id.recyclerview_posts);
            badgeImageView = findViewById(R.id.verificationBadge);

            // Initialize Firebase auth
            firebaseAuth = FirebaseAuth.getInstance();

            // Set the group name and category
            gNameTv.setText(groupName);
            gCategoryTv.setText(groupCategory);

            Query query = FirebaseDatabase.getInstance().getReference("Groups").orderByChild("Competitions").equalTo(gid);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Check if the group object is still not null
                    if (group != null) {
                        //get data
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String name = "" + ds.child("gName").getValue();
                            String category = "" + ds.child("gCategory").getValue();
                            String image = "" + ds.child("image").getValue();
                            String cover = "" + ds.child("gCover").getValue();

                            // Update data
                            gNameTv.setText(name);
                            gCategoryTv.setText(category);

                            try {
                                //if image is received, set it
                                Picasso.get().load(image).into(imageIv);
                            } catch (Exception e) {
                                //if there is any exception while getting image then set default
                                Picasso.get().load(R.drawable.ic_face_img_white).into(imageIv);
                            }

                            try {
                                //if image is received, set it
                                Picasso.get().load(cover).into(coverIv);
                            } catch (Exception e) {
                                // Handle exceptions
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle onCancelled
                }
            });

            // Handle the case where the group object is null
            Log.e("GroupPostsActivity", "Group object is null");


        // Initialize post list
        postList = new ArrayList<>();

        checkUserStatus();
        loadPosts();
    }
    private void loadPosts() {

        //linear layout for recycleview
        LinearLayoutManager layoutManager = new LinearLayoutManager(GroupPostsActivity.this);
        //show newest post first, for this load from last
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set this layout to recycleview
        recyclerView.setLayoutManager(layoutManager);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelPost modelPost = ds.getValue(ModelPost.class);

                    if (modelPost != null && modelPost.getgCategory() != null && modelPost.getgCategory().equalsIgnoreCase(groupCategory)) {
                        postList.add(modelPost);
                    }
                }
                adapterPosts = new AdapterPosts(GroupPostsActivity.this, postList);
                recyclerView.setAdapter(adapterPosts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(GroupPostsActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchPost(String searchQuery) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelPost modelPost = ds.getValue(ModelPost.class);

                    if (modelPost.getpTitle().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            modelPost.getpDescr().toLowerCase().contains(searchQuery.toLowerCase())) {

                        postList.add(modelPost);
                    }
                    adapterPosts = new AdapterPosts(GroupPostsActivity.this, postList);
                    recyclerView.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(GroupPostsActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // User is signed in, stay here
        } else {
            // User not signed in, go to the main activity
            startActivity(new Intent(GroupPostsActivity.this, MainActivity.class));
            finish();
        }
    }
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        //inflating menu
//        inflater.inflate(R.menu.menu_main, menu);
//
//        //searchView to search posts by post title/description
//        MenuItem item = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
//
//        //search listener
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                //called when user press search button
//                if (!TextUtils.isEmpty(s)) {
//                    searchPost(s);
//                } else {
//                    loadPosts();
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                //called as and when user press any letter
//                if (!TextUtils.isEmpty(s)) {
//                    searchPost(s);
//                } else {
//                    loadPosts();
//                }
//                return false;
//            }
//        });
//
//        super.onCreateOptionsMenu(menu);
//    }
//    //handle menu item clicks
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        //get item id
//        int id = item.getItemId();
//        if (id == R.id.action_logout) {
//            firebaseAuth.signOut();
//            checkUserStatus();
//        }
//        if (id == R.id.action_add_post) {
//            startActivity(new Intent(this, AddPostActivity.class));
//
//        }
//        return super.onOptionsItemSelected(item);
//    }

}