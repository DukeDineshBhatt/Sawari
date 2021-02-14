package com.dinesh.sawari;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class Bookings extends BaseActivity {

    int mSelectedItem;
    private static final String SELECTED_ITEM = "arg_selected_item";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(Bookings.this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    int getContentViewId() {
        return R.layout.activity_bookings;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.booking;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_ITEM, mSelectedItem);
        super.onSaveInstanceState(outState);
    }
}