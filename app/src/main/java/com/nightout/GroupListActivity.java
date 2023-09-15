package com.nightout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class GroupListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        // Find the first CardView by its ID
        CardView cardView1 = findViewById(R.id.cardView1);

        // Set an OnClickListener to the first CardView
        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Perform the action you want when the first CardView is clicked
                // For example, start a new activity
                Intent intent = new Intent(GroupListActivity.this, GroupPostsActivity.class);
                startActivity(intent);
            }
        });
    }
}