package com.example.newlogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView profileImageview;
    EditText inputUsername, inputCity, inputCountry, inputProfession;
    Button btnUpdate;

    DatabaseReference mUserRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImageview = findViewById(R.id.circleImageView);
        inputUsername = findViewById(R.id.et_inputUsername_profile);
        inputCity = findViewById(R.id.et_inputCity_profile);
        inputCountry = findViewById(R.id.et_inputCountry_profile);
        inputProfession = findViewById(R.id.et_inputProfession_profile);
        btnUpdate = findViewById(R.id.btn_Update);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");



        mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String profileImageUri = snapshot.child("profileImage").getValue().toString();
                    String city = snapshot.child("city").getValue().toString();
                    String country = snapshot.child("country").getValue().toString();
                    String profession = snapshot.child("profession").getValue().toString();
                    String username = snapshot.child("username").getValue().toString();

                    Picasso.get().load(profileImageUri).into(profileImageview);
                    inputCity.setText(city);
                    inputUsername.setText(username);
                    inputCountry.setText(country);
                    inputProfession.setText(profession);
                }
                else
                {
                    Toast.makeText(ProfileActivity.this, "data doesn't exist", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this , ""+error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}