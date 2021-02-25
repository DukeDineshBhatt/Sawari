package com.dinesh.sawari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class Search extends BaseActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    int mSelectedItem;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 123;
    String from_city, to_city;
    TextView date, time, seat;
    private int _day;
    private int _month;
    private int _birthYear;
    EditText to, from;
    Button search;
    CardView find, give;
    int yy, mm, dd;
    ProgressBar progressbar;
    String currentAM_PM = "pm";
    String selectedAM_PM = "pm";
    int currentHourIn12Format, selectedHour12, selectedMinutes, currentAmPm;
    DatabaseReference mLocationsDatabase;
    List<String> listItems;
    LinearLayout layout_date, layout_time;

    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        from = findViewById(R.id.from);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        seat = findViewById(R.id.seat);
        progressbar = findViewById(R.id.progressbar);
        layout_date = findViewById(R.id.layout_date);
        layout_time = findViewById(R.id.layout_time);
        to = findViewById(R.id.to);
        search = findViewById(R.id.search);

        listItems = new ArrayList<String>();

        FirebaseApp.initializeApp(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        mLocationsDatabase = database.getReference("Locations");

        progressbar.setVisibility(View.VISIBLE);

        Calendar c = Calendar.getInstance();
        yy = c.get(Calendar.YEAR);
        mm = c.get(Calendar.MONTH);
        dd = c.get(Calendar.DAY_OF_MONTH);

        _birthYear = c.get(Calendar.YEAR);
        _month = c.get(Calendar.MONTH);
        _day = c.get(Calendar.DAY_OF_MONTH);

        // set current date into textview
        date.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(dd).append("-").append(mm + 1).append("-").append(yy));

        currentHourIn12Format = c.get(Calendar.HOUR_OF_DAY);
        currentAmPm = c.get(Calendar.AM_PM);

        int min = c.get(Calendar.MINUTE);
        selectedHour12 = c.get(Calendar.HOUR);
        selectedMinutes = c.get(Calendar.MINUTE);


        time.setText(String.format("%02d:%02d %s", selectedHour12, min,
                c.get(Calendar.HOUR_OF_DAY) < 12 ? "am" : "pm"));

        if (currentAmPm == 0) {
            currentAM_PM = "am";

        }

        mLocationsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    listItems.add(postSnapshot.child("Name").getValue().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        this.layout_date.setOnClickListener(this);

        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //final CharSequence[] items = {"Pithoragarh", "Haldwani", "Nainital", "Delhi",};

                AlertDialog.Builder builder = new AlertDialog.Builder(Search.this);
                builder.setTitle("Select City");
                builder.setCancelable(false);
                builder.setSingleChoiceItems(listItems.toArray(new String[listItems.size()]), -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        //Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
                        from_city = listItems.get(item).toString();

                    }
                });

                builder.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                from.setText(from_city);
                            }
                        });
                builder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Toast.makeText(MainActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Search.this);
                builder.setTitle("Select City");
                builder.setCancelable(false);
                builder.setSingleChoiceItems(listItems.toArray(new String[listItems.size()]), -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        //Toast.makeText(getApplicationContext(), items1[item], Toast.LENGTH_SHORT).show();
                        to_city = listItems.get(item);

                    }
                });

                builder.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                to.setText(to_city);
                            }
                        });
                builder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Toast.makeText(MainActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        layout_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                CustomTimePickerDialog mTimePicker;
                mTimePicker = new CustomTimePickerDialog(Search.this, new CustomTimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {

                        Calendar datetime = Calendar.getInstance();
                        datetime.set(Calendar.HOUR_OF_DAY, i);
                        datetime.set(Calendar.MINUTE, i1);

                        selectedHour12 = i % 12;
                        if (selectedHour12 == 0)
                            selectedHour12 = 12;

                        if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                            selectedAM_PM = "am";
                        else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                            selectedAM_PM = "pm";

                        selectedMinutes = i1;
                        time.setText(String.format("%02d:%02d %s", selectedHour12, i1,
                                i < 12 ? "am" : "pm"));
                    }


                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();


            }
        });


        seat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Search.this, CountActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.nothing);


            }
        });

        progressbar.setVisibility(View.GONE);


    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(Search.this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        _birthYear = year;
        _month = monthOfYear;
        _day = dayOfMonth;
        updateDisplay();
    }

    @Override
    public void onClick(View v) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        DatePickerDialog dialog = new DatePickerDialog(Search.this, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();

    }

    private void updateDisplay() {

        date.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(_day).append("-").append(_month + 1).append("-").append(_birthYear).append(" "));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("tag", "Place: " + place.getName() + ", " + place.getLatLng());


            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("tag", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_search;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.search;
    }
}