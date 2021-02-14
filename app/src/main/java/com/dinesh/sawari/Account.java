package com.dinesh.sawari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.internal.AccountType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class Account extends BaseActivity {
    ImageView image, edit,edit1,edit2;
    Button logout;
    String user_id;
    TextView name, mobile, alt_mobile, email;
    ProgressBar progressbar;
    private final int PICK_IMAGE_REQUEST = 22;
    private static final int PERMISSION_REQUEST_CODE = 200;

    private Uri filePath;
    DatabaseReference usersRef;
    FirebaseDatabase database;

    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        image = findViewById(R.id.image);
        logout = findViewById(R.id.logout);
        name = findViewById(R.id.name);
        mobile = findViewById(R.id.mobile);
        alt_mobile = findViewById(R.id.alt_mobile);
        email = findViewById(R.id.email);
        progressbar = findViewById(R.id.progressbar);
        edit = findViewById(R.id.edit);
        edit1 = findViewById(R.id.edit1);
        edit2 = findViewById(R.id.edit2);

        SharedPreferences shared = getSharedPreferences("myAppPrefs", MODE_PRIVATE);
        user_id = (shared.getString("user_id", ""));

        mobile.setText(user_id);
        progressbar.setVisibility(View.VISIBLE);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

         database  = FirebaseDatabase.getInstance();

         usersRef  = database.getReference("Users");

        usersRef.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("image")) {

                    Glide.with(getApplicationContext()).
                            load(dataSnapshot.child("image").getValue().toString())
                            .placeholder(R.drawable.avtar)
                            .circleCrop()
                            .into(image);
                } else {
                    Glide.with(getApplicationContext()).
                            load(R.drawable.avtar)
                            .circleCrop()
                            .into(image);

                }

                if (dataSnapshot.hasChild("name")) {

                    name.setText(dataSnapshot.child("name").getValue().toString());
                } else {

                    name.setText(" ");
                }
                if (dataSnapshot.hasChild("alt_number")) {

                    alt_mobile.setText(dataSnapshot.child("alt_number").getValue().toString());
                } else {

                    alt_mobile.setText(" ");
                }
                if (dataSnapshot.hasChild("email")) {

                    email.setText(dataSnapshot.child("email").getValue().toString());
                } else {

                    email.setText(" ");
                }


                progressbar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressbar.setVisibility(View.GONE);

            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (checkPermission()) {
                    //main logic or main code

                    // . write your main code to execute, It will execute if the permission is already given.
                    SelectImage();


                } else {
                    requestPermission();
                }


            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Account.this, EditProfile.class);
                intent.putExtra("user_id", user_id);
                startActivity(intent);

            }
        });
        edit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Account.this, EditProfile.class);
                intent.putExtra("user_id", user_id);
                startActivity(intent);

            }
        });
        edit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Account.this, EditProfile.class);
                intent.putExtra("user_id", user_id);
                startActivity(intent);

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(Account.this)
                        .setTitle("Sign Out")
                        .setMessage("are you sure want to logout?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                Account.super.onBackPressed();

                                SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putBoolean("hasLoggedIn", false);


                                SharedPreferences mPrefs = getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor1 = mPrefs.edit();
                                editor1.remove("user_id");

                                editor.commit();
                                editor1.commit();

                                Intent intent = new Intent(Account.this, StartActivity.class);
                                startActivity(intent);
                                finish();

                            }
                        }).create().show();


            }
        });

    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                    // main logic
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(Account.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    // Select Image method
    private void SelectImage() {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                image.setImageBitmap(bitmap);
                uploadImage();


            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }


    private void uploadImage() {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "images/" + user_id + "/"
                                    + "profile");

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    if (taskSnapshot.getMetadata() != null) {
                                        if (taskSnapshot.getMetadata().getReference() != null) {
                                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String imageUrl = uri.toString();
                                                    //createNewPost(imageUrl);
                                                    usersRef.child(user_id).child("image").setValue(imageUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            progressDialog.dismiss();
                                                            Toast
                                                                    .makeText(Account.this,
                                                                            "Image Uploaded!!",
                                                                            Toast.LENGTH_SHORT)
                                                                    .show();
                                                        }
                                                    });

                                                    Glide.with(getApplicationContext()).
                                                            load(imageUrl)
                                                            .placeholder(R.drawable.avtar)
                                                            .circleCrop()
                                                            .into(image);
                                                }
                                            });
                                        }
                                    }

                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(Account.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int) progress + "%");
                                }
                            });
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(Account.this, MainActivity.class);
        startActivity(intent);
        finish();


    }

    @Override
    int getContentViewId() {
        return R.layout.activity_account;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.acount;
    }
}
