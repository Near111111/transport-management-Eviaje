package com.example.welcomscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminAddEditGroup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_add_edit_group);
        ImageButton shuttlesButton = findViewById(R.id.shuttles);
        ImageButton driverButton = findViewById(R.id.driver);
        ImageButton passengersButton = findViewById(R.id.passengers);
        ImageButton CalendarButton = findViewById(R.id.calendar);
        ImageButton bulletinButton1 = findViewById(R.id.bulletin);


        shuttlesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Shuttles activity
                Intent intent = new Intent(AdminAddEditGroup.this, AdminShuttles.class);
                startActivity(intent);
            }
        });

        driverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Driver activity
                Intent intent = new Intent(AdminAddEditGroup.this, AdminDriver.class);
                startActivity(intent);
            }
        });

        passengersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Passengers activity
                Intent intent = new Intent(AdminAddEditGroup.this, AdminPassengers.class);
                startActivity(intent);
            }
        });

        bulletinButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Bulletin activity
                Intent intent = new Intent(AdminAddEditGroup.this, AdminBulletin.class);
                startActivity(intent);
            }
        });
        CalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Bulletin activity
                Intent intent = new Intent(AdminAddEditGroup.this, AdminSchedule.class);
                startActivity(intent);
            }
        });
    }
}