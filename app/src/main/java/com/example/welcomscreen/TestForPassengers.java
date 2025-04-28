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
import java.util.List;

public class TestForPassengers extends AppCompatActivity {

    Button buttonCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_for_passengers);

        ImageButton imageButton20 = findViewById(R.id.imageButton20);
        Button cancel = findViewById(R.id.saveButton2);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearEditTextFields();
                startActivity(new Intent(TestForPassengers.this, AdminPassengers.class));
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

        Spinner spinnerUsername = findViewById(R.id.spinnerUsername);
        spinnerUsername.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedUsername = (String) parentView.getItemAtPosition(position);
                getPassengerDetails(selectedUsername);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });

        // Fetch all passengers
        fetchAllPassengers();
    }

    private void fetchAllPassengers() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Create JSON object with required parameter
                    JSONObject requestData = new JSONObject();
                    requestData.put("admin_username", "Arvi");

                    // Establish connection to the API endpoint
                    URL url = new URL("https://c889-136-158-57-167.ngrok-free.app/api/user/getAllPassenger");
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
                        // Successfully fetched all passengers
                        // Parse response and get username
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
                            List<String> passengerUsernames = new ArrayList<>();
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject passengerData = dataArray.getJSONObject(i);
                                String username = passengerData.getString("username");
                                passengerUsernames.add(username);
                            }

                            // Update UI with fetched usernames
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    populateSpinner(passengerUsernames);
                                }
                            });
                        } else {
                            // API returned an error code
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(TestForPassengers.this, "Failed to fetch passengers: Error code " + resultCode, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        // Error handling for unsuccessful response
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(TestForPassengers.this, "Failed to fetch passengers", Toast.LENGTH_SHORT).show();
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

    private void populateSpinner(List<String> passengerUsernames) {
        Spinner spinnerUsername = findViewById(R.id.spinnerUsername);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, passengerUsernames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUsername.setAdapter(adapter);

        // After populating spinner, get details for the first username
        if (!passengerUsernames.isEmpty()) {
            String selectedUsername = passengerUsernames.get(0);
            getPassengerDetails(selectedUsername);
        }
    }

    private void getPassengerDetails(String username) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Create JSON object with required parameter
                    JSONObject requestData = new JSONObject();
                    requestData.put("username", username);

                    // Establish connection to the API endpoint
                    URL url = new URL("https://c889-136-158-57-167.ngrok-free.app/api/user/getPassengerByUserName");
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
                        // Successfully fetched passenger details
                        // Parse response and update UI with details
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
                            JSONArray dataArray = apiResult.getJSONArray("data");
                            JSONObject passengerData = dataArray.getJSONObject(0);
                            String firstName = passengerData.getString("passenger_name");
                            String phoneNumber = String.valueOf(passengerData.getLong("mobile_number"));
                            String email = passengerData.getString("email_add");

                            // Update UI with fetched details
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    EditText first_name = findViewById(R.id.editText1);
                                    EditText phone = findViewById(R.id.editText2);
                                    EditText emailAdd = findViewById(R.id.editText3);

                                    first_name.setText(firstName);
                                    phone.setText(phoneNumber);
                                    emailAdd.setText(email);
                                }
                            });
                        } else {
                            // API returned an error code
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(TestForPassengers.this, "Failed to fetch passenger details: Error code " + resultCode, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        // Error handling for unsuccessful response
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(TestForPassengers.this, "Failed to fetch passenger details", Toast.LENGTH_SHORT).show();
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

    private void toggleEditMode() {
        EditText first_name = findViewById(R.id.editText1);
        EditText last_name = findViewById(R.id.editTextLastName);
        EditText phone = findViewById(R.id.editText2);
        EditText email = findViewById(R.id.editText3);
        Spinner editTextUsername = findViewById(R.id.spinnerUsername);
        boolean isEnabled = first_name.isEnabled();

        first_name.setEnabled(!isEnabled);
        last_name.setEnabled(!isEnabled);
        phone.setEnabled(!isEnabled);
        email.setEnabled(!isEnabled);
        editTextUsername.setEnabled(!isEnabled);
    }

    private void clearEditTextFields() {
        EditText first_name = findViewById(R.id.editText1);
        EditText last_name = findViewById(R.id.editTextLastName);
        EditText phone = findViewById(R.id.editText2);
        EditText email = findViewById(R.id.editText3);

        last_name.setText("");
        first_name.setText("");
        phone.setText("");
        email.setText("");
    }

    public void saveChanges(View view) {
        EditText first_name = findViewById(R.id.editText1);
        EditText phone = findViewById(R.id.editText2);
        EditText last_name = findViewById(R.id.editTextLastName);
        EditText emailAdd = findViewById(R.id.editText3);
        Spinner editTextUsername = findViewById(R.id.spinnerUsername);
        Spinner passengerGroup = findViewById(R.id.spinnerGroup);

        String firstName = first_name.getText().toString();
        String lastName = last_name.getText().toString();
        String phoneNumber = phone.getText().toString();
        String email = emailAdd.getText().toString();
        String username = editTextUsername.getSelectedItem().toString();
        String passenger_group = getPassengerGroupValue(passengerGroup.getSelectedItem().toString());

        // Call API to update passenger details
        updatePassenger(firstName, lastName, phoneNumber, email, username, passenger_group);
    }

    private String getPassengerGroupValue(String group) {
        // Return 2 for Manila City Group and 1 for Quezon City Group
        return group.equals("Manila City Group") ? "2" : "1";
    }

    private void updatePassenger(String firstName, String lastName, String phoneNumber, String email, String username, String passengerGroup) {
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
                    requestData.put("passenger_group_id", passengerGroup);

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
