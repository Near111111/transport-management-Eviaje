package com.example.welcomscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddShuttle extends AppCompatActivity {

    private EditText editTextName, editTextModel, editTextPlateNumber, editTextColor, editTextSittingCapacity, editTextCoding;
    private String adminUsername = "Arvi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shuttle); // Use the provided layout

        // Initialize EditText fields
        editTextName = findViewById(R.id.editTextName);
        editTextModel = findViewById(R.id.editTextModel);
        editTextPlateNumber = findViewById(R.id.editTextPlateNumber);
        editTextColor = findViewById(R.id.editTextColor);
        editTextSittingCapacity = findViewById(R.id.editTextCapacity);
        editTextCoding = findViewById(R.id.editTextCoding);

        // Set click listener for add button
        findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addShuttle();
            }
        });

        // Set click listener for back button
        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddShuttle.this, AdminShuttles.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void addShuttle() {
        // Retrieve data from EditText fields
        String name = editTextName.getText().toString();
        String model = editTextModel.getText().toString();
        String plateNumber = editTextPlateNumber.getText().toString();
        String color = editTextColor.getText().toString();
        String sittingCapacity = editTextSittingCapacity.getText().toString();
        String coding = editTextCoding.getText().toString();

        // Perform API call in a separate thread
        new Thread(() -> {
            try {
                // Create JSON object with request data
                JSONObject requestData = new JSONObject();
                requestData.put("admin_username", adminUsername);
                requestData.put("name", name);
                requestData.put("model", model);
                requestData.put("plate_number", plateNumber);
                requestData.put("color", color);
                requestData.put("siting_capacity", sittingCapacity); // Corrected parameter name
                requestData.put("coding", coding);

                // Create URL object for API endpoint
                URL url = new URL("https://c889-136-158-57-167.ngrok-free.app/api/shuttles/registerShuttles");

                // Open connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Write JSON data to output stream
                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeBytes(requestData.toString());
                outputStream.flush();
                outputStream.close();

                // Get response code
                int responseCode = connection.getResponseCode();

                // Handle response
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> {
                        // Show success message
                        Toast.makeText(AddShuttle.this, "Shuttle added successfully", Toast.LENGTH_SHORT).show();
                        // Set result as OK and finish activity
                        setResult(RESULT_OK);
//                        finish();
                    });
                } else {
                    runOnUiThread(() -> {
                        // Show error message
                        Toast.makeText(AddShuttle.this, "Failed to add shuttle", Toast.LENGTH_SHORT).show();
                    });
                }

                // Disconnect connection
                connection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    // Show error message
                    Toast.makeText(AddShuttle.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start(); // Start the thread
    }
}
