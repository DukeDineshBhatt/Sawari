package com.dinesh.sawari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class ReviewCabBooking extends AppCompatActivity implements PaymentResultListener {

    Toolbar toolbar;
    TextView date, time, route, type, name, price, seats, bags, ac, five_percent, half_amount, full_amount, email, phonenumber, username;

    String from, to, datetxt, timetxt, routeString, typetxt, nametxt, pricetxt, seatstxt, bagstxt, actxt, twentyPercentPrice;
    Button pay;
    String payment;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    String user_id;
    DatabaseReference usersRef;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_cab_booking);

        toolbar = findViewById(R.id.toolbar);
        date = findViewById(R.id.datetxt);
        time = findViewById(R.id.time);
        route = findViewById(R.id.route);
        type = findViewById(R.id.type);
        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        seats = findViewById(R.id.seats);
        bags = findViewById(R.id.bags);
        ac = findViewById(R.id.ac);
        email = findViewById(R.id.email);
        phonenumber = findViewById(R.id.phonenumber);
        username = findViewById(R.id.username);
        five_percent = findViewById(R.id.five_percent);
        half_amount = findViewById(R.id.half_amount);
        full_amount = findViewById(R.id.full_amount);
        pay = findViewById(R.id.pay);
        radioGroup = (RadioGroup) findViewById(R.id.radio);

        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Review your Booking");

        getSupportActionBar().setDisplayShowTitleEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        SharedPreferences shared = getSharedPreferences("myAppPrefs", MODE_PRIVATE);
        user_id = (shared.getString("user_id", ""));

        Intent intent = getIntent();
        routeString = intent.getStringExtra("route");
        datetxt = intent.getStringExtra("date");
        typetxt = intent.getStringExtra("type");
        nametxt = intent.getStringExtra("name");
        pricetxt = intent.getStringExtra("price");
        seatstxt = intent.getStringExtra("seats");
        bagstxt = intent.getStringExtra("bags");
        actxt = intent.getStringExtra("ac");

        date.setText(datetxt);
        route.setText(routeString);
        type.setText(typetxt);
        name.setText(nametxt + " or Equivalent");
        price.setText(pricetxt);
        seats.setText(seatstxt + " seats");
        bags.setText(bagstxt + " Bags");
        if (actxt.equals("yes")) {
            ac.setText("AC");

        } else {

            ac.setText("Non-Ac");

        }
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("Users");

        usersRef.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                phonenumber.setText(user_id);
                if (dataSnapshot.hasChild("name")) {

                    username.setText(dataSnapshot.child("name").getValue().toString());
                } else {

                    username.setText("");
                }
                if (dataSnapshot.hasChild("email")) {

                    email.setText(dataSnapshot.child("email").getValue().toString());
                } else {

                    email.setText("");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });


        int price = Integer.parseInt(pricetxt);
        int res = (price / 100) * 5;
        five_percent.setText(String.valueOf(res));

        int resHalf = (price / 100) * 50;
        half_amount.setText(String.valueOf(resHalf));
        full_amount.setText(String.valueOf(price));


        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectedId = radioGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                radioButton = (RadioButton) findViewById(selectedId);


                if (radioButton.getText().equals("Pay 5% now and rest after trip")) {

                    /*Intent intent = new Intent(ReviewCabBooking.this, MyPayment.class);
                    Bundle b = new Bundle();
                    b.putInt("amount", Integer.parseInt(five_percent.getText().toString()));
                    b.putString("email", email.getText().toString());
                    b.putString("phonenumber", phonenumber.getText().toString());
                    b.putString("username", username.getText().toString());
                    intent.putExtras(b);
                    startActivity(intent);*/
                    payment = five_percent.getText().toString();

                    if (username.getText().toString().isEmpty() || email.getText().toString().isEmpty()) {


                        Toast.makeText(ReviewCabBooking.this, "Please enter your booking details", Toast.LENGTH_SHORT).show();


                    } else {

                        startPayment();
                    }


                } else if (radioButton.getText().equals("Pay 50% now and rest after trip")) {

                    payment = half_amount.getText().toString();


                    if (username.getText().toString().isEmpty() || email.getText().toString().isEmpty()) {


                        Toast.makeText(ReviewCabBooking.this, "Please enter your booking details", Toast.LENGTH_SHORT).show();


                    } else {

                        startPayment();
                    }

                    /*Intent intent = new Intent(ReviewCabBooking.this, MyPayment.class);
                    Bundle b = new Bundle();
                    b.putInt("amount", Integer.parseInt(half_amount.getText().toString()));
                    b.putString("email", email.getText().toString());
                    b.putString("phonenumber", phonenumber.getText().toString());
                    b.putString("username", username.getText().toString());
                    intent.putExtras(b);
                    startActivity(intent);*/

                } else {
                    payment = full_amount.getText().toString();
                    if (username.getText().toString().isEmpty() || email.getText().toString().isEmpty()) {


                        Toast.makeText(ReviewCabBooking.this, "Please enter your booking details", Toast.LENGTH_SHORT).show();


                    } else {

                        startPayment();
                    }

                   /* Intent intent = new Intent(ReviewCabBooking.this, MyPayment.class);
                    Bundle b = new Bundle();
                    b.putInt("amount", Integer.parseInt(full_amount.getText().toString()));
                    b.putString("email", email.getText().toString());
                    b.putString("phonenumber", phonenumber.getText().toString());
                    b.putString("username", username.getText().toString());
                    intent.putExtras(b);
                    startActivity(intent);*/

                }
            }
        });

    }


    public void startPayment() {
        /**
         * You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;
        final Checkout co = new Checkout();
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Sawari");
            options.put("description", "App Payment");
            //You can omit the image option to fetch the image from dashboard
            options.put("image", R.drawable.logo1);
            options.put("currency", "INR");
            //payment = orderamount.getText().toString();
            // amount is in paise so please multiple it by 100
            //Payment failed Invalid amount (should be passed in integer paise. Minimum value is 100 paise, i.e. ₹ 1)
            double total = Double.parseDouble(payment);
            total = total * 100;
            options.put("amount", total);
            JSONObject preFill = new JSONObject();
            preFill.put("email", email.getText().toString().trim());
            preFill.put("contact", phonenumber.getText().toString().trim());
            options.put("prefill", preFill);
            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        // payment successfull pay_DGU19rDsInjcF2
        Log.e("TAG", " payment successfull " + s.toString());
        Toast.makeText(this, "Payment successfully done! " + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Log.e("TAG", "error code " + String.valueOf(i) + " -- Payment failed " + s.toString());
        try {
            Toast.makeText(this, "Payment error please try again", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("OnPaymentError", "Exception in onPaymentError", e);
        }
    }


}