package com.example.newlogin;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class findFriendViewHolder extends RecyclerView.ViewHolder {

    CircleImageView profileImage;
    TextView username, profession;

    public findFriendViewHolder(@NonNull View itemView) {
        super(itemView);

        profileImage = itemView.findViewById(R.id.civ_profileImage);
        username = itemView.findViewById(R.id.tv_username);
        profession = itemView.findViewById(R.id.tv_profession);
    }
}
