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

public class EditVehicleActivity extends AppCompatActivity {

    private EditText editTextName, editTextModel, editTextPlateNumber, editTextColor, editTextSittingCapacity, editTextCoding;
    private String adminUsername = "Arvi";

    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_vehicle);

        // Initialize EditText fields
        editTextName = findViewById(R.id.editTextName);
        editTextModel = findViewById(R.id.editTextModel);
        editTextPlateNumber = findViewById(R.id.editTextPlateNumber);
        editTextColor = findViewById(R.id.editTextColor);
        editTextSittingCapacity = findViewById(R.id.editTextCapacity);
        editTextCoding = findViewById(R.id.editTextCoding);

        // Get intent data and populate EditText fields
        Intent intent = getIntent();
        editTextName.setText(intent.getStringExtra("name"));
        editTextModel.setText(intent.getStringExtra("model"));
        editTextPlateNumber.setText(intent.getStringExtra("plate_number"));
        editTextColor.setText(intent.getStringExtra("color"));
        editTextSittingCapacity.setText(String.valueOf(intent.getIntExtra("sitting_capacity", 0)));
        editTextCoding.setText(intent.getStringExtra("coding"));

        // Set click listener for save button
        findViewById(R.id.buttonSave1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });
//        backButton.setOnClickListener(v -> startActivity(new Intent(EditVehicleActivity.this, AdminShuttles.class)));
        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditVehicleActivity.this, AdminShuttles.class);
                startActivity(intent);
                finish();
            }
        });
    }


    private void saveChanges() {
        // Retrieve data from EditText fields
        String name = editTextName.getText().toString();
        String model = editTextModel.getText().toString();
        String plateNumber = editTextPlateNumber.getText().toString();
        String color = editTextColor.getText().toString();
        String sittingCapacity = editTextSittingCapacity.getText().toString(); // Corrected parameter name
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
                URL url = new URL(ApiConfig.API_URL + "/api/shuttles/updateShuttles");

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
                        Toast.makeText(EditVehicleActivity.this, "Changes saved", Toast.LENGTH_SHORT).show();
                        // Set result as OK and finish activity
                        setResult(RESULT_OK);
//                        finish();
                    });
                } else {
                    runOnUiThread(() -> {
                        // Show error message
                        Toast.makeText(EditVehicleActivity.this, "Failed to update shuttle details", Toast.LENGTH_SHORT).show();
                    });
                }

                // Disconnect connection
                connection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    // Show error message
                    Toast.makeText(EditVehicleActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start(); // Start the thread
    }

}
