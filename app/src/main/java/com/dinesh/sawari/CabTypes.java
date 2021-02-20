package com.dinesh.sawari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CabTypes extends AppCompatActivity {

    Toolbar toolbar;
    String from, to, datetxt, timetxt, routeString;
    TextView date, time;
    RecyclerView cabs;
    private LinearLayoutManager linearLayoutManager;
    myadapter adapter;
    ProgressBar progressbar;
    DatabaseReference mCabsDatabase, mCabsBookingRequest;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cab_types);

        toolbar = findViewById(R.id.toolbar);
        date = findViewById(R.id.datetxt);
        time = findViewById(R.id.time);
        layout = findViewById(R.id.layout);
        progressbar = findViewById(R.id.progressbar);
        cabs = (RecyclerView) findViewById(R.id.cabs);


        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        from = intent.getStringExtra("from");
        to = intent.getStringExtra("to");
        datetxt = intent.getStringExtra("date");
        timetxt = intent.getStringExtra("time");

        getSupportActionBar().setTitle(from + " - " + to);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        progressbar.setVisibility(View.VISIBLE);

        date.setText(datetxt);
        time.setText(timetxt);
        routeString = from + " - " + to;


        linearLayoutManager = new LinearLayoutManager(this);

        mCabsDatabase = FirebaseDatabase.getInstance().getReference().child("Cabs");
        mCabsBookingRequest = FirebaseDatabase.getInstance().getReference().child("DriversCabBookings");

        cabs.setLayoutManager(linearLayoutManager);


        mCabsBookingRequest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(routeString)) {

                    layout.setVisibility(View.GONE);

                    FirebaseRecyclerOptions<CabsModel> options =
                            new FirebaseRecyclerOptions.Builder<CabsModel>()
                                    .setQuery(mCabsBookingRequest.child(routeString), CabsModel.class)
                                    .build();


                    adapter = new myadapter(options);
                    cabs.setAdapter(adapter);
                    adapter.startListening();
                    progressbar.setVisibility(View.GONE);

                } else {

                    cabs.setVisibility(View.GONE);
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


    public class myadapter extends FirebaseRecyclerAdapter<CabsModel, myadapter.myviewholder> {
        public myadapter(@NonNull FirebaseRecyclerOptions<CabsModel> options) {
            super(options);
        }

        @Override
        protected void onBindViewHolder(@NonNull myadapter.myviewholder holder, final int position, @NonNull final CabsModel model) {

            holder.setIsRecyclable(false);

            if (model.getType().equals("Lite")) {

                Glide.with(getApplicationContext()).load(R.drawable.wagonr).into(holder.image);
            } else if (model.getType().equals("Comfort")) {

                Glide.with(getApplicationContext()).load(R.drawable.dzire).into(holder.image);

            } else {
                Glide.with(getApplicationContext()).load(R.drawable.innova).into(holder.image);

            }

            holder.type.setText(model.getType());
            holder.price.setText(String.valueOf(model.getPrice()));
            holder.name.setText(model.getName() + " or Equivalent");
            if (model.getAc().equals("yes")) {

                holder.ac.setText("AC");
            } else {
                holder.ac.setText("Non-AC");
            }

            if (model.getType().equals("Lite")) {
                holder.bags.setText("2 Bags");
            } else if (model.getType().equals("comfort")) {
                holder.bags.setText("3 Bags");
            } else if (model.getType().equals("comfort")) {
                holder.bags.setText("4 Bags");
            } else if (model.getType().equals("6 Plus")) {
                holder.bags.setText("4 Bags");
            }else if (model.getType().equals("6 Pro")) {
                holder.bags.setText("4 Bags");
            }

            holder.seats.setText(model.getSeats() + " seats");

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(CabTypes.this, ReviewCabBooking.class);

                    intent.putExtra("route", routeString);
                    intent.putExtra("from", from);
                    intent.putExtra("to", to);
                    intent.putExtra("time", timetxt);
                    intent.putExtra("date", date.getText().toString());
                    intent.putExtra("type", model.getType());
                    intent.putExtra("name", model.getName());
                    intent.putExtra("price", String.valueOf(model.getPrice()));
                    intent.putExtra("seats", String.valueOf(model.getSeats()));
                    intent.putExtra("ac", String.valueOf(model.getAc()));
                    intent.putExtra("driver", String.valueOf(getRef(position).getKey().toString()));

                    startActivity(intent);

                }
            });

        }

        @NonNull
        @Override
        public myadapter.myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cabs_model, parent, false);
            return new myadapter.myviewholder(view);
        }

        class myviewholder extends RecyclerView.ViewHolder {

            TextView type, name, ac, bags, seats, price;
            ImageView image;


            public myviewholder(@NonNull View itemView) {
                super(itemView);

                type = (TextView) itemView.findViewById(R.id.type);
                name = (TextView) itemView.findViewById(R.id.name);
                ac = (TextView) itemView.findViewById(R.id.ac);
                bags = (TextView) itemView.findViewById(R.id.bags);
                seats = (TextView) itemView.findViewById(R.id.seats);
                price = (TextView) itemView.findViewById(R.id.price);
                image = (ImageView) itemView.findViewById(R.id.image);

            }
        }

    }
}