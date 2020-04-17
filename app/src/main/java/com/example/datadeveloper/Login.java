package com.example.datadeveloper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    EditText memail, mpassword;
    Button mloginbtn;
    TextView mcreatebtn, resetmaillink;
    FirebaseAuth fauth;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        memail = findViewById(R.id.editText2);
        mpassword = findViewById(R.id.editText4);
        mpassword.setTypeface(Typeface.DEFAULT);
        mpassword.setTransformationMethod(new PasswordTransformationMethod());
        progressBar = findViewById(R.id.progressBar1);
        fauth = FirebaseAuth.getInstance();
        mloginbtn = findViewById(R.id.button3);
        mcreatebtn = findViewById(R.id.textView2);
        resetmaillink = findViewById(R.id.textView3);


        mloginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = memail.getText().toString().trim();
                String password = mpassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    memail.setError("Email is Required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mpassword.setError("Password is required");
                    return;
                }
                if (password.length() < 6) {
                    mpassword.setError("password should be greater than six letters");
                    return;
                }

                // autenticate the user

                fauth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this, dashboard.class));
                            progressBar.setVisibility(View.VISIBLE);


                        } else {
                            Toast.makeText(Login.this, "Error!!!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });


            }
        });
        mcreatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, MainActivity.class));

            }
        });

        resetmaillink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetmail1=new EditText(v.getContext());
                final AlertDialog.Builder passworddialog=new AlertDialog.Builder(v.getContext());
                passworddialog.setTitle("Reset Password");
                passworddialog.setMessage("Enter your Email id to receive Reset link :-");
                passworddialog.setView(resetmail1);

                passworddialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //get email and send link
                        String mail=resetmail1.getText().toString();
                        fauth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Login.this, "Reset link as been sent ", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this, "Error!!"+e.getMessage(), Toast.LENGTH_SHORT).show();


                            }
                        });
                    }
                });
                passworddialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                passworddialog.show();
            }
        });







    }
}