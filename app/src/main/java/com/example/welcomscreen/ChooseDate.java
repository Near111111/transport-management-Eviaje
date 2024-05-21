package com.example.welcomscreen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ChooseDate extends AppCompatActivity {

    private CalendarView calendarView;
    private ImageButton saveButton;
    private TextView textViewSelectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choose_date);

        // Initialize UI components
        calendarView = findViewById(R.id.calendarView);
        saveButton = findViewById(R.id.imageButton13);
        textViewSelectedDate = findViewById(R.id.textViewSelectedDate);

        // Set up back button to go to AdminNewSchedule activity
        ImageButton imageButtonBack = findViewById(R.id.imageButton4);
        imageButtonBack.setOnClickListener(v -> {
            Intent intent = new Intent(ChooseDate.this, AdminNewSchedule.class);
            startActivity(intent);
            finish();
        });

        // Set up calendar date change listener
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Format the date
            String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            // Display the selected date in the TextView
            textViewSelectedDate.setText("Selected Date: " + selectedDate);
        });

        // Set up save button click listener
        saveButton.setOnClickListener(v -> {
            // Get the selected date from the TextView
            String selectedDate = textViewSelectedDate.getText().toString();
            // Handle save logic here (e.g., save to database, pass to another activity)
            // For now, just display a message
            Intent intent = new Intent(ChooseDate.this, AdminNewSchedule.class); // Change this to your target activity
            intent.putExtra("SELECTED_DATE", selectedDate);
            startActivity(intent);
        });

        // Apply window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
