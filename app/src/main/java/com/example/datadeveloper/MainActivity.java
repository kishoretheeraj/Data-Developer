package com.example.datadeveloper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText fname, femail, fphone, fpassword;
    TextView login;
    Button register, reset;
    FirebaseAuth fauth;
    ProgressBar progressBar;
    FirebaseFirestore mFireStore;
    String userid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fname = findViewById(R.id.editText);
        femail = findViewById(R.id.editText2);
        fphone = findViewById(R.id.editText3);
        fpassword = findViewById(R.id.password);
        fpassword.setTypeface(Typeface.DEFAULT);
        fpassword.setTransformationMethod(new PasswordTransformationMethod());
        login=findViewById(R.id.textView2);
        fauth = FirebaseAuth.getInstance();
        register = findViewById(R.id.button3);
        reset = findViewById(R.id.button4);
        progressBar = findViewById(R.id.progressBar);

        mFireStore=FirebaseFirestore.getInstance();


        if (fauth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), dashboard.class));
            finish();
        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = femail.getText().toString().trim();
                String password = fpassword.getText().toString().trim();


                if (TextUtils.isEmpty(email)) {
                    femail.setError("Email is requifred");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    fpassword.setError("Password is required");
                    return;
                }
                if (password.length() < 6) {
                    fpassword.setError("password should be greater than six letters");
                    return;
                }

                fauth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String email = femail.getText().toString().trim();
                            String name=fname.getText().toString().trim();
                            String phone=fphone.getText().toString().trim();

                            Map<String,String> usermap=new HashMap<>();
                            usermap.put("name",name);
                            usermap.put("email",email);
                            usermap.put("phone",phone);

                            userid=fauth.getCurrentUser().getUid();
                            DocumentReference documentReference=mFireStore.collection("users").document(userid);

                            documentReference.set(usermap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();;
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });




                          /*  mFireStore.collection("users").add(usermap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                                }
                            });*/
                            Toast.makeText(MainActivity.this, "User Created.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), dashboard.class));
                            progressBar.setVisibility(View.VISIBLE);








                            //send verification link
                            FirebaseUser user=fauth.getCurrentUser();
                            user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(MainActivity.this,"Verification Email has been sent successfully", Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "Error!!"+e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });

                        } else {
                            Toast.makeText(MainActivity.this, "Error!!!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }


                    }
                });





            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));

            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fname.setText("");
                femail.setText("");
                fpassword.setText("");
                fphone.setText("");

            }
        });



    }

}
