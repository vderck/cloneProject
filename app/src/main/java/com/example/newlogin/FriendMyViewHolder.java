package com.example.newlogin;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendMyViewHolder extends RecyclerView.ViewHolder {
    CircleImageView profileImageUrl;
    TextView username, profession;

    public FriendMyViewHolder(@NonNull View itemView) {
        super(itemView);
        profileImageUrl = itemView.findViewById(R.id.profile_image);
        username = itemView.findViewById(R.id.tv_username2);
        profession = itemView.findViewById(R.id.tv_profession);
    }
}
