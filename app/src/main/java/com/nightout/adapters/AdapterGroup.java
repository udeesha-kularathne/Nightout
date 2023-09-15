package com.nightout.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;

import com.nightout.ChatActivity;
import com.nightout.GroupPostsActivity;
import com.nightout.R;
import com.nightout.ThereProfileActivity;
import com.nightout.models.ModelGroup;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterGroup extends RecyclerView.Adapter<AdapterGroup.MyHolder> {

    Context context;
    List<ModelGroup> groupList;

    public AdapterGroup(Context context, List<ModelGroup> groupList) {
        this.context = context;
        this.groupList = groupList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // Inflate the layout for a single group item
        View view = LayoutInflater.from(context).inflate(R.layout.row_groups, viewGroup, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {

        //get data
        String hisUID = groupList.get(i).getUid();
        String userImage = groupList.get(i).getImage();
        String groupName = groupList.get(i).getgName();
        String groupCategory = groupList.get(i).getgCategory();
        String groupDescrp = groupList.get(i).getgDescrp();


        //set data
        myHolder.gNameTv.setText(groupName);
        myHolder.gCategoryTv.setText(groupCategory);
        myHolder.gDescrpTv.setText(groupDescrp);
        try {
            Picasso.get().load(userImage)
                    .placeholder(R.drawable.ic_face_img_purple)
                    .into(myHolder.gProfileIv);
        } catch (Exception e) {

            // Set any other data you want to display
        }

        //handle item click
        myHolder.itemView.setOnClickListener(v -> {

            //show dialog
//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            builder.setItems(new String[]{"Group"}, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    if (which==0){
                        //profile clicked
                        /*click to go to ThereProfileActivity with uid, this uid is of clicked user
                         * which will be used to show user specific data/posts*/
            Intent intent = new Intent(context, GroupPostsActivity.class);
            intent.putExtra("gName", groupName); // Use "gName" instead of "uid"
            intent.putExtra("gCategory", groupCategory); // Use "gCategory" instead of "groupCategory"
            context.startActivity(intent);
//                    }
//                    if (which==1){
//                        //chat clicked
//                        /*Click user from user list to start chatting/messaging
//                         * Start activity by putting UID of receiver
//                         * we will use that UID to identify the user we are gonna chat*/
//                        Intent intent = new Intent(context, ChatActivity.class);
//                        intent.putExtra("hisUid", hisUID);
//                        context.startActivity(intent);


                    });
                }
//            });
//            builder.create().show();
//        });
//    }






    @Override
    public int getItemCount() {
        return groupList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView gProfileIv;
        TextView gNameTv, gCategoryTv, gDescrpTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            gProfileIv = itemView.findViewById(R.id.gProfileIv);
            gNameTv = itemView.findViewById(R.id.gNameTv);
            gCategoryTv = itemView.findViewById(R.id.gCategoryTv);
            gDescrpTv = itemView.findViewById(R.id.gDescrpTv);
        }
    }
}
