package com.nightout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nightout.adapters.AdapterUsers;
import com.nightout.models.ModelUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class contentsimilarFragment extends Fragment {
    private FirebaseUser currentUser;
    private RecyclerView recyclerView; // Container to display similar users
    private AdapterUsers adapterUsers; // Adapter for displaying user details
    private List<ModelUser> userList; // List to store user details

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contentsimilar, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.users_recyclerView);

        // Initialize LayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e("Authentication Error", "User is not authenticated.");
            return view;
        }

        // Initialize user list
        userList = new ArrayList<>();

        // Fetch similar users and their details
        findSimilarUsersAndFetchDetails();

        return view;
    }

    private void findSimilarUsersAndFetchDetails() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            DatabaseReference currentUserAnswersRef = database.getReference("Users")
                    .child(currentUserId)
                    .child("IndividualQuestions");

            currentUserAnswersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Map<String, String> currentUserAnswers = new HashMap<>();
                        for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
                            String questionKey = questionSnapshot.getKey();
                            String answer = questionSnapshot.getValue(String.class);
                            currentUserAnswers.put(questionKey, answer);
                        }

                        DatabaseReference usersAnswersRef = database.getReference("Users");
                        usersAnswersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Map<String, Map<String, String>> userAnswersMap = new HashMap<>();
                                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                        String userId = userSnapshot.getKey();
                                        Map<String, String> userAnswers = new HashMap<>();
                                        if (userSnapshot.child("IndividualQuestions").exists()) {
                                            for (DataSnapshot questionSnapshot : userSnapshot.child("IndividualQuestions").getChildren()) {
                                                String questionKey = questionSnapshot.getKey();
                                                String answer = questionSnapshot.getValue(String.class);
                                                userAnswers.put(questionKey, answer);
                                            }
                                            userAnswersMap.put(userId, userAnswers);
                                        }
                                    }

                                    Map<String, Double> userSimilarityScores = calculateUserSimilarities(currentUserAnswers, userAnswersMap);
                                    processSimilarUsers(userSimilarityScores);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e("Firebase Error", databaseError.getMessage());
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Firebase Error", databaseError.getMessage());
                }
            });
        } else {
            Log.e("Authentication Error", "User is not authenticated.");
        }
    }

    private Map<String, Double> calculateUserSimilarities(
            Map<String, String> currentUserAnswers,
            Map<String, Map<String, String>> userAnswersMap) {
        Map<String, Double> userSimilarityScores = new HashMap<>();
        double totalScore = 93.53643564;
        int numberOfQuestions = currentUserAnswers.size();
        double scorePerQuestion = totalScore / numberOfQuestions;

        for (Map.Entry<String, Map<String, String>> entry : userAnswersMap.entrySet()) {
            String userId = entry.getKey();
            Map<String, String> otherUserAnswers = entry.getValue();
            double similarityScore = 0.00000000;

            for (Map.Entry<String, String> questionEntry : currentUserAnswers.entrySet()) {
                String question = questionEntry.getKey();
                String currentUserAnswer = questionEntry.getValue();
                String otherUserAnswer = otherUserAnswers.get(question);

                if (currentUserAnswer != null && currentUserAnswer.equals(otherUserAnswer)) {
                    similarityScore += scorePerQuestion;
                }
            }

            userSimilarityScores.put(userId, similarityScore);
        }

        return userSimilarityScores;
    }

    private void processSimilarUsers(Map<String, Double> userSimilarityScores) {
        List<Map.Entry<String, Double>> userList = new ArrayList<>(userSimilarityScores.entrySet());
        Collections.sort(userList, (entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()));
        int count = 0;

        for (Map.Entry<String, Double> entry : userList) {
            double similarityScore = entry.getValue();
            String userId = entry.getKey();

            if (similarityScore <= 0.0 || count >= 10) {
                break;
            }

            if (currentUser != null && userId.equals(currentUser.getUid())) {
                continue;
            }

            Log.d("SimilarUser", "User ID: " + userId + ", Similarity Score: " + similarityScore);

            // Fetch and display user information
            fetchUserInfoAndDisplay(userId);

            count++;
        }
    }




    private void fetchUserInfoAndDisplay(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.child("name").getValue(String.class);
                    String userEmail = dataSnapshot.child("email").getValue(String.class);

                    // Log user information
                    Log.d("UserInfo", "User ID: " + userId + ", Name: " + userName + ", Email: " + userEmail);

                    // Display user information
                    displayUserInfo(userName, userEmail);
                } else {
                    // User data does not exist
                    Log.e("UserInfo", "User ID: " + userId + " data does not exist.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log any database error
                Log.e("Firebase Error", "Error fetching user data: " + databaseError.getMessage());
            }
        });
    }


    private void displayUserInfo(String userName, String userEmail) {
        // Create a new ModelUser instance to store user details
        ModelUser modelUser = new ModelUser();
        modelUser.setName(userName);
        modelUser.setEmail(userEmail);

        // Add the user details to the user list
        userList.add(modelUser);

        // Initialize or update the adapter
        if (adapterUsers == null) {
            adapterUsers = new AdapterUsers(getActivity(), userList);
            recyclerView.setAdapter(adapterUsers);
        } else {
            adapterUsers.notifyDataSetChanged();
        }
    }
}
