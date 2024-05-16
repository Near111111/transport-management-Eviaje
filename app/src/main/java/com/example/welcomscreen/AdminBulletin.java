package com.example.welcomscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AdminBulletin extends AppCompatActivity {

    ImageButton imageButton;
    String jsonString = "{\"holidays\":[\"New Year's Day\",\"Easter Sunday\",\"Independence Day\",\"Christmas Day\"]}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_bulletin);

        ImageButton shuttlesButton = findViewById(R.id.shuttles);
        ImageButton driverButton = findViewById(R.id.driver);
        ImageButton passengersButton = findViewById(R.id.passengers);
        ImageButton bulletinButton = findViewById(R.id.calendar);


        shuttlesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Shuttles activity
                Intent intent = new Intent(AdminBulletin.this, AdminShuttles.class);
                startActivity(intent);
            }
        });

        driverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Driver activity
                Intent intent = new Intent(AdminBulletin.this, AdminDriver.class);
                startActivity(intent);
            }
        });

        passengersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Passengers activity
                Intent intent = new Intent(AdminBulletin.this, AdminPassengers.class);
                startActivity(intent);
            }
        });

        bulletinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Bulletin activity
                Intent intent = new Intent(AdminBulletin.this, AdminSchedule.class);
                startActivity(intent);
            }
        });

        // Set the current date with "Holidays" text
        TextView textDate = findViewById(R.id.hollidays1);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM, yyyy", Locale.getDefault());
        String currentDate = sdf.format(calendar.getTime());
        textDate.setText(currentDate + " Holidays");

        // Set up ImageButton to navigate to AdminEditAnnouncement activity
        imageButton = findViewById(R.id.imageButton15);
        imageButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminBulletin.this, AdminEditAnnouncement.class);
            startActivity(intent);
            finish();
        });

        // Populate the holiday list using the JSON object
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray holidaysArray = jsonObject.getJSONArray("holidays");
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < holidaysArray.length(); i++) {
                stringBuilder.append("- ").append(holidaysArray.getString(i)).append("\n");
            }
            TextView textHolidays = findViewById(R.id.textView19);
            textHolidays.setText(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}