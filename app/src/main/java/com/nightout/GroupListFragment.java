package com.nightout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nightout.R;
import com.nightout.adapters.AdapterUsers;
import com.nightout.adapters.AdapterGroup;
import com.nightout.models.ModelGroup;
import com.nightout.models.ModelUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class GroupListFragment extends Fragment {

    // Firebase auth
    private FirebaseAuth firebaseAuth;

    private RecyclerView recyclerView;
    private AdapterGroup adapterGroup;
    private List<ModelGroup> groupList; // List to hold group data

    public GroupListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_list, container, false);

        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize RecyclerView and adapter
        recyclerView = view.findViewById(R.id.groups_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        groupList = new ArrayList<>();

        // Get user's categories and fetch relevant groups
        getUserCategoriesAndFetchGroups();

        return view;
    }

    private void getUserCategoriesAndFetchGroups() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserUid = currentUser.getUid();

            // Fetch matching groups based on individual categories
            fetchCategoriesAndMatchGroups(currentUserUid, "IndividualQuestions");

            // Fetch matching groups based on organization categories
            fetchCategoriesAndMatchGroups(currentUserUid, "OrganizationQuestions");
        }
    }

    private void fetchCategoriesAndMatchGroups(String currentUserUid, String questionType) {
        DatabaseReference userCategoriesRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(currentUserUid)
                .child(questionType);

        userCategoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String answer5 = dataSnapshot.child("answer5").getValue(String.class);

                    // Log the retrieved answer5 value
                    Log.d(questionType + " Answer5", questionType + " Answer5: " + answer5);

                    // Split the answer5 into multiple categories
                    String[] categories = answer5.split(","); // Assuming categories are separated by commas

                    // Fetch matching groups based on user's categories
                    fetchMatchingGroups(categories);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Log.e("FirebaseError", "Firebase Error: " + databaseError.getMessage());
            }
        });
    }

    private void fetchMatchingGroups(String[] categories) {
        DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("Groups");

        groupsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupList.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelGroup group = ds.getValue(ModelGroup.class);

                    if (group != null) {
                        String groupCategory = group.getgCategory();

                        // Check if the group's category matches any of the user's categories
                        for (String category : categories) {
                            if (category.trim().equalsIgnoreCase(groupCategory)) {
                                groupList.add(group);

                                // Log the matching group
                                Log.d("MatchingGroup", "Matched Group: " + group.getgName());

                                break; // Exit the loop once a match is found
                            }
                        }
                    }
                }

                // Initialize and set the adapter with the filtered groups
                adapterGroup = new AdapterGroup(getActivity(), groupList);
                recyclerView.setAdapter(adapterGroup);

                // Log the final list of matched groups
                Log.d("FinalMatchingGroups", "Total Matches: " + groupList.size());
                for (ModelGroup matchedGroup : groupList) {
                    Log.d("FinalMatchingGroups", "Group Name: " + matchedGroup.getgName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Log.e("FirebaseError", "Firebase Error: " + databaseError.getMessage());
            }
        });
    }

    // ... (other methods)
}
