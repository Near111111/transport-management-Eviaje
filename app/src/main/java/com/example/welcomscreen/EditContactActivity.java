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
import java.util.ArrayList;
import java.util.List;

public class EditContactActivity extends AppCompatActivity {
    private EditText editTextName;
    private EditText editTextPhoneNumber;
    private EditText editTextEmail;
    private EditText editTextAddress, editTextUsername;
    private Button buttonUpdate;
    private int contactIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test111);

        // Initialize views
        editTextName = findViewById(R.id.editText1);
        editTextPhoneNumber = findViewById(R.id.editText2);
        editTextEmail = findViewById(R.id.editText3);
        editTextAddress = findViewById(R.id.editText4);
//        editTextUsername = findViewById(R.id.spin);
        buttonUpdate = findViewById(R.id.saveButton);

        // Retrieve contact index from intent extras
        contactIndex = getIntent().getIntExtra("contactIndex", -1);
        if (contactIndex != -1) {
            List<AdminDriver.ContactCard> contactCardList = getIntent().getParcelableArrayListExtra("contactCardList");
            AdminDriver.ContactCard contact = contactCardList.get(contactIndex);

            // Populate EditText fields with current contact details
            editTextName.setText(contact.getName());
            editTextPhoneNumber.setText(contact.getPhoneNumber());
            editTextEmail.setText(contact.getEmail());
            editTextAddress.setText(contact.getAddress());
            editTextUsername.setText(contact.getUsername());

            // Set onClickListener for update button
            buttonUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Update contact details with new values
                    AdminDriver.ContactCard newContact = new AdminDriver.ContactCard(
                            editTextName.getText().toString(),
                            editTextPhoneNumber.getText().toString(),
                            editTextEmail.getText().toString(),
                            editTextAddress.getText().toString(),
                            editTextUsername.getText().toString()
                    );

                    // Update the contact in the list
                    contactCardList.set(contactIndex, newContact);

                    // Pass the updated contact list back to AdminDriver activity
                    Intent intent = new Intent();
                    intent.putParcelableArrayListExtra("updatedContactCardList", (ArrayList<AdminDriver.ContactCard>) contactCardList);
                    setResult(RESULT_OK, intent);

                    // Call API to update driver details
                    updateDriver(newContact, contact.getUsername());

                    // Finish the activity
                    finish();
                }
            });
        }
    }

    private void updateDriver(AdminDriver.ContactCard newContact, String username) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Create JSON object with required parameters
                    JSONObject requestData = new JSONObject();
                    requestData.put("admin_username", "Arvi");
                    requestData.put("username", username);
                    requestData.put("first_name", newContact.getName());
                    requestData.put("last_name", ""); // Assuming last name is not available
                    requestData.put("mobile_number", newContact.getPhoneNumber());
                    requestData.put("email_add", newContact.getEmail());
                    requestData.put("pickup_dropoff_id", 3); // Assuming pickup_dropoff_id is constant

                    // Establish connection to the API endpoint
                    URL url = new URL(ApiConfig.API_URL + "/api/user/updateDriverByDriverName");
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
                    } else {
                        // Error handling for unsuccessful response
                        // You can display an error message or take appropriate action
                        // For example:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(EditContactActivity.this, "Failed to update driver details", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    // Close connection
                    connection.disconnect();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    // Handle exception
                    // For example, display error message
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(EditContactActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
}