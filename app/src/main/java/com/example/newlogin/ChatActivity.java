package com.example.newlogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    Toolbar mToolbar;
    RecyclerView recyclerView;
    EditText inputSms;
    ImageView sendButton;
    CircleImageView userProfileImageAppbar;
    TextView usernameAppbar,status;
    String otherUserID;
    DatabaseReference mUserRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String otherUsername, OtherUserProfileImageLink, otherUserStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mToolbar = findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        otherUserID = getIntent().getStringExtra("otherUserID");

        recyclerView = findViewById(R.id.recyclerView30);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        inputSms = findViewById(R.id.et_inputSms);
        sendButton = findViewById(R.id.iv_imageView4);
        userProfileImageAppbar = findViewById(R.id.userProfileImageAppbar);
        usernameAppbar = findViewById(R.id.tv_username3);
        status = findViewById(R.id.textView3);
        mUserRef= FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();

        
        LoadOtherUser();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSMS();
            }
        });

    }

    private void sendSMS() {
        String sms = inputSms.getText().toString();
        if (sms.isEmpty()) {

        }
        else {
            HashMap hashMap = new HashMap();
            hashMap.put("sms", sms);
            hashMap.put("status", "unseen");
            hashMap.put("userID" , mUser.getUid());

        }

    }

    private void LoadOtherUser() {

        mUserRef.child(otherUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    otherUsername = snapshot.child("username").getValue().toString();
                    OtherUserProfileImageLink = snapshot.child("profileImage").getValue().toString();
                    otherUserStatus=snapshot.child("status").getValue().toString();
                    Picasso.get().load(OtherUserProfileImageLink).into(userProfileImageAppbar);
                    usernameAppbar.setText(otherUsername);
                    status.setText(otherUserStatus);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}