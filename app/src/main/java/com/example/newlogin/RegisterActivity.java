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

public class RegisterActivity extends AppCompatActivity {

    TextView alreadyHaveAnAccount;
    EditText registerEmail, registerPassword, registerConfirmPassword;
    Button btn_register;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        alreadyHaveAnAccount = findViewById(R.id.tv_alreadyHaveAnAccount);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        alreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this , MainActivity.class);
                startActivity(intent);
            }
        });

        registerEmail = findViewById(R.id.et_registerEmail);
        registerPassword = findViewById(R.id.et_registerPassword);
        registerConfirmPassword = findViewById(R.id.et_registerConfirmPassword);
        btn_register = findViewById(R.id.btn_login);
        progressDialog = new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PerforAuth();
            }
        });
    }

    private void PerforAuth() {
    String email = registerEmail.getText().toString();
    String password = registerPassword.getText().toString();
    String confirmPassword = registerConfirmPassword.getText().toString();

    if(!email.matches(emailPattern)){
        registerEmail.setError("Please, enter correct E-mail addresses");
    } else if (password.isEmpty() || password.length() < 6) {
        registerPassword.setError("please, enter proper password");
    } else if (!password.equals(confirmPassword)){
        registerConfirmPassword.setError("the confirmed password should be matched with the original passowrd");
    } else {
        progressDialog.setMessage("Registration process in progress...");
        progressDialog.setTitle("Registration");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    sendUserToNextActivity();
                    Toast.makeText(RegisterActivity.this , "Registration complete!" , Toast.LENGTH_LONG).show();
                }

                else {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "" + task.getException() , Toast.LENGTH_SHORT).show();
                }
            }

            private void sendUserToNextActivity() {
                Intent intent = new Intent(RegisterActivity.this , SetupActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    }
}