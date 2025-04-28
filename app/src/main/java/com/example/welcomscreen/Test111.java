package com.example.welcomscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Test111 extends AppCompatActivity {
    private ArrayList<String> driverUsernames;
    private Spinner spinnerUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test111);

        ImageButton imageButton20 = findViewById(R.id.imageButton20);
        Button cancel = findViewById(R.id.saveButton2);
        spinnerUsername = findViewById(R.id.spinner_driver);
        clearEditTextFields();
        cancel.setOnClickListener(v -> startActivity(new Intent(Test111.this, AdminDriver.class)));

        imageButton20.setOnClickListener(v -> toggleEditMode());

        // Initialize spinner with default item
        driverUsernames = new ArrayList<>();
        driverUsernames.add("List of Driver");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, driverUsernames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUsername.setAdapter(adapter);

        // Fetch list of driver usernames from API
        fetchDriverUsernames();

        // Listen for item selection events on the spinner
        spinnerUsername.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedUsername = parent.getItemAtPosition(position).toString();
                if (position == 0) {
                    clearEditTextFields();
                } else {
                    // Call API to get driver details by username
                    getDriverDetailsByUsername(selectedUsername);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void fetchDriverUsernames() {
        new Thread(() -> {
            try {
                // Create JSON object with required parameters
                JSONObject requestData = new JSONObject();
                requestData.put("admin_username", "Arvi");

                // Establish connection to the API endpoint
                URL url = new URL("https://c889-136-158-57-167.ngrok-free.app/api/user/getAllDriver");
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
                    // Successfully retrieved driver usernames
                    // Parse response and update spinner
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray driverData = jsonResponse.getJSONObject("api_result").getJSONArray("data");
                    for (int i = 0; i < driverData.length(); i++) {
                        JSONObject driver = driverData.getJSONObject(i);
                        String username = driver.getString("username");
                        driverUsernames.add(username);
                    }

                    // Update spinner UI
                    runOnUiThread(() -> {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(Test111.this, android.R.layout.simple_spinner_item, driverUsernames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerUsername.setAdapter(adapter);
                    });
                } else {
                    // Error handling for unsuccessful response
                    // Print response code and message for debugging
                    System.out.println("Response Code: " + responseCode);
                    System.out.println("Response Message: " + connection.getResponseMessage());

                    runOnUiThread(() -> Toast.makeText(Test111.this, "Failed to fetch driver usernames", Toast.LENGTH_SHORT).show());
                }
                // Close connection
                connection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                // Handle exception
                runOnUiThread(() -> Toast.makeText(Test111.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void clearEditTextFields() {
        EditText editText1 = findViewById(R.id.editText1);
        EditText editText2 = findViewById(R.id.editText2);
        EditText editText3 = findViewById(R.id.editText3);
        EditText editText4 = findViewById(R.id.editText4);

        editText1.setText("");
        editText2.setText("");
        editText3.setText("");
        editText4.setText("");
    }

    public void saveChanges(View view) {
        EditText editText1 = findViewById(R.id.editText1);
        Spinner editTextUsername = findViewById(R.id.spinner_driver);
        EditText editText2 = findViewById(R.id.editText2);
        EditText editText3 = findViewById(R.id.editText3);
        EditText editText4 = findViewById(R.id.editText4);

        String firstName = editText1.getText().toString();
        String lastName = editText4.getText().toString();
        String username = editTextUsername.getSelectedItem().toString();
        String phoneNumber = editText2.getText().toString();
        String email = editText3.getText().toString();
        // Assuming address is not used here

        // Call API to update driver details
        updateDriver(firstName, lastName, username, phoneNumber, email);
    }

    private void updateDriver(String firstName, String lastName, String username, String phoneNumber, String email) {
        new Thread(() -> {
            try {
                // Create JSON object with required parameters
                JSONObject requestData = new JSONObject();
                requestData.put("admin_username", "Arvi");
                requestData.put("username", username);
                requestData.put("first_name", firstName);
                requestData.put("last_name", lastName);
                requestData.put("mobile_number", phoneNumber);
                requestData.put("email_add", email);
                requestData.put("pickup_dropoff_id", 3); // Assuming pickup_dropoff_id is constant

                // Establish connection to the API endpoint
                URL url = new URL("https://c889-136-158-57-167.ngrok-free.app/api/user/updateDriverByDriverName");
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
                    // Successfully updated driver details
                    // You can handle the response as needed
                    // For example, parse the response JSON if any
                    runOnUiThread(() -> {
                        // Display a Toast message to indicate that changes are saved
                        Intent intent = new Intent(Test111.this, AdminDriver.class);
                        startActivity(intent);
                        Toast.makeText(Test111.this, "Changes saved", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    // Error handling for unsuccessful response
                    // Print response code and message for debugging
                    System.out.println("Response Code: " + responseCode);
                    System.out.println("Response Message: " + connection.getResponseMessage());

                    runOnUiThread(() -> Toast.makeText(Test111.this, "Failed to update driver details", Toast.LENGTH_SHORT).show());
                }
                // Close connection
                connection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                // Handle exception
                runOnUiThread(() -> Toast.makeText(Test111.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void displayContactDetails(AdminDriver.ContactCard contact) {
        EditText editText1 = findViewById(R.id.editText1);
        EditText editText2 = findViewById(R.id.editText2);
        EditText editText3 = findViewById(R.id.editText3);
        EditText editText4 = findViewById(R.id.editText4);

        editText1.setText(contact.getName());
        editText2.setText(contact.getPhoneNumber());
        editText3.setText(contact.getEmail());
        editText4.setText(contact.getName());
    }

    private void toggleEditMode() {
        EditText editText1 = findViewById(R.id.editText1);
        EditText editText2 = findViewById(R.id.editText2);
        EditText editText3 = findViewById(R.id.editText3);
        EditText editText4 = findViewById(R.id.editText4);

        boolean isEnabled = editText1.isEnabled();

        editText1.setEnabled(!isEnabled);
        editText2.setEnabled(!isEnabled);
        editText3.setEnabled(!isEnabled);
        editText4.setEnabled(!isEnabled);
    }

    private void getDriverDetailsByUsername(String username) {
        new Thread(() -> {
            try {
                // Create JSON object with required parameter
                JSONObject requestData = new JSONObject();
                requestData.put("username", username);

                // Establish connection to the API endpoint
                URL url = new URL("https://c889-136-158-57-167.ngrok-free.app/api/user/getDriverByUserName");
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
                String responseMessage = connection.getResponseMessage();
                System.out.println("Response Code: " + responseCode);
                System.out.println("Response Message: " + responseMessage);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Successfully retrieved driver details
                    // Parse response and update UI
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    System.out.println("Response Content: " + response.toString());

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONObject apiResult = jsonResponse.getJSONObject("api_result");
                    int resultCode = apiResult.getInt("code");

                    if (resultCode == 200) {
                        // Extract data array
                        JSONArray dataArray = apiResult.getJSONArray("data");
                        if (dataArray.length() > 0) {
                            // Get the first object in the array (assuming there's only one)
                            JSONObject driverData = dataArray.getJSONObject(0);
                            String driverFirstName = driverData.getString("driver_name");
                            String driverPhoneNumber = driverData.getString("mobile_number");
                            String driverEmail = driverData.getString("email_add");
                            String driverAddress = driverData.getString("address");

                            // Update UI with retrieved driver details
                            runOnUiThread(() -> {
                                EditText editText1 = findViewById(R.id.editText1);
                                EditText editText2 = findViewById(R.id.editText2);
                                EditText editText3 = findViewById(R.id.editText3);
                                EditText editText4 = findViewById(R.id.editText4);

                                editText1.setText(driverFirstName);
                                editText2.setText(driverPhoneNumber);
                                editText3.setText(driverEmail);
                                editText4.setText(driverAddress);
                            });
                        } else {
                            // No data available
                            runOnUiThread(() -> Toast.makeText(Test111.this, "No driver data available", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        // API returned an error code
                        runOnUiThread(() -> Toast.makeText(Test111.this, "Failed to fetch driver details: Error code " + resultCode, Toast.LENGTH_SHORT).show());
                    }
                } else {
                    // Error handling for unsuccessful response
                    // Print response code and message for debugging
                    System.out.println("Response Code: " + responseCode);
                    System.out.println("Response Message: " + responseMessage);

                    runOnUiThread(() -> Toast.makeText(Test111.this, "Failed to fetch driver details", Toast.LENGTH_SHORT).show());
                }
                // Close connection
                connection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                // Handle exception
                runOnUiThread(() -> Toast.makeText(Test111.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}