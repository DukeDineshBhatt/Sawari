package com.dinesh.sawari;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends BaseActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private static final int AUTOCOMPLETE_REQUEST_CODE = 123;
    public static final String PREFS_NAME = "MyPrefsFile";
    private Toolbar toolbar;
    int mSelectedItem;
    String from_city, to_city;
    TextView date, time;
    private int _day;
    private int _month;
    private int _birthYear;
    EditText to, from;
    Button search;
    int yy, mm, dd;
    String currentAM_PM = "pm";
    String selectedAM_PM = "pm";
    int currentHourIn12Format, selectedHour12, selectedMinutes, currentAmPm;
    DatabaseReference mLocationsDatabase;
    List<String> listItems;
    LinearLayout layout_date, layout_time;
    private static final String SELECTED_ITEM = "arg_selected_item";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("hasLoggedIn", true);
        editor.commit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        from = findViewById(R.id.from);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        layout_date = findViewById(R.id.layout_date);
        layout_time = findViewById(R.id.layout_time);
        to = findViewById(R.id.to);
        search = findViewById(R.id.search);

        listItems = new ArrayList<String>();

        FirebaseApp.initializeApp(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        mLocationsDatabase = database.getReference("Locations");

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


    }

    @Override
    protected void onResume() {
        super.onResume();


        this.layout_date.setOnClickListener(this);

        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //final CharSequence[] items = {"Pithoragarh", "Haldwani", "Nainital", "Delhi",};

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
                mTimePicker = new CustomTimePickerDialog(MainActivity.this, new CustomTimePickerDialog.OnTimeSetListener() {
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

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (from.getText().toString().trim().isEmpty()) {

                    Toast.makeText(MainActivity.this, "Select Pickup location", Toast.LENGTH_SHORT).show();

                } else if (to.getText().toString().trim().isEmpty()) {

                    Toast.makeText(MainActivity.this, "Select Drop location", Toast.LENGTH_SHORT).show();

                } else if (from.getText().toString().equals(to.getText().toString())) {

                    AlertDialog.Builder popupBuilder = new AlertDialog.Builder(MainActivity.this);
                    TextView myMsg = new TextView(MainActivity.this);

                    popupBuilder.setPositiveButton("OK", null);
                    popupBuilder.setCancelable(false);
                    myMsg.setText("Pickup and Drop location cannot be from the same city.Please try alternate route.");
                    myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
                    myMsg.setTextColor(Color.parseColor("#000000"));
                    myMsg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                    myMsg.setPadding(45, 96, 45, 45);
                    popupBuilder.setView(myMsg);
                    popupBuilder.show();

                } else if (mm == _month && dd == _day && selectedAM_PM.equals(currentAM_PM) && selectedHour12 <= currentHourIn12Format) {

                    AlertDialog.Builder popupBuilder = new AlertDialog.Builder(MainActivity.this);
                    TextView myMsg = new TextView(MainActivity.this);

                    popupBuilder.setPositiveButton("OK", null);
                    popupBuilder.setCancelable(false);
                    myMsg.setText("Please select Pickup time at least 4 hours from now.");
                    myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
                    myMsg.setTextColor(Color.parseColor("#000000"));
                    myMsg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                    myMsg.setPadding(45, 96, 45, 45);
                    popupBuilder.setView(myMsg);
                    popupBuilder.show();

                } else if (mm == _month && dd == _day && selectedAM_PM.equals(currentAM_PM) && selectedHour12 < currentHourIn12Format + 4) {

                    AlertDialog.Builder popupBuilder = new AlertDialog.Builder(MainActivity.this);
                    TextView myMsg = new TextView(MainActivity.this);

                    popupBuilder.setPositiveButton("OK", null);
                    popupBuilder.setCancelable(false);
                    myMsg.setText("Please select Pickup time at least 4 hours from now.");
                    myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
                    myMsg.setTextColor(Color.parseColor("#000000"));
                    myMsg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                    myMsg.setPadding(45, 96, 45, 45);
                    popupBuilder.setView(myMsg);
                    popupBuilder.show();

                } else {
                    //Toast.makeText(MainActivity.this, "selectedAM_PM" + selectedAM_PM + " currentAM_PM" + currentAM_PM, Toast.LENGTH_SHORT).show();

                    from.setText("");
                    to.setText("");
                    Intent intent = new Intent(MainActivity.this, CabTypes.class);
                    intent.putExtra("from", from_city);
                    intent.putExtra("to", to_city);
                    intent.putExtra("date", date.getText().toString());
                    intent.putExtra("time", selectedHour12 + ":" + selectedMinutes + "" + selectedAM_PM);
                    startActivity(intent);
                }

            }
        });


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

        DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, this,
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
        return R.layout.activity_main;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.homee;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_ITEM, mSelectedItem);
        super.onSaveInstanceState(outState);
    }
    @Override
    public void onBackPressed() {

        finishAffinity();


    }

}

