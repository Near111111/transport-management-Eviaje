package com.example.welcomscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AdminShuttles extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VehicleAdapter vehicleAdapter;
    private List<Vehicle> vehicleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_shuttles);

        ImageButton shuttlesButton = findViewById(R.id.calendar);
        ImageButton driverButton = findViewById(R.id.driver);
        ImageButton passengersButton = findViewById(R.id.passengers);
        ImageButton bulletinButton = findViewById(R.id.bulletin);


        shuttlesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Shuttles activity
                Intent intent = new Intent(AdminShuttles.this, AdminSchedule.class);
                startActivity(intent);
            }
        });

        driverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Driver activity
                Intent intent = new Intent(AdminShuttles.this, AdminDriver.class);
                startActivity(intent);
            }
        });

        passengersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Passengers activity
                Intent intent = new Intent(AdminShuttles.this, AdminPassengers.class);
                startActivity(intent);
            }
        });

        bulletinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Bulletin activity
                Intent intent = new Intent(AdminShuttles.this, AdminBulletin.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        vehicleList = new ArrayList<>();
        vehicleList.add(new Vehicle("Hi-Ace 1", "Toyota Hi Ace 2024", "12345", "White", 10, "Wednesday", R.drawable.hiace));

        vehicleAdapter = new VehicleAdapter(this, vehicleList);
        recyclerView.setAdapter(vehicleAdapter);

        ImageButton buttonPlus = findViewById(R.id.button_plus);
        buttonPlus.setOnClickListener(v -> {
            // Handle add vehicle button click event
            addVehicle();
        });

        ImageButton buttonMinus = findViewById(R.id.button_minus);
        buttonMinus.setOnClickListener(v -> {
            // Handle remove vehicle button click event
            removeVehicle();
        });

    }

    private void addVehicle() {
        // Add a new vehicle with hardcoded data
        vehicleList.add(new Vehicle("New Vehicle", "Model X", "00000", "Black", 8, "Friday", R.drawable.hiace1));
        vehicleAdapter.notifyItemInserted(vehicleList.size() - 1);
        recyclerView.scrollToPosition(vehicleList.size() - 1);
    }

    private void removeVehicle() {
        // Remove the last vehicle in the list
        if (!vehicleList.isEmpty()) {
            int lastIndex = vehicleList.size() - 1;
            vehicleList.remove(lastIndex);
            vehicleAdapter.notifyItemRemoved(lastIndex);
        }
    }

}
