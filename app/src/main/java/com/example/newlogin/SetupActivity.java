package com.example.newlogin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 101;
    CircleImageView circleImageView;
    EditText et_name, et_city, et_country, et_profession;
    Button btn_save;
    Uri imageUri;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mRef;
    StorageReference StorageRef;

    ProgressDialog mLoadingBar;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        circleImageView = findViewById(R.id.profile_image);
        et_name = findViewById(R.id.et_inputUserName);
        et_city = findViewById(R.id.et_inputCity);
        et_country = findViewById(R.id.et_inputCountry);
        et_profession = findViewById(R.id.et_inputProfession);
        btn_save = findViewById(R.id.btn_save);
        mLoadingBar = new ProgressDialog(this);
        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Setup Profile");

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference().child("Users");
        StorageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages");

//        if(mRef.child("Users") != null) {
//            startActivity(new Intent(SetupActivity.this , HomeActivity.class));
//        }



        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveData();
            }
        });
    }

    private void SaveData()
    {
        String userName = et_name.getText().toString();
        String city = et_city.getText().toString();
        String country = et_country.getText().toString();
        String profession = et_profession.getText().toString();



        if (userName.isEmpty() || userName.length()<3)
        {
            showError(et_name, "Username is not valid");
        }
        else if (city.isEmpty())
        {
            showError(et_city, "please enter your city");
        }
        else if (country.isEmpty())
        {
            showError(et_country, "please enter your country");
        }
        else if (profession.isEmpty())
        {
            showError(et_profession, "please enter your profession");
        }
        else if (imageUri == null)
        {
            Toast.makeText(this, "please select your picture" , Toast.LENGTH_LONG).show();
        }
        else
        {
            mLoadingBar.setTitle("adding setup profile");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();
            StorageRef.child(mUser.getUid()).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                {
                    if(task.isSuccessful())
                    {
                            StorageRef.child(mUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                            {
                                @Override
                                public void onSuccess(Uri uri)
                                {
                                    HashMap hashMap = new HashMap();
                                    hashMap.put("username" , userName);
                                    hashMap.put("city" , city);
                                    hashMap.put("country" , country);
                                    hashMap.put("profession" , profession);
                                    hashMap.put("profileImage" , uri.toString());
                                    hashMap.put("status", "offline");

                                    mRef.child(mUser.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener()
                                    {
                                        @Override
                                        public void onSuccess(Object o)
                                        {   Intent intent = new Intent(SetupActivity.this , HomeActivity.class);
                                            startActivity(intent);
                                            mLoadingBar.dismiss();
                                            Toast.makeText(SetupActivity.this, "set-up profile completed !", Toast.LENGTH_LONG).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener()
                                    {
                                        @Override
                                        public void onFailure(@NonNull Exception e)
                                        {
                                            mLoadingBar.dismiss();
                                            Toast.makeText(SetupActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            });
                    }
                }
            });
        }

    }


    private void showError(EditText input, String s)
    {
        input.setError(s);
        input.requestFocus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode ==RESULT_OK && data!=null)
        {
            imageUri=data.getData();
            circleImageView.setImageURI(imageUri);
        }
    }

}