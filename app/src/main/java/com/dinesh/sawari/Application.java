package com.dinesh.sawari;

import android.content.Context;

import com.razorpay.Checkout;

public class Application extends android.app.Application {

    private static Context context;
    public String baseurl = "https://www.fast2sms.com/";
    public String notificationBaseurl = "https://fcm.googleapis.com/";


    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
    }
}
