package com.dinesh.sawari;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Bookings extends BaseActivity {

    int mSelectedItem;
    private static final String SELECTED_ITEM = "arg_selected_item";
    ProgressBar progressbar;
    RecyclerView bookings;
    private LinearLayoutManager linearLayoutManager;
    myadapter adapter;
    String user_id,driver_id;
    LinearLayout layout;
    DatabaseReference mMyCabBookings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressbar= findViewById(R.id.progressbar);
        bookings = (RecyclerView) findViewById(R.id.cabs);
        layout = findViewById(R.id.empty_layout);

        SharedPreferences shared = getSharedPreferences("myAppPrefs", MODE_PRIVATE);
        user_id = (shared.getString("user_id", ""));

        linearLayoutManager = new LinearLayoutManager(this);

        mMyCabBookings = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        bookings.setLayoutManager(linearLayoutManager);

        mMyCabBookings.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                progressbar.setVisibility(View.VISIBLE);

                if (dataSnapshot.hasChild("CabBooking")) {

                    layout.setVisibility(View.GONE);

                    FirebaseRecyclerOptions<CabBookingModel> options =
                            new FirebaseRecyclerOptions.Builder<CabBookingModel>()
                                    .setQuery(mMyCabBookings.child("CabBooking"), CabBookingModel.class)
                                    .build();


                    adapter = new myadapter(options);
                    bookings.setAdapter(adapter);
                    adapter.startListening();
                    progressbar.setVisibility(View.GONE);

                } else {

                    bookings.setVisibility(View.GONE);
                    layout.setVisibility(View.VISIBLE);
                    progressbar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressbar.setVisibility(View.GONE);
            }
        });
    }

    public class myadapter extends FirebaseRecyclerAdapter<CabBookingModel,myadapter.myviewholder> {
        public myadapter(@NonNull FirebaseRecyclerOptions<CabBookingModel> options) {
            super(options);
        }

        @Override
        protected void onBindViewHolder(@NonNull myadapter.myviewholder holder, final int position, @NonNull final CabBookingModel model) {

            progressbar.setVisibility(View.VISIBLE);

            holder.setIsRecyclable(false);

            String str = getRef(position).getKey().toString();
            str = str.replaceAll("[^a-zA-Z0-9]", " ");

            String[] splitStr = str.split("\\s+");

            String from = splitStr[0];
            String to = splitStr[1];
            driver_id = splitStr[2];

            holder.from.setText(from);
            holder.to.setText(to);
            holder.date.setText(model.getDate());
            holder.time.setText(model.getTime());

            if (model.getStatus().equals("pending")) {
                holder.status.setText("Pending");
                holder.status_img.setBackgroundResource(R.drawable.wait);
            } else if (model.getStatus().equals("accept")) {
                holder.status_img.setBackgroundResource(R.drawable.green_tick);
                holder.status.setText("Accepted");
                holder.status.setTextColor(Color.parseColor("#228B22"));

            } else if (model.getStatus().equals("decline")) {
                holder.status_img.setBackgroundResource(R.drawable.cross);
                holder.status.setText("Canceled");
                holder.status.setTextColor(Color.parseColor("#C0C0C0"));

            }
            else if (model.getStatus().equals("confirm")) {
                holder.status_img.setBackgroundResource(R.drawable.check_green);
                holder.status.setText("Confirmed");
                holder.status.setTextColor(Color.parseColor("#228B22"));

            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(Bookings.this, BookingDetails.class);

                   // intent.putExtra("route", routeString);
                    intent.putExtra("from", splitStr[0]);
                    intent.putExtra("to", splitStr[1]);
                    intent.putExtra("date", model.getDate());
                    intent.putExtra("time", model.getTime());
                    intent.putExtra("type", model.getType());
                    intent.putExtra("name", model.getName());
                    intent.putExtra("price", model.getPrice());
                    intent.putExtra("seats", model.getSeats());
                    intent.putExtra("ac", model.getAc());
                    intent.putExtra("status", model.getStatus());
                    intent.putExtra("driver_id", driver_id);
                    intent.putExtra("booking_details", getRef(position).getKey().toString());

                    startActivity(intent);

                }
            });

            progressbar.setVisibility(View.GONE);
        }

        @NonNull
        @Override
        public myadapter.myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cab_booking_model, parent, false);
            return new myadapter.myviewholder(view);
        }

        class myviewholder extends RecyclerView.ViewHolder {

            TextView from,to,date,time,status;
            ImageView status_img;


            public myviewholder(@NonNull View itemView) {
                super(itemView);

                from = (TextView) itemView.findViewById(R.id.from);
                to = (TextView) itemView.findViewById(R.id.to);
                date = (TextView) itemView.findViewById(R.id.date);
                time = (TextView) itemView.findViewById(R.id.time);
                status = (TextView) itemView.findViewById(R.id.status);
                status_img = (ImageView) itemView.findViewById(R.id.status_img);


            }
        }

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