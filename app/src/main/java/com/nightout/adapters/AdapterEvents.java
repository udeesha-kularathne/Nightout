package com.nightout.adapters;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nightout.AddPostActivity;
import com.nightout.R;
import com.nightout.ReviewAddPostActivity;
import com.nightout.ThereProfileActivity;
import com.nightout.models.ModelEvent;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.logging.Handler;

public class AdapterEvents extends RecyclerView.Adapter<AdapterEvents.MyHolder> {
    Context context;
    List<ModelEvent> eventList;

    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
    public AdapterEvents(Context context, List<ModelEvent> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public AdapterEvents.MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate layout row_post.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_events, viewGroup, false);

        return new AdapterEvents.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterEvents.MyHolder myHolder, int i) {
        //get data
        String uid = eventList.get(i).getUid();
        String uEmail = eventList.get(i).getuEMail();
        String uName = eventList.get(i).getuName();
        String uDp = eventList.get(i).getuDp();
        String eId = eventList.get(i).geteId();
        String eTitle = eventList.get(i).geteTitle();
        String eDescription = eventList.get(i).geteDescr();
        String eImage = eventList.get(i).geteImage();
        String eTimeStamp = eventList.get(i).geteTime();
        String date = eventList.get(i).getDate();
        String time = eventList.get(i).getTime();
        String eGroup = eventList.get(i).geteGroup();
        String verified = eventList.get(i).getVerified();
        String interested = eventList.get(i).getInterested();
        String going = eventList.get(i).getGoing();
        String participated = eventList.get(i).getParticipated();
        String dayOrnight = eventList.get(i).geteDayorNight();
        String inOrout = eventList.get(i).geteInOrOut();
        String location = eventList.get(i).getLocation();

// Convert timestamp to dd/MM/yyyy hh:mm aa (e.g., 01/09/2023 01:30 PM)
        // Convert timestamp to dd/MM/yyyy hh:mm aa (e.g., 01/09/2023 01:30 PM)
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(eTimeStamp));
        String eTime = DateFormat.format("dd/mm/yyyy hh:mm aa", calendar).toString();



        //set data
        myHolder.uNameTv.setText(uName);
        myHolder.pTimeTv.setText(eTime);
        myHolder.pTitleTv.setText(eTitle);
        myHolder.eTimeTv.setText("Time:" + time);
        myHolder.eDateTv.setText("Date:" + date);
        myHolder.pDescriptionTv.setText(eDescription);
        myHolder.eLocationTv.setText("Location:" + location);
        myHolder.eDayOrNightTv.setText(dayOrnight);
        myHolder.eInOrOutTv.setText(inOrout);
        myHolder.eIdTv.setText(eId);

        // Retrieve user verification status from the Users data
        usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String verified = dataSnapshot.child("verified").getValue(String.class);
                    if (verified != null && verified.equals("verified")) {
                        // User is verified, make the badge visible
                        myHolder.verificationBadge.setVisibility(View.VISIBLE);
                    } else {
                        // User is not verified, hide the badge
                        myHolder.verificationBadge.setVisibility(View.GONE);
                    }
                } else {
                    // Handle the case where the user data does not exist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });


        //set user dp
        try {
            Picasso.get().load(uDp).placeholder(R.drawable.ic_profile_image).into(myHolder.uPictureIv);
        } catch (Exception e) {

        }

        //set post image
        //if there is no image i.e. pImage.equals("noImage") then hide ImageVIEW
        if (eImage.equals("noImage")) {
            //hide imageView
            myHolder.pImageIv.setVisibility(View.GONE);

        } else {
            try {
                Picasso.get().load(eImage).into(myHolder.pImageIv);
            } catch (Exception e) {

            }

        }


        //handle button clicks
        myHolder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //will implement later
                Toast.makeText(context, "More", Toast.LENGTH_SHORT).show();
            }
        });
