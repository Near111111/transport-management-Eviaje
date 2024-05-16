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

public class EmployeeProfile extends AppCompatActivity {

    private ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_profile);
        ImageButton homeButton = findViewById(R.id.imageView14);
        ImageButton livetrackingButton = findViewById(R.id.imageView18);
        ImageButton sceduleButton = findViewById(R.id.imageView15);
        ImageButton logoutButton = findViewById(R.id.imageButton21);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Shuttles activity
                Intent intent = new Intent(EmployeeProfile.this, EmployeeHomeScreen.class);
                startActivity(intent);
            }
        });

        livetrackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Driver activity
                Intent intent = new Intent(EmployeeProfile.this, PassengerActivity.class);
                startActivity(intent);
            }
        });

        sceduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Bulletin activity
                Intent intent = new Intent(EmployeeProfile.this, EmployeeSchedule.class);
                startActivity(intent);
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Driver activity
                Intent intent = new Intent(EmployeeProfile.this, MainActivity3.class);
                startActivity(intent);
            }
        });
    }
}