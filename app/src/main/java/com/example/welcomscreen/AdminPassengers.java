package com.example.welcomscreen;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AdminPassengers extends AppCompatActivity {
    private RecyclerView recyclerView;
    public static ContactAdapterForPassengers contactAdapter;
    public static List<ContactCardForPassengers> contactCardList;
    private Button nextButton;
    private Button prevButton;

    private ImageButton imageButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_passengers);

        ImageButton shuttlesButton = findViewById(R.id.shuttles);
        ImageButton driverButton = findViewById(R.id.driver);
        ImageButton passengersButton = findViewById(R.id.calendar);
        ImageButton bulletinButton = findViewById(R.id.bulletin);


        shuttlesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Shuttles activity
                Intent intent = new Intent(AdminPassengers.this, AdminShuttles.class);
                startActivity(intent);
            }
        });

        driverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Driver activity
                Intent intent = new Intent(AdminPassengers.this, AdminDriver.class);
                startActivity(intent);
            }
        });

        passengersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Passengers activity
                Intent intent = new Intent(AdminPassengers.this, AdminSchedule.class);
                startActivity(intent);
            }
        });

        bulletinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Bulletin activity
                Intent intent = new Intent(AdminPassengers.this, AdminBulletin.class);
                startActivity(intent);
            }
        });

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize pagination controls
        nextButton = findViewById(R.id.nextButton);
        prevButton = findViewById(R.id.prevButton);



        imageButton = findViewById(R.id.imageButton20);
        imageButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminPassengers.this, TestForPassengers.class);
            startActivity(intent);
            finish();


        });

        // Create sample ContactCard objects
        contactCardList = new ArrayList<>();
        contactCardList.add(new ContactCardForPassengers("Jo", "1234567890", "john.doe@example.com", "123 Main St"));
        contactCardList.add(new ContactCardForPassengers("tr", "1234567890", "john.doe@example.com", "123 Main St"));
        contactCardList.add(new ContactCardForPassengers("John e", "1234567890", "john.doe@example.com", "123 Main St"));
        contactCardList.add(new ContactCardForPassengers("Johnqrqerwerew Doe", "1234567890", "john.doe@example.com", "123 Main St"));
        // Add more sample contact cards as needed

        // Initialize and set adapter to RecyclerView
        contactAdapter = new ContactAdapterForPassengers(contactCardList);
        recyclerView.setAdapter(contactAdapter);

        // Set onClickListeners for pagination controlsa
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactAdapter.nextPage();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactAdapter.previousPage();
            }
        });
    }
}