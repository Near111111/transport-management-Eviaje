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
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Test111 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test111);

        ImageButton imageButton20 = findViewById(R.id.imageButton20);
        Button cancel =  findViewById(R.id.saveButton2);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText1 = findViewById(R.id.editText1);
                EditText editTextUsername = findViewById(R.id.editTextUsername);
                EditText editText2 = findViewById(R.id.editText2);
                EditText editText3 = findViewById(R.id.editText3);
                EditText editText4 = findViewById(R.id.editText4);

                editText1.setText("");
                editTextUsername.setText("");
                editText2.setText("");
                editText3.setText("");
                editText4.setText("");
            }
        });
        imageButton20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEditMode();
            }
        });
    }

    private void toggleEditMode() {
        EditText editText1 = findViewById(R.id.editText1);
        EditText editTextUsername = findViewById(R.id.editTextUsername);
        EditText editText2 = findViewById(R.id.editText2);
        EditText editText3 = findViewById(R.id.editText3);
        EditText editText4 = findViewById(R.id.editText4);

        boolean isEnabled = editText1.isEnabled();

        editText1.setEnabled(!isEnabled);
        editTextUsername.setEnabled(!isEnabled);
        editText2.setEnabled(!isEnabled);
        editText3.setEnabled(!isEnabled);
        editText4.setEnabled(!isEnabled);
    }

    public void saveChanges(View view) {
        EditText editText1 = findViewById(R.id.editText1);
        EditText editTextUsername = findViewById(R.id.editTextUsername);
        EditText editText2 = findViewById(R.id.editText2);
        EditText editText3 = findViewById(R.id.editText3);
        EditText editText4 = findViewById(R.id.editText4);

        String firstName = editText1.getText().toString();
        String lastName = editText4.getText().toString();
        String username = editTextUsername.getText().toString();
        String phoneNumber = editText2.getText().toString();
        String email = editText3.getText().toString();
        String address = editText4.getText().toString();

        // Call API to update driver details
        updateDriver(firstName, lastName, username, phoneNumber, email, address);
    }

    private void updateDriver(String firstName, String lastName, String username, String phoneNumber, String email, String address) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Create JSON object with required parameters
                    JSONObject requestData = new JSONObject();
                    requestData.put("admin_username", "Arvi");
                    requestData.put("username", username);
                    requestData.put("first_name", firstName);
                    requestData.put("last_name", lastName); // Assuming last name is not available
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Display a Toast message to indicate that changes are saved
                                Intent intent = new Intent(Test111.this, AdminDriver.class);
                                startActivity(intent);
                                Toast.makeText(Test111.this, "Changes saved", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // Error handling for unsuccessful response
                        // Print response code and message for debugging
                        System.out.println("Response Code: " + responseCode);
                        System.out.println("Response Message: " + connection.getResponseMessage());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Test111.this, "Failed to update driver details", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Test111.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
}