//        myHolder.likeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //will implement later
//                Toast.makeText(context, "Like", Toast.LENGTH_SHORT).show();
//            }
//        });
//        myHolder.commentBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //will implement later
//                Toast.makeText(context, "Comment", Toast.LENGTH_SHORT).show();
//            }
//        });
//        myHolder.shareBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //will implement later
//                Toast.makeText(context, "Share", Toast.LENGTH_SHORT).show();
//            }
//        });
        myHolder.interestedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = myHolder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    // You have the current position, and you can use it as needed
                    // For example, you can access your data list with dataList.get(currentPosition)

                    // Change the button color when clicked
                    myHolder.interestedBtn.setBackgroundColor(ContextCompat.getColor(context, R.color.colorFollow));

                    // You can also reset the color after a certain duration if needed
                    myHolder.interestedBtn.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Reset the button color after a delay (if needed)
                            myHolder.interestedBtn.setBackgroundColor(ContextCompat.getColor(context, R.color.colorFollow));
                        }
                    }, 1000); // Change back to the original color after 1000 milliseconds (1 second)

                    // Display a toast message
                    Toast.makeText(context, "Interested", Toast.LENGTH_SHORT).show();
                }
            }
        });

        myHolder.goingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //will implement later
                Toast.makeText(context, "Going", Toast.LENGTH_SHORT).show();
            }
        });
        myHolder.participatedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //will implement later
                Toast.makeText(context, "Participated", Toast.LENGTH_SHORT).show();
            }
        });
        myHolder.reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the event ID (eid) of the clicked event
                String clickedEventId = eventList.get(i).geteId();

                // Create an Intent to start the ReviewAddPostActivity
                Intent reviewIntent = new Intent(context, ReviewAddPostActivity.class);

                // Put the event ID as an extra in the Intent
                reviewIntent.putExtra("eventId", clickedEventId);

                // Start the ReviewAddPostActivity
                context.startActivity(reviewIntent);
            }
        });
        myHolder.profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*click to go to ThereProfileActivity with uid, this uid is of clicked user
                 * which will be used to show user specific data/posts*/
                Intent intent = new Intent(context, ThereProfileActivity.class);
                intent.putExtra("uid", uid);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder {

        //views from row_post.xml
        ImageView uPictureIv, pImageIv, verificationBadge;
        TextView uNameTv, pTimeTv, pTitleTv, pDescriptionTv, pLikesTv, eLocationTv, eDayOrNightTv, eInOrOutTv, eTimeTv, eDateTv, eIdTv;
        ImageButton moreBtn;
        Button interestedBtn, goingBtn, participatedBtn, reviewBtn;
        LinearLayout profileLayout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            uPictureIv = itemView.findViewById(R.id.uPictureIv);
            pImageIv = itemView.findViewById(R.id.pImageIv);
            uNameTv = itemView.findViewById(R.id.uNameTv);
            pTimeTv = itemView.findViewById(R.id.pTimeTv);
            pTitleTv = itemView.findViewById(R.id.pTitleTv);
            pDescriptionTv = itemView.findViewById(R.id.pDescriptionTv);
            pLikesTv = itemView.findViewById(R.id.pLikesTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            eLocationTv = itemView.findViewById(R.id.eLocationTv);
            eDayOrNightTv = itemView.findViewById(R.id.eDayOrNightTv);
            eInOrOutTv = itemView.findViewById(R.id.eInOrOutTv);
            reviewBtn = itemView.findViewById(R.id.reviewBtn);
            interestedBtn = itemView.findViewById(R.id.interestBtn);
            goingBtn = itemView.findViewById(R.id.goingBtn);
            participatedBtn = itemView.findViewById(R.id.participatedBtn);
            eDateTv = itemView.findViewById(R.id.eDateTv);
            eTimeTv = itemView.findViewById(R.id.eTimeTv);
            eIdTv = itemView.findViewById(R.id.eIdTv);
//            likeBtn = itemView.findViewById(R.id.likeBtn);
//            commentBtn = itemView.findViewById(R.id.commentBtn);
//            shareBtn = itemView.findViewById(R.id.shareBtn);
            profileLayout = itemView.findViewById(R.id.profileLayout);
            verificationBadge = itemView.findViewById(R.id.verificationBadge);


        }
    }
}
