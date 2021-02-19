package com.dinesh.sawari;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dinesh.sawari.NOtificationPOJO.Data;
import com.dinesh.sawari.NOtificationPOJO.DataBean;
import com.dinesh.sawari.NOtificationPOJO.ResultBean;
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

    String token;
    String baseurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cab_booking_success);

        Intent intent = getIntent();
        token = intent.getStringExtra("driver_token");

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
        body.setTo(token);
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
}