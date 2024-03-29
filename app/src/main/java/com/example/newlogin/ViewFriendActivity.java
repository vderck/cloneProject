
package com.example.newlogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class ViewFriendActivity extends AppCompatActivity {

    DatabaseReference mUserRef, requestRef, friendRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String profileImageUrl, username, city, country;
    Button btnPerform, btnDecline;
    String CurrentState = "nothing_happening";

    CircleImageView profileImage;
    TextView Username, address;
    String profession;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friend);
        String userID = getIntent().getStringExtra("userkey");
        Toast.makeText(this, "" + userID, Toast.LENGTH_SHORT).show();

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        requestRef = FirebaseDatabase.getInstance().getReference().child("Requests");
        friendRef = FirebaseDatabase.getInstance().getReference().child("Friend");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        profileImage = findViewById(R.id.civ_profileImage);
        Username = findViewById(R.id.tv_username2);
        address = findViewById(R.id.tv_address);
        btnPerform = findViewById(R.id.btn_perform);
        btnDecline = findViewById(R.id.btn_decline);


        LoadUser();

        btnPerform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performAction(userID);
            }
        });
        CheckUserExistance(userID);
        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unfriend(userID);
            }
        });


    }

    private void unfriend(String userID) {
        if (CurrentState.equals("friend"))
        {
            friendRef.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                 if (task.isSuccessful())
                 {
                     friendRef.child(userID).child(mUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(ViewFriendActivity.this, "you are unfriend", Toast.LENGTH_SHORT).show();
                                CurrentState ="nothing_happen";
                                btnPerform.setText("Send Friend Request");
                                btnDecline.setVisibility(View.GONE);
                            }

                         }
                     });
                 }

                }
            });
        }
        if (CurrentState.equals("he_sent_pending")){
            HashMap hashMap = new HashMap();
            hashMap.put("status" , "decline");
            hashMap.put("username" , username);
            hashMap.put("profileImageUrl" , profileImageUrl);
            hashMap.put("profession" , "profession");

            requestRef.child(userID).child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ViewFriendActivity.this, "you have Decline Friend", Toast.LENGTH_SHORT).show();
                        CurrentState = "he_sent_decline";
                        btnPerform.setVisibility(View.GONE);
                        btnDecline.setVisibility(View.GONE);

                    }
                }
            });
        }
    }

    private void CheckUserExistance(String userID) {
         friendRef.child(mUser.getUid()).child(userID).addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    CurrentState = "friend";
                    btnPerform.setText("send SMS");
                    btnDecline.setText("Unfriend");
                    btnDecline.setVisibility(View.VISIBLE);
                }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });
         friendRef.child(userID).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    CurrentState = "friend";
                    btnPerform.setText("send SMS");
                    btnDecline.setText("Unfriend");
                    btnDecline.setVisibility(View.VISIBLE);
                }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });
         requestRef.child(mUser.getUid()).child(userID).addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    if(snapshot.child("status").getValue().toString().equals("pending"))
                    {
                        CurrentState="I_sent_pending";
                        btnPerform.setText("Cancel Friend Request");
                        btnDecline.setVisibility(View.GONE);
                    }
                    if(snapshot.child("status").getValue().toString().equals("decline"))
                    {

                        CurrentState="I_Sent_Decline";
                        btnPerform.setText("Cancel Friend Request");
                        btnDecline.setVisibility(View.GONE);

                    }

                }

             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });
         requestRef.child(userID).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    if (snapshot.child("status").getValue().toString().equals("pending")){
                        CurrentState = "he_sent_pending";
                        btnPerform.setText("Accept Friend Request");
                        btnDecline.setText("Decline Friend");
                        btnDecline.setVisibility(View.VISIBLE);
                    }
                }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });
         if (CurrentState.equals("nothing_happen")){
             CurrentState="nothing_happen";
             btnPerform.setText("Send Friend Request");
             btnDecline.setVisibility(View.GONE);
         }
    }


    private void performAction(String userID) {
        if (CurrentState.equals("nothing_happening"))
        {
            HashMap hashMap = new HashMap();
            hashMap.put("status", "pending");

                requestRef.child((mUser.getUid())).child(userID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(ViewFriendActivity.this , "you've sent a friend request", Toast.LENGTH_SHORT).show();
                            btnDecline.setVisibility(View.GONE);
                            CurrentState = "sentRequest_pending";
                            btnPerform.setText("cancel Friend Request");
                        }
                        else
                        {
                            Toast.makeText(ViewFriendActivity.this, ""+task.getException().toString(), Toast.LENGTH_LONG).show();

                        }
                    }
                });
        }
        if (CurrentState.equals("I_Sent_pending")|| CurrentState.equals("I_Sent_decline"))
        {
            requestRef.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()){
                        Toast.makeText(ViewFriendActivity.this , "you've cancelled friend request", Toast.LENGTH_SHORT).show();
                        CurrentState="nothing_happening";
                        btnPerform.setText("Send Friend Request");
                        btnDecline.setVisibility(View.GONE);
                    }
                    else{
                        Toast.makeText(ViewFriendActivity.this , ""+task.getException().toString(), Toast.LENGTH_SHORT).show();
                        CurrentState = "friend";
                        btnPerform.setText("send SMS");
                        btnDecline.setText("Unfriend");
                        btnDecline.setVisibility(View.VISIBLE);
                    }

                }
            });
        }
        if (CurrentState.equals("he_sent_pending")){
            requestRef.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                 if (task.isSuccessful()){
                    HashMap hashmap = new HashMap();
                    hashmap.put("status" , "friend");
                    hashmap.put("username" , username);
                    hashmap.put("profileImageUrl" , profileImageUrl);
                    friendRef.child(mUser.getUid()).child(userID).updateChildren(hashmap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful())
                            {
                                friendRef.child(mUser.getUid()).updateChildren(hashmap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                         Toast.makeText(ViewFriendActivity.this, "Friend added successfully !", Toast.LENGTH_SHORT).show();
                                            CurrentState = "friend";
                                            btnPerform.setText("send SMS");
                                            btnDecline.setText("unFriend");
                                            btnDecline.setVisibility(View.VISIBLE);
                                    }
                                });
                            }

                        }
                    });
                 }
                }
            });
        }
        if (CurrentState.equals("friend"))
        {
            Intent intent = new Intent(ViewFriendActivity.this, ChatActivity.class);
            intent.putExtra("otherUserID", userID);
            startActivity(intent);

        }

        if (CurrentState.equals("cancel Friend Request"))
        {
            HashMap hashMap = new HashMap();
            hashMap.put("status", "sentRequest_pending");

            requestRef.child((mUser.getUid())).child(userID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(ViewFriendActivity.this , "you've sent a friend request", Toast.LENGTH_SHORT).show();
                        btnDecline.setVisibility(View.GONE);
                        CurrentState = "sentRequest_pending";
                        btnPerform.setText("cancel Friend Request");
                    }
                    else
                    {
                        Toast.makeText(ViewFriendActivity.this, ""+task.getException().toString(), Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }


    private void LoadUser() {
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    profileImageUrl = snapshot.child("profileImage").getValue().toString();
                    username = snapshot.child("username").getValue().toString();
                    city = snapshot.child("city").getValue().toString();
                    country = snapshot.child("country").getValue().toString();
                    profession= snapshot.child("profession").getValue().toString();

                    Picasso.get().load(profileImageUrl).into(profileImage);
                    Username.setText(username);
                    address.setText(city +" " + country);

                }
                else
                {
                    Toast.makeText(ViewFriendActivity.this, "Error, data connection failure", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ViewFriendActivity.this, ""+error.getMessage().toString() , Toast.LENGTH_SHORT).show();
            }
        });
    }
}