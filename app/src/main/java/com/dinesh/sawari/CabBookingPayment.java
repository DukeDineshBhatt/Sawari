package com.dinesh.sawari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.HashMap;

public class CabBookingPayment extends AppCompatActivity implements PaymentResultListener {

    Toolbar toolbar;
    Button pay;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    String pricetxt, payment;
    TextView five_percent, half_amount, full_amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cab_booking_payment);

        toolbar = findViewById(R.id.toolbar);
        pay = findViewById(R.id.pay);
        five_percent = findViewById(R.id.five_percent);
        half_amount = findViewById(R.id.half_amount);
        full_amount = findViewById(R.id.full_amount);
        radioGroup = (RadioGroup) findViewById(R.id.radio);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Booking Payment");

        getSupportActionBar().setDisplayShowTitleEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        Intent intent = getIntent();
        pricetxt = intent.getStringExtra("price");


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

                radioButton = (RadioButton) findViewById(selectedId);

                if (radioButton.getText().equals("Pay 5% now and rest after trip")) {

                    payment = five_percent.getText().toString();

                    setProgressDialog();

                    startPayment();

                    /*HashMap<String, Object> taskMap = new HashMap<>();
                        taskMap.put("price", pricetxt);
                        taskMap.put("type", typetxt);
                        taskMap.put("name", nametxt);
                        taskMap.put("seats", seatstxt);
                        taskMap.put("ac", actxt);
                        taskMap.put("date", datetxt);
                        taskMap.put("time", timetxt);
                        taskMap.put("status", "pending");

                        usersRef.child(user_id).child("CabBooking").child(driver_intent).updateChildren(taskMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {

                                    mDriverDatabase.child(driver_id).child("BookingRequest").child(fromtxt + " - " + totxt + " " + user_id + " " + car_number).updateChildren(taskMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                Intent intent = new Intent(CabBookingPayment.this, CabBookingSuccess.class);
                                                Bundle b = new Bundle();

                                                intent.putExtra("route", routeString);
                                                intent.putExtra("from", fromtxt);
                                                intent.putExtra("to", totxt);
                                                intent.putExtra("date", datetxt);
                                                intent.putExtra("time", timetxt);
                                                intent.putExtra("type", typetxt);
                                                intent.putExtra("name", nametxt);
                                                intent.putExtra("price", pricetxt);
                                                intent.putExtra("seats", seatstxt);
                                                intent.putExtra("ac", actxt);
                                                intent.putExtra("driver_token", driver_token);
                                                intent.putExtra("driver_intent", driver_intent);
                                                intent.putExtras(b);
                                                startActivity(intent);

                                            } else {

                                                Toast.makeText(CabBookingPayment.this, "Something wrong! Please Try again.", Toast.LENGTH_SHORT).show();

                                            }

                                        }
                                    });


                                } else {
                                    Toast.makeText(CabBookingPayment.this, "Something wrong! Please Try again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });*/


                } else if (radioButton.getText().equals("Pay 50% now and rest after trip")) {

                    payment = half_amount.getText().toString();


                    //startPayment();


                } else {
                    payment = full_amount.getText().toString();

                    //startPayment();


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
            preFill.put("email", "email@gmail.com");
            preFill.put("contact", "123456789");
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

        Intent intent = new Intent(CabBookingPayment.this, CabBookingSuccess.class);
        Bundle b = new Bundle();
        b.putInt("amount", Integer.parseInt(full_amount.getText().toString()));
        b.putString("email", "email@gmail.com");
        b.putString("phonenumber", "123456789");
        b.putString("username", "username");
        b.putString("driver_token", "driver_token");
        intent.putExtras(b);
        startActivity(intent);
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

}