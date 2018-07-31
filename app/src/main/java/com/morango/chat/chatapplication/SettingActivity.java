package com.morango.chat.chatapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    CircleImageView userImage;
    TextView userStatus, userName;

    Button changStatus, changeImage;
    Toolbar toolbar;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    StorageReference storageReferenceThumb;

    //Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        userImage = findViewById(R.id.userImage);
        userStatus = findViewById(R.id.userStatus);
        userName = findViewById(R.id.userName);
        changStatus = findViewById(R.id.changeStatusButton);
        changeImage = findViewById(R.id.changeImageButton);
        toolbar = findViewById(R.id.settingToolBar);
        mAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(mAuth.getCurrentUser().getUid());
        storageReference = FirebaseStorage.getInstance().getReference().child("ProfilePictures");
//        storageReferenceThumb = FirebaseStorage.getInstance().getReference().child("ThumbImages");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName.setText(dataSnapshot.child("userName").getValue().toString());
                userStatus.setText(dataSnapshot.child("userStatus").getValue().toString());
                String imageUrl = dataSnapshot.child("userImage").getValue().toString();

                try {
                    Picasso.get().load(imageUrl).error(R.drawable.default_profile).into(userImage);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropTheImage();
            }
        });

        changStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(), StatusChangeActivity.class);
                in.putExtra("status", userStatus.getText().toString());
                startActivity(in);
            }
        });

    }

    private void CropTheImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("Crop")
                .setCropShape(CropImageView.CropShape.OVAL)
                .setAspectRatio(1, 1)
                .setMaxCropResultSize(1000, 1000)
                .setMinCropWindowSize(400, 400)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
//
//                File orignalImage = new File(result.getUri().getPath());
//
//                try{
//                   bitmap = new Compressor(this)
//                            .setMaxHeight(200)
//                            .setMaxWidth(200)
//                            .setQuality(50)
//                            .compressToBitmap(orignalImage);
//
//                }catch (Exception ex){
//
//                }
//
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG,50, byteArrayOutputStream);
//                byte[] thumbByte = byteArrayOutputStream.toByteArray();


                Picasso.get().load(result.getUri()).error(R.drawable.default_profile).into(userImage);
                storageReference = storageReference.child(mAuth.getCurrentUser().getUid() + ".jpg");
//                storageReferenceThumb = storageReferenceThumb.child(mAuth.getCurrentUser().getUid() + ".jpg");

                UploadTask uploadTask = storageReference.putFile(result.getUri());
                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return storageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
//                            databaseReference.child("userImage").setValue(task.getResult().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        Toast.makeText(getApplicationContext(), "Database success:" + task.getResult().toString(), Toast.LENGTH_LONG).show();
//                                    } else {
//                                        Toast.makeText(getApplicationContext(), "Database success:" + task.getException().toString(), Toast.LENGTH_LONG).show();
//                                    }
//                                }
//                            });
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                        }

                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }

    }
}
