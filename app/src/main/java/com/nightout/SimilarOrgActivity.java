package com.nightout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimilarOrgActivity extends AppCompatActivity {
    private FirebaseUser currentUser;
    private LinearLayout containerPostContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similar_org);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e("Authentication Error", "User is not authenticated.");
            return;
        }

        containerPostContent = findViewById(R.id.containerPostContent);

        findSimilarIndividualUsersAndFetchPosts();
    }

    private void findSimilarIndividualUsersAndFetchPosts() {
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
                                    Map<String, Map<String, String>> individualUserAnswersMap = new HashMap<>();
                                    Map<String, Map<String, String>> organizationUserAnswersMap = new HashMap<>();
                                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                        String userId = userSnapshot.getKey();
                                        Map<String, String> userAnswers = new HashMap<>();
                                        if (userSnapshot.child("IndividualQuestions").exists()) {
                                            for (DataSnapshot questionSnapshot : userSnapshot.child("IndividualQuestions").getChildren()) {
                                                String questionKey = questionSnapshot.getKey();
                                                String answer = questionSnapshot.getValue(String.class);
                                                userAnswers.put(questionKey, answer);
                                            }
                                            individualUserAnswersMap.put(userId, userAnswers);
                                        }

                                        // Check if the user has organization questions
                                        if (userSnapshot.child("OrganizationQuestions").exists()) {
                                            for (DataSnapshot questionSnapshot : userSnapshot.child("OrganizationQuestions").getChildren()) {
                                                String questionKey = questionSnapshot.getKey();
                                                String answer = questionSnapshot.getValue(String.class);
                                                userAnswers.put(questionKey, answer);
                                            }
                                            organizationUserAnswersMap.put(userId, userAnswers);
                                        }
                                    }

                                    Map<String, Double> individualUserSimilarityScores = calculateUserSimilarities(currentUserAnswers, individualUserAnswersMap);
                                    Map<String, Double> organizationUserSimilarityScores = calculateUserSimilarities(currentUserAnswers, organizationUserAnswersMap);

                                    processSimilarUsers(individualUserSimilarityScores, organizationUserSimilarityScores);
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
        double totalScore = 100.0;

        int numberOfQuestions = currentUserAnswers.size();
        double scorePerQuestion = totalScore / numberOfQuestions;

        for (Map.Entry<String, Map<String, String>> entry : userAnswersMap.entrySet()) {
            String userId = entry.getKey();
            Map<String, String> otherUserAnswers = entry.getValue();

            double similarityScore = 0.0;

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

    private void processSimilarUsers(
            Map<String, Double> individualUserSimilarityScores,
            Map<String, Double> organizationUserSimilarityScores) {
        // Process individual users and organization users separately
        processIndividualUsers(individualUserSimilarityScores);
        processOrganizationUsers(organizationUserSimilarityScores);
    }

    private void processIndividualUsers(Map<String, Double> userSimilarityScores) {
        List<Map.Entry<String, Double>> userList = new ArrayList<>(userSimilarityScores.entrySet());

        Collections.sort(userList, (entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()));

        int count = 0;

        for (Map.Entry<String, Double> entry : userList) {
            double similarityScore = entry.getValue();
            String userId = entry.getKey();

            if (similarityScore <= 0.0 || count >= 3) {
                break;
            }

            if (currentUser != null && userId.equals(currentUser.getUid())) {
                continue;
            }

            Log.d("SimilarIndividualUser", "Individual User ID: " + userId);
            fetchLatestPostForUser(userId);

            count++;
        }
    }

    private void processOrganizationUsers(Map<String, Double> userSimilarityScores) {
        List<Map.Entry<String, Double>> userList = new ArrayList<>(userSimilarityScores.entrySet());

        Collections.sort(userList, (entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()));

        int count = 0;

        for (Map.Entry<String, Double> entry : userList) {
            double similarityScore = entry.getValue();
            String userId = entry.getKey();

            if (similarityScore <= 0.0 || count >= 3) {
                break;
            }

            if (currentUser != null && userId.equals(currentUser.getUid())) {
                continue;
            }

            Log.d("SimilarOrgUser", "Organization User ID: " + userId);
            fetchLatestPostForUser(userId);

            count++;
        }
    }

    private void fetchLatestPostForUser(String uId) {
        Log.d("FetchPost", "Fetching latest post for user ID: " + uId);

        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("Posts");

        Query query = postsRef.orderByChild("uid").equalTo(uId).limitToLast(1);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String pDescr = postSnapshot.child("pDescr").getValue(String.class);
                        String pTitle = postSnapshot.child("pTitle").getValue(String.class);

                        Log.d("FetchPost", "Post Title: " + pTitle + ", Description: " + pDescr);

                        addPostToLayout(pTitle, pDescr);
                    }
                } else {
                    // Log a message if no posts were found for the user
                    Log.d("FetchPost", "No posts found for user ID: " + uId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase Error", databaseError.getMessage());
            }
        });
    }

    private void addPostToLayout(String pTitle, String pDescr) {
        Log.d("AddPost", "Adding post with title: " + pTitle + " and description: " + pDescr);

        View postView = getLayoutInflater().inflate(R.layout.row_posts, null);

        TextView postTitleTv = postView.findViewById(R.id.pTitleTv);
        TextView postDescriptionTv = postView.findViewById(R.id.pDescriptionTv);

        postTitleTv.setText(pTitle);
        postDescriptionTv.setText(pDescr);

        containerPostContent.addView(postView);
    }
}
