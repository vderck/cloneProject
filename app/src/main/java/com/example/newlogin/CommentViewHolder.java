package com.example.newlogin;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentViewHolder extends RecyclerView.ViewHolder {
    CircleImageView profileImage;
    TextView username,comment;
    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        profileImage = itemView.findViewById(R.id.iv_profileImageComment);
        username = itemView.findViewById(R.id.tv_userNameComment);
        comment = itemView.findViewById(R.id.tv_comments);

    }
}
