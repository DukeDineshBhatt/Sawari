package com.dinesh.sawari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dinesh.sawari.NOtificationPOJO.Data;
import com.dinesh.sawari.NOtificationPOJO.DataBean;
import com.dinesh.sawari.NOtificationPOJO.ResultBean;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class CabBookingSuccess extends AppCompatActivity {

    String usernametxt,user_id;
    String fromtxt, totxt, datetxt, timetxt, routeString, typetxt, nametxt, pricetxt, seatstxt, bagstxt, actxt, driver_token;
    ImageView image;
    TextView username,date, time, from, to, type, name, price, seats, bags, ac;
    DatabaseReference usersRef;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cab_booking_success);

        Intent intent = getIntent();
        driver_token = intent.getStringExtra("driver_token");
        datetxt = intent.getStringExtra("date");
        timetxt = intent.getStringExtra("time");
        typetxt = intent.getStringExtra("type");
        nametxt = intent.getStringExtra("name");
        pricetxt = intent.getStringExtra("price");
        seatstxt = intent.getStringExtra("seats");
        bagstxt = intent.getStringExtra("bags");
        actxt = intent.getStringExtra("ac");
        fromtxt = intent.getStringExtra("from");
        totxt = intent.getStringExtra("to");

        SharedPreferences shared = getSharedPreferences("myAppPrefs", MODE_PRIVATE);
        user_id = (shared.getString("user_id", ""));

        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("Users");

        image=findViewById(R.id.image);
        username=findViewById(R.id.username);

        date = findViewById(R.id.datetxt);
        time = findViewById(R.id.time);
        from = findViewById(R.id.from);
        to = findViewById(R.id.to);
        type = findViewById(R.id.type);
        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        seats = findViewById(R.id.seats);
        bags = findViewById(R.id.bags);
        ac = findViewById(R.id.ac);

        username.setText(usernametxt);

        date.setText(datetxt);
        time.setText(timetxt);
        from.setText(fromtxt);
        to.setText(totxt);
        type.setText(typetxt);
        name.setText(nametxt + " or Equivalent");
        price.setText(pricetxt);
        seats.setText(seatstxt + " seats");

        if (typetxt.equals("Lite")) {
            bags.setText("2 Bags");
        } else if (typetxt.equals("comfort")) {
            bags.setText("3 Bags");
        } else if (typetxt.equals("comfort")) {
            bags.setText("4 Bags");
        } else if (typetxt.equals("6 Plus")) {
            bags.setText("4 Bags");
        } else if (typetxt.equals("6 Pro")) {
            bags.setText("4 Bags");
        }

        if (actxt.equals("yes")) {
            ac.setText("AC");

        } else {

            ac.setText("Non-Ac");

        }

        Glide.with(this)
                .load("https://i.pinimg.com/originals/26/c1/28/26c12809de558824fd9ee3cc6a67b490.gif")
                .into(image);

        usersRef.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               username.setText("Hey "+dataSnapshot.child("name").getValue().toString()+" !");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //sendNotification();

    }

    public void sendNotification(){

        Application b = (Application) getApplicationContext();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.level(HttpLoggingInterceptor.Level.HEADERS);
        logging.level(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder().writeTimeout(1000, TimeUnit.SECONDS).readTimeout(1000, TimeUnit.SECONDS).connectTimeout(1000, TimeUnit.SECONDS).addInterceptor(logging).build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(b.notificationBaseurl)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AllApiIneterface cr = retrofit.create(AllApiIneterface.class);

        Data body1 = new Data();
        body1.setBody("Hello, We have found a New Booking For You.");
        body1.setTitle("NEW SAWARI BOOKING");
        body1.setIcon("https://firebasestorage.googleapis.com/v0/b/sawari-7818f.appspot.com/o/sawarilogo%20-%20Copy.png?alt=media&token=9a03230f-eb66-4fed-92c3-18703bbdbf65");

        DataBean body = new DataBean();
        body.setTo(driver_token);
        body.setData(body1);

        Call<ResultBean> call = cr.sendNotification(body);
        call.enqueue(new Callback<ResultBean>() {
            @Override
            public void onResponse(@NotNull Call<ResultBean> call, @NotNull Response<ResultBean> response) {

                //Toast.makeText(CabBookingSuccess.this, response.body().toString(), Toast.LENGTH_LONG).show();

                if (response.body().getSuccess() == 1) {

                    Toast.makeText(CabBookingSuccess.this, "Booking Successfully Done.", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(CabBookingSuccess.this, "Please try again", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<ResultBean> call, Throwable t) {

                Toast.makeText(CabBookingSuccess.this, "Something wrong", Toast.LENGTH_SHORT).show();
                Log.d("DDDD"," "+t.toString());

            }
        });


    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(CabBookingSuccess.this, MainActivity.class);
        startActivity(intent);
        finish();


    }
}