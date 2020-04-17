package com.example.datadeveloper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class dashboard extends AppCompatActivity {
    Button logout,verify,resetpassword,changeprofile;
    TextView verifymsg,fname,femail,fphone;
   FirebaseAuth fauth;
   StorageReference storageReference;
    FirebaseFirestore fstore;
    String userid;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        logout=findViewById(R.id.logout);
        verifymsg=findViewById(R.id.textView5);
        fname=findViewById(R.id.textView6);
        femail=findViewById(R.id.textView7);
        resetpassword =findViewById(R.id.resetpassword);

        changeprofile=findViewById(R.id.changeprofile);
        fphone=findViewById(R.id.textView8);
        imageView=findViewById(R.id.profile);

        storageReference= FirebaseStorage.getInstance().getReference();

StorageReference profileRef = storageReference.child("users/"+fauth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imageView);
            }
        });

        verify=findViewById(R.id.verifybtn);
       fauth=FirebaseAuth.getInstance();
        FirebaseUser user=fauth.getCurrentUser();
        fstore=FirebaseFirestore.getInstance();

        if(!user.isEmailVerified())
        {
            verifymsg.setVisibility(View.VISIBLE);
            verify.setVisibility(View.VISIBLE);
            verify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    FirebaseUser user=fauth.getCurrentUser();
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(v.getContext(),"Verfication Email has been sent successfully", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(dashboard.this, "Error!!"+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            });
        }

        userid=fauth.getCurrentUser().getUid();

        DocumentReference documentReference=fstore.collection("users").document(userid);
        documentReference.addSnapshotListener(dashboard.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                fphone.setText(documentSnapshot.getString("phone"));
                femail.setText(documentSnapshot.getString("email"));
                fname.setText(documentSnapshot.getString("name"));

            }
        });

        changeprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent opengallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(opengallery,1000);
                


            }
        });






    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000)
        {
            if(resultCode== Activity.RESULT_OK)
            {
                Uri image=data.getData();
                //imageView.setImageURI(image);
                uploadimagetofirebase(image);
                
            }
        }
    }

    private void uploadimagetofirebase(Uri image) {
        //upload image to firebase storage

        final StorageReference fileref=storageReference.child("users/"+fauth.getCurrentUser().getUid()+"/profile.jpg");
        fileref.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(imageView);
                    }
                });
                Toast.makeText(dashboard.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(dashboard.this, "Image Upload failed", Toast.LENGTH_SHORT).show();
                
            }
        });
        
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
        Toast.makeText(this, "Logout Successful", Toast.LENGTH_SHORT).show();
        finish();
    }






    }
