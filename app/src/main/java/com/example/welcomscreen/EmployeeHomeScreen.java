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

public class EmployeeHomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_home_screen);
        ImageButton livetrackingButton = findViewById(R.id.imageView18);
        ImageButton scheduleButton = findViewById(R.id.imageView15);
        ImageButton profileButton = findViewById(R.id.imageView19);


        livetrackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Driver activity
                Intent intent = new Intent(EmployeeHomeScreen.this, PassengerActivity.class);
                startActivity(intent);
            }
        });

        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Passengers activity
                Intent intent = new Intent(EmployeeHomeScreen.this, EmployeeSchedule.class);
                startActivity(intent);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Bulletin activity
                Intent intent = new Intent(EmployeeHomeScreen.this, EmployeeProfile.class);
                startActivity(intent);
            }
        });
    }
}