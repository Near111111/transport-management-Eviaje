package com.example.welcomscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class EditHoliday extends AppCompatActivity {

    private ImageButton backButton;
    private ImageButton saveButton;
    private EditText holidayEditText;
    private EditText announcementEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_holiday);

        backButton = findViewById(R.id.imageButton_back);
        saveButton = findViewById(R.id.imageButton_save);
        holidayEditText = findViewById(R.id.editText_holiday);
        announcementEditText = findViewById(R.id.editText_announcement);

        // Retrieve existing texts from the Intent
        Intent intent = getIntent();
        String existingHolidays = intent.getStringExtra("existingHolidays");
//        String existingAnnouncements = intent.getStringExtra("existingAnnouncements");

        // Set the existing text to the respective EditTexts
        if (existingHolidays != null) {
            // Paghihiwalayin ang holiday at announcement base sa "-"
            String[] parts = existingHolidays.split(" - ");

            if (parts.length == 2) {
                // I-set ang holiday sa holidayEditText
                holidayEditText.setText(parts[0].trim());
                // I-set ang announcement sa announcementEditText
                announcementEditText.setText(parts[1].trim());
            } else {
                // I-set ang holiday sa holidayEditText
                holidayEditText.setText(parts[0].trim());
                // I-set ang announcement sa announcementEditText
                announcementEditText.setText(parts[1].trim());
            }
        }
//        if (existingAnnouncements != null) {
//            announcementEditText.setText(existingAnnouncements.trim());
//        }

        backButton.setOnClickListener(v -> onBackPressed());
        backButton.setOnClickListener(v -> startActivity(new Intent(EditHoliday.this, AdminBulletin.class)));
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String holiday = holidayEditText.getText().toString().trim();
                String announcement = announcementEditText.getText().toString().trim();

                if (!holiday.isEmpty() && !announcement.isEmpty()) {
                    // Call the method to save data
                    saveAnnouncement(holiday, announcement);
                } else {
                    saveAnnouncement(holiday, announcement);
//                    Toast.makeText(EditHoliday.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveAnnouncement(String holiday, String announcement) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // URL of the API
                    String apiUrl = "https://c889-136-158-57-167.ngrok-free.app/api/user/addBulletinMessage";

                    // Create a JSON object to send to the API
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("admin_username", "Arvi");
                    jsonBody.put("holiday", holiday);
                    jsonBody.put("announcement", announcement);

                    // Open connection
                    URL url = new URL(apiUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    // Write data to the connection
                    OutputStream outputStream = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    writer.write(jsonBody.toString());
                    writer.flush();
                    writer.close();
                    outputStream.close();

                    // Get the response code
                    int responseCode = connection.getResponseCode();

                    // Check if data is successfully saved
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(EditHoliday.this, "Announcement saved successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(EditHoliday.this, "Announcement to save announcement", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    // Close connection
                    connection.disconnect();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(EditHoliday.this, "Failed to save announcement", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
}
