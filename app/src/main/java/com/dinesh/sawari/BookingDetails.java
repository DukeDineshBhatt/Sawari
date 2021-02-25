package com.dinesh.sawari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class BookingDetails extends AppCompatActivity {

    String usernametxt, user_id, statustxt, driver_id, booking_details;
    String fromtxt, totxt, datetxt, timetxt, routeString, typetxt, nametxt, pricetxt, seatstxt, bagstxt, actxt, driver_token;
    ImageView image, call_us, status_img,processing_img,payment_img,confrm_img;
    TextView username, date, time, from, to, type, name, price, seats, bags, ac, status,processing,processing_txt,payment,payment_txt,des_txt,confrm,confrm_txt;
    DatabaseReference usersRef, mDriverDatabase;
    FirebaseDatabase database;
    Toolbar toolbar;
    Button pay;
    View processing_line,payment_line;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cab_booking_success);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Booking Details");

        getSupportActionBar().setDisplayShowTitleEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        Intent intent = getIntent();
        //driver_token = intent.getStringExtra("driver_token");
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
        statustxt = intent.getStringExtra("status");
        driver_id = intent.getStringExtra("driver_id");
        booking_details = intent.getStringExtra("booking_details");

        SharedPreferences shared = getSharedPreferences("myAppPrefs", MODE_PRIVATE);
        user_id = (shared.getString("user_id", ""));

        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("Users");
        mDriverDatabase = database.getReference("Drivers");

        image = findViewById(R.id.image);
        username = findViewById(R.id.username);

        date = findViewById(R.id.datetxt);
        status_img = findViewById(R.id.status_img);
        time = findViewById(R.id.time);
        from = findViewById(R.id.from);
        to = findViewById(R.id.to);
        type = findViewById(R.id.type);
        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        seats = findViewById(R.id.seats);
        bags = findViewById(R.id.bags);
        ac = findViewById(R.id.ac);
        call_us = findViewById(R.id.call_us);
        status = findViewById(R.id.status_txt);
        processing = findViewById(R.id.processing);
        processing_txt = findViewById(R.id.processing_txt);
        processing_img = findViewById(R.id.processing_img);
        processing_line = findViewById(R.id.processing_line);
        payment = findViewById(R.id.payment);
        payment_txt = findViewById(R.id.payment_txt);
        payment_img = findViewById(R.id.payment_img);
        des_txt = findViewById(R.id.des_txt);
        pay = findViewById(R.id.pay);
        payment_line = findViewById(R.id.payment_line);
        confrm_img = findViewById(R.id.confrm_img);
        confrm = findViewById(R.id.confrm);
        confrm_txt = findViewById(R.id.confrm_txt);


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


        usersRef.child(user_id).child("CabBooking").child(booking_details).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String s = dataSnapshot.child("status").getValue().toString();

                if (s.equals("pending")) {

                    status.setText("Your Booking is Processing.");
                    status_img.setBackgroundResource(R.drawable.wait);

                } else if (s.equals("accept")) {

                    pay.setVisibility(View.VISIBLE);

                    pay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(BookingDetails.this,CabBookingPayment.class);
                            intent.putExtra("price", pricetxt);
                            startActivity(intent);

                        }
                    });

                    processing.setText("Processed");
                    processing_txt.setText("Your Booking is Processed.");
                    processing_img.setImageResource(R.drawable.check_green);
                    processing_line.setBackgroundColor(Color.parseColor("#43A047"));

                    payment.setTextColor(Color.parseColor("#000000"));
                    payment_txt.setTextColor(Color.parseColor("#000000"));

                    status.setText("Your Booking is Accepted.");
                    status_img.setBackgroundResource(R.drawable.green_tick);
                    status.setTextColor(Color.parseColor("#228B22"));

                    des_txt.setText(R.string.payment_txt);

                } else if (s.equals("decline")) {
                    status_img.setBackgroundResource(R.drawable.cross);
                    status.setText("Your Booking is Canceled.");
                    status.setTextColor(Color.parseColor("#C0C0C0"));

                }
                else if (s.equals("confirm")) {

                    pay.setVisibility(View.GONE);

                    processing.setText("Processed");
                    processing_txt.setText("Your Booking is Processed.");
                    processing_img.setImageResource(R.drawable.check_green);
                    processing_line.setBackgroundColor(Color.parseColor("#43A047"));

                    payment.setTextColor(Color.parseColor("#000000"));
                    payment.setText("Payment Done");
                    payment_txt.setText("Your Payment is completed.");
                    payment_txt.setTextColor(Color.parseColor("#000000"));
                    payment_img.setImageResource(R.drawable.check_green);
                    payment_line.setBackgroundColor(Color.parseColor("#43A047"));

                    confrm.setTextColor(Color.parseColor("#000000"));
                    confrm_txt.setTextColor(Color.parseColor("#000000"));
                    confrm_img.setImageResource(R.drawable.check_green);


                    status_img.setBackgroundResource(R.drawable.check_green);
                    status.setTextColor(Color.parseColor("#228B22"));
                    status.setText("Your Booking is Confirmed.");

                    des_txt.setText(R.string.confirm_txt);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Glide.with(this)
                .load("https://i.pinimg.com/originals/26/c1/28/26c12809de558824fd9ee3cc6a67b490.gif")
                .into(image);

        usersRef.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                username.setText("Hey " + dataSnapshot.child("name").getValue().toString() + " !");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        call_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel: 8826601886"));
                startActivity(callIntent);
            }
        });


    }


    public void setProgressDialog() {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(this);
        tvText.setText("Loading ...");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setView(ll);

        AlertDialog dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}