package com.example.welcomscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestForPassengers extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_for_passengers);

        ImageButton imageButton20 = findViewById(R.id.imageButton20);
        Button cancel = findViewById(R.id.saveButton2);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText first_name = findViewById(R.id.editText1); // first_name
                EditText last_name = findViewById(R.id.editTextLastName);
                EditText phone = findViewById(R.id.editText2); // phone
                EditText email = findViewById(R.id.editText3); // email
                EditText editTextUsername = findViewById(R.id.editTextUsername);
                Spinner spinnerGroup = findViewById(R.id.spinnerGroup);

                first_name.setText("");
                last_name.setText("");
                phone.setText("");
                email.setText("");
                editTextUsername.setText("");
            }
        });
        imageButton20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEditMode();
            }
        });

        // Define group array
        String[] groupArray = {"Manila City Group", "Quezon City Group"};

        // Populate Spinner with group array
        Spinner spinnerGroup = findViewById(R.id.spinnerGroup);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groupArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGroup.setAdapter(adapter);
    }

    private void toggleEditMode() {
        EditText first_name = findViewById(R.id.editText1); // first_name
        EditText last_name = findViewById(R.id.editTextLastName);
        EditText phone = findViewById(R.id.editText2); // phone
        EditText email = findViewById(R.id.editText3); // email
        EditText editTextUsername = findViewById(R.id.editTextUsername);
        Spinner spinnerGroup = findViewById(R.id.spinnerGroup);
        boolean isEnabled = first_name.isEnabled();

        first_name.setEnabled(!isEnabled);
        last_name.setEnabled(!isEnabled);
        phone.setEnabled(!isEnabled);
        email.setEnabled(!isEnabled);
        editTextUsername.setEnabled(!isEnabled);
        spinnerGroup.setEnabled(!isEnabled);
    }

    public void saveChanges(View view) {
        EditText first_name = findViewById(R.id.editText1);
        EditText last_name = findViewById(R.id.editTextLastName);
        EditText phone = findViewById(R.id.editText2);
        EditText emailAdd = findViewById(R.id.editText3);
        EditText editTextUsername = findViewById(R.id.editTextUsername);
        Spinner spinnerGroup = findViewById(R.id.spinnerGroup);

        String firstName = first_name.getText().toString();
        String lastName = last_name.getText().toString();
        String username = editTextUsername.getText().toString();
        String phoneNumber = phone.getText().toString();
        String email = emailAdd.getText().toString();
        String selectedGroup = spinnerGroup.getSelectedItem().toString();
        String passengerGroupId;

        if (selectedGroup.equals("Manila City Group")) {
            passengerGroupId = "2";
        } else if (selectedGroup.equals("Quezon City Group")) {
            passengerGroupId = "1";
        } else {
            // Default to 1 if group not recognized
            passengerGroupId = "1";
        }

        // Call API to update passenger details
        updatePassenger(firstName, lastName, username, phoneNumber, email, passengerGroupId);
    }

    private void updatePassenger(String firstName, String lastName, String username, String phoneNumber, String email, String passengerGroupId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Create JSON object with required parameters
                    JSONObject requestData = new JSONObject();
                    requestData.put("admin_username", "Arvi");
                    requestData.put("username", username);
                    requestData.put("first_name", firstName);
                    requestData.put("last_name", lastName);
                    requestData.put("mobile_number", phoneNumber);
                    requestData.put("email_add", email);
                    requestData.put("passenger_group_id", passengerGroupId);

                    // Establish connection to the API endpoint
                    URL url = new URL("https://c889-136-158-57-167.ngrok-free.app/api/user/updatePassengerByUserName");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    // Write JSON data to the connection output stream
                    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                    outputStream.writeBytes(requestData.toString());
                    outputStream.flush();
                    outputStream.close();

                    // Get response code
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Successfully updated passenger details
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(TestForPassengers.this, AdminPassengers.class);
                                startActivity(intent);
                                Toast.makeText(TestForPassengers.this, "Changes saved", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // Error handling for unsuccessful response
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(TestForPassengers.this, "Failed to update passenger details", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    // Close connection
                    connection.disconnect();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    // Handle exception
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(TestForPassengers.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
}
