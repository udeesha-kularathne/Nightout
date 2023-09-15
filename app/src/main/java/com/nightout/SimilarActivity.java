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

public class SimilarActivity extends AppCompatActivity {
    private FirebaseUser currentUser; // Declare currentUser as a member variable
    private LinearLayout containerSimilarUsers;
    private LinearLayout containerPostContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similar); // Set the XML layout
        // Initialize Firebase

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e("Authentication Error", "User is not authenticated.");
            return;
        }

        // Initialize LinearLayouts from the XML layout
//        containerSimilarUsers = findViewById(R.id.containerSimilarUsers);
        containerPostContent = findViewById(R.id.containerPostContent);

        // Fetch similar users and process data
        findSimilarUsersAndFetchPosts();
    }

    private void findSimilarUsersAndFetchPosts() {
        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Current user is authenticated
            String currentUserId = currentUser.getUid();

            // Fetch current user's answers from Firebase
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

                        // Sample data: List of users and their answers (replace with your actual data)
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

                                    // Calculate user similarities
                                    Map<String, Double> userSimilarityScores = calculateUserSimilarities(currentUserAnswers, userAnswersMap);

                                    // Process the similar users and their similarity scores
                                    processSimilarUsers(userSimilarityScores);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle error
                                Log.e("Firebase Error", databaseError.getMessage());
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                    Log.e("Firebase Error", databaseError.getMessage());
                }
            });
        } else {
            // User is not authenticated, handle this case accordingly
            Log.e("Authentication Error", "User is not authenticated.");
        }
    }

    // Function to calculate user similarities based on answers
    private Map<String, Double> calculateUserSimilarities(
            Map<String, String> currentUserAnswers,
            Map<String, Map<String, String>> userAnswersMap) {

        Map<String, Double> userSimilarityScores = new HashMap<>();

        // Set the total score to 93.53643564
        double totalScore = 93.53643564;
        int numberOfQuestions = currentUserAnswers.size(); // Number of questions is the size of currentUserAnswers

        // Calculate the score per matching question
        double scorePerQuestion = totalScore / numberOfQuestions;

        for (Map.Entry<String, Map<String, String>> entry : userAnswersMap.entrySet()) {
            String userId = entry.getKey();
            Map<String, String> otherUserAnswers = entry.getValue();

            // Initialize similarity score
            double similarityScore = 0.00000000;

            // Compare answers for each question
            for (Map.Entry<String, String> questionEntry : currentUserAnswers.entrySet()) {
                String question = questionEntry.getKey();
                String currentUserAnswer = questionEntry.getValue();
                String otherUserAnswer = otherUserAnswers.get(question);

                // If answers match, increment the similarity score by scorePerQuestion
                if (currentUserAnswer != null && currentUserAnswer.equals(otherUserAnswer)) {
                    similarityScore += scorePerQuestion;
                }
            }

            // Store the similarity score for the user
            userSimilarityScores.put(userId, similarityScore);
        }

        return userSimilarityScores;
    }

    // Function to process the similar users and their similarity scores
    // Function to process the similar users and their similarity scores
    // Function to process the similar users and their similarity scores
    private void processSimilarUsers(Map<String, Double> userSimilarityScores) {
        // Create a list of user entries for sorting
        List<Map.Entry<String, Double>> userList = new ArrayList<>(userSimilarityScores.entrySet());

        // Sort the list in descending order based on similarity scores
        Collections.sort(userList, (entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()));

        int count = 0; // Counter to keep track of displayed users

        for (Map.Entry<String, Double> entry : userList) {
            double similarityScore = entry.getValue();
            String userId = entry.getKey();

            // Skip users with a similarity score of 0 or users beyond the top 10
            if (similarityScore <= 0.0 || count >= 3) {
                break;
            }

            // Skip the current user (you can customize this logic)
            if (currentUser != null && userId.equals(currentUser.getUid())) {
                continue;
            }

            Log.d("SimilarUser", "User ID: " + userId);
            fetchLatestPostForUser(userId);

//            // Create a new TextView for each user and add it to the container
//            TextView textView = new TextView(this);
//            // Format the output to display percentage with two decimal places
//            String formattedPercentage = String.format("%.8f%%", similarityScore);
//            textView.setText("User: " + userId + ", Similarity Score: " + formattedPercentage);
//            containerSimilarUsers.addView(textView);

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

                        // Log post data
                        Log.d("FetchPost", "Post Title: " + pTitle + ", Description: " + pDescr);

                        // Add the post to the layout
                        addPostToLayout(pTitle, pDescr);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Log.e("Firebase Error", databaseError.getMessage());
            }
        });
    }

    private void addPostToLayout(String pTitle, String pDescr) {
        Log.d("AddPost", "Adding post with title: " + pTitle + " and description: " + pDescr);

        // Inflate the row_posts.xml layout for each post
        View postView = getLayoutInflater().inflate(R.layout.row_posts, null);

        // Initialize the TextViews within the post item layout
        TextView postTitleTv = postView.findViewById(R.id.pTitleTv);
        TextView postDescriptionTv = postView.findViewById(R.id.pDescriptionTv);

        // Set the post title and description
        postTitleTv.setText(pTitle);
        postDescriptionTv.setText(pDescr);

        // Add the postView to the containerPostContent in activity_similar.xml
        containerPostContent.addView(postView);
    }


}
