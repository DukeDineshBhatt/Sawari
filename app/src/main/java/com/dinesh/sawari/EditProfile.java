package com.dinesh.sawari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EditProfile extends AppCompatActivity {
    private Toolbar toolbar;
    EditText name, alt_number, email;
    String user_id;
    ProgressBar progressbar;
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Profile");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        name = findViewById(R.id.name);
        alt_number = findViewById(R.id.alt_mobile);
        email = findViewById(R.id.email);
        progressbar = findViewById(R.id.progressbar);
        save = findViewById(R.id.save);

        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        HashMap<String, Object> map = new HashMap<>();

        progressbar.setVisibility(View.VISIBLE);

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference usersRef = database.getReference("Users");

        usersRef.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("name")) {

                    name.setText(dataSnapshot.child("name").getValue().toString());
                } else {

                    name.setText("");
                }
                if (dataSnapshot.hasChild("alt_number")) {

                    alt_number.setText(dataSnapshot.child("alt_number").getValue().toString());
                } else {

                    alt_number.setText("");
                }
                if (dataSnapshot.hasChild("email")) {

                    email.setText(dataSnapshot.child("email").getValue().toString());
                } else {

                    email.setText("");
                }


                progressbar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressbar.setVisibility(View.GONE);

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressbar.setVisibility(View.VISIBLE);
                map.put("name", name.getText().toString());
                map.put("alt_number", alt_number.getText().toString());
                map.put("email", email.getText().toString());

                usersRef.child(user_id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            progressbar.setVisibility(View.GONE);

                            Toast.makeText(EditProfile.this, "Success", Toast.LENGTH_SHORT).show();
                            Intent intent =new Intent(EditProfile.this,Account.class);
                            startActivity(intent);


                        } else {
                            Toast.makeText(EditProfile.this, "Try Again!", Toast.LENGTH_SHORT).show();
                            progressbar.setVisibility(View.GONE);

                        }

                    }
                });


            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(EditProfile.this, Account.class);
        startActivity(intent);
        finish();


    }

}