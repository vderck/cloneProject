package com.example.newlogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity
{

    TextView createNewAccount;
    TextView forgotPassword;
    EditText Email, Password;
    Button btn_login;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNewAccount = findViewById(R.id.tv_creatNewAccount);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Email = findViewById(R.id.et_registerEmail);
        Password = findViewById(R.id.et_registerPassword);
        btn_login = findViewById(R.id.btn_login);
        forgotPassword = findViewById(R.id.tv_forgotPassword);

        progressDialog = new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this , ForgotPasswordActivity.class));
                finish();
            }
        });

        createNewAccount.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(MainActivity.this , RegisterActivity.class));
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                 PerforLogin();
            }

            private void PerforLogin()
            {
                String email = Email.getText().toString();
                String password = Password.getText().toString();

                if(!email.matches(emailPattern))
                {
                    Email.setError("Please, enter correct E-mail addresses");
                } else if (password.isEmpty() || password.length() < 6)
                {
                    Password.setError("please, enter proper password");
                }  else {
                    progressDialog.setMessage("Login in progress...");
                    progressDialog.setTitle("Login");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful())
                            {
                                progressDialog.dismiss();
                                mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
                                mUserRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            sendUserToNextActivity();
                                        }
                                        else
                                        {
                                            sendUserToSetupActivity();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(MainActivity.this, ""+error.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });

//                                다음은 왜 안되는가를 한번 깊이 고민해볼것....
//                                if ((mUserRef.getKey() == null))
//                                {
//                                    sendUserToSetupActivity();
//                                }
//                                else if ((mUserRef.getKey() != null)){
//                                sendUserToNextActivity();
//                                }


                                Toast.makeText(MainActivity.this, "login successful" , Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(MainActivity.this , SetupActivity.class);
                                startActivity(intent);
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(MainActivity.this, "server connection failure. please, try again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            }
        });


    }

    private void sendUserToSetupActivity() {
        Intent intent = new Intent(MainActivity.this , SetupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void sendUserToNextActivity()
    {
        Intent intent = new Intent(MainActivity.this , HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

