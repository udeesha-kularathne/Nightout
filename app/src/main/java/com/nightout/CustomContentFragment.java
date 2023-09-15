package com.nightout;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomContentFragment extends Fragment {
    private FirebaseUser currentUser;
    private LinearLayout containerPostContent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e("Authentication Error", "User is not authenticated.");
            return;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_custom_content, container, false);
        containerPostContent = rootView.findViewById(R.id.containerPostContent);

        findSimilarIndividualUsersAndFetchPosts();

        return rootView;
    }

    private void findSimilarIndividualUsersAndFetchPosts() {
        Context context = getContext();
        if (context == null) {
            Log.e("Context Error", "Context is null.");
            return;
        }

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
        double totalScore = 93.4356343;

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

            if (similarityScore <= 0.0 || count >= 10) {
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

            if (similarityScore <= 0.0 || count >= 10) {
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

        Query query = postsRef.orderByChild("uid").equalTo(uId).limitToLast(5);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String pDescr = postSnapshot.child("pDescr").getValue(String.class);
                        String pTitle = postSnapshot.child("pTitle").getValue(String.class);
                        String pImage = postSnapshot.child("pImage").getValue(String.class);
                        String uDp = postSnapshot.child("uDp").getValue(String.class);
                        String uName = postSnapshot.child("uName").getValue(String.class);

                        Log.d("FetchPost", "Post Title: " + pTitle + ", Description: " + pDescr);

                        addPostToLayout(pTitle, pDescr, uName, uDp, pImage);
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

    private void addPostToLayout(String pTitle, String pDescr, String uName, String uDp, String pImage) {
        Log.d("AddPost", "Adding post with title: " + pTitle + " and description: " + pDescr);

        View postView = getLayoutInflater().inflate(R.layout.row_posts, null);

        TextView postTitleTv = postView.findViewById(R.id.pTitleTv);
        TextView postDescriptionTv = postView.findViewById(R.id.pDescriptionTv);
        ImageView postImageIv = postView.findViewById(R.id.pImageIv);
        ImageView postAvatarIv = postView.findViewById(R.id.uPictureIv);
        TextView postNameTv = postView.findViewById(R.id.uNameTv);

        postTitleTv.setText(pTitle);
        postDescriptionTv.setText(pDescr);
        postNameTv.setText(uName);


        try {
            //if image is received the set
            Picasso.get().load(uDp).into(postAvatarIv);
            Picasso.get().load(pImage).into(postImageIv);
        }
        //if there is any exception while getting image then set default
        catch (Exception e) {
            Picasso.get().load(R.drawable.ic_profile_image).into(postAvatarIv);

        }

        containerPostContent.addView(postView);
    }
}
