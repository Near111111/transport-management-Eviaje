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

public class EmployeeSchedule extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_schedule);
        ImageButton homeButton = findViewById(R.id.imageButton19);
        ImageButton livetrackingButton = findViewById(R.id.imageButton18);
        ImageButton profileButton = findViewById(R.id.imageButton17);


        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Shuttles activity
                Intent intent = new Intent(EmployeeSchedule.this, EmployeeHomeScreen.class);
                startActivity(intent);
            }
        });

        livetrackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Driver activity
                Intent intent = new Intent(EmployeeSchedule.this, PassengerActivity.class);
                startActivity(intent);
            }
        });


        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Bulletin activity
                Intent intent = new Intent(EmployeeSchedule.this, EmployeeProfile.class);
                startActivity(intent);
            }
        });
    }
}