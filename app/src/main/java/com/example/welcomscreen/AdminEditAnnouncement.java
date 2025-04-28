package com.example.welcomscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class AdminEditAnnouncement extends AppCompatActivity {

    EditText editTextAnnouncement;
    Button buttonSubmit;
    ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_announcement);


        imageButton = findViewById(R.id.imageButton6);
        imageButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminEditAnnouncement.this, AdminBulletin.class);
            startActivity(intent);
            finish();
        });

        Intent intent = getIntent();
        String existingAnnouncements = intent.getStringExtra("existingAnnouncements");
        editTextAnnouncement = findViewById(R.id.editText_announcement);
        buttonSubmit = findViewById(R.id.button_submit);
        if (existingAnnouncements != null) {
            editTextAnnouncement.setText(existingAnnouncements.trim());
        }

            editTextAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Allow user to edit the text when clicked
                editTextAnnouncement.setFocusableInTouchMode(true);
                editTextAnnouncement.requestFocus();
            }
        });


        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEnteredText = editTextAnnouncement.getText().toString();
                String apiUrl = "https://c889-136-158-57-167.ngrok-free.app/api/passenger/updateBulletinAnnouncement";

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(apiUrl);
                            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                            urlConnection.setRequestMethod("POST");
                            urlConnection.setRequestProperty("Content-Type", "application/json");
                            urlConnection.setDoOutput(true);

                            // Create JSON data to send
                            JSONObject jsonParam = new JSONObject();
                            jsonParam.put("admin_username", "Arvi");
                            jsonParam.put("announcement", userEnteredText);

                            // Write data to the connection output stream
                            OutputStream outputStream = urlConnection.getOutputStream();
                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                            writer.write(jsonParam.toString());
                            writer.flush();
                            writer.close();
                            outputStream.close();

                            // Get response from the server
                            int responseCode = urlConnection.getResponseCode();
                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                // Successful API call
                                // Handle success as needed
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(AdminEditAnnouncement.this, "Announcement updated successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                // API call failed
                                // Handle failure as needed
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(AdminEditAnnouncement.this, "Failed to update announcement", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            urlConnection.disconnect(); // Close the connection
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            // Handle exception
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(AdminEditAnnouncement.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }
}