package com.example.welcomscreen;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class EmployeeProfile extends AppCompatActivity {

    private ImageButton homeButton, livetrackingButton, scheduleButton, logoutButton;
    private TextView nameTextView, mobileTextView, emailTextView, addressTextView, groupTextView;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_profile);

        initializeViews();

        Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra("username");
            if (username != null) {
                Log.d("EmployeeProfile", "Username received: " + username);
                new GetUserDetailsTask().execute(username);
            } else {
                Log.e("EmployeeProfile", "Username is null");
            }
        } else {
            Log.e("EmployeeProfile", "Intent is null");
        }

        homeButton.setOnClickListener(v -> {
            if (username != null) {
                // Ilagay ang breakpoint sa linya sa ibaba
                Log.d("EmployeeHomeScreen", "profileButton clicked"); // Pwedeng lagyan ng log message para sa debugging
                Intent profileIntent = new Intent(EmployeeProfile.this, EmployeeHomeScreen.class);
                profileIntent.putExtra("username", username);
                startActivity(profileIntent);
            } else {
                // Kung ang username ay null, gawin ang mga naaangkop na hakbang dito, tulad ng pagpapakita ng mensahe ng error o iba pa.
            }
        });

        livetrackingButton.setOnClickListener(v -> {
            Intent trackingIntent = new Intent(EmployeeProfile.this, PassengerActivity.class);
            startActivity(trackingIntent);
        });

        scheduleButton.setOnClickListener(v -> {
            if (username != null) {
                // Ilagay ang breakpoint sa linya sa ibaba
                Log.d("EmployeeHomeScreen", "profileButton clicked"); // Pwedeng lagyan ng log message para sa debugging
                Intent profileIntent = new Intent(EmployeeProfile.this, EmployeeSchedule.class);
                profileIntent.putExtra("username", username);
                startActivity(profileIntent);
            } else {
                // Kung ang username ay null, gawin ang mga naaangkop na hakbang dito, tulad ng pagpapakita ng mensahe ng error o iba pa.
            }
        });

        logoutButton.setOnClickListener(v -> {
            Intent logoutIntent = new Intent(EmployeeProfile.this, MainActivity3.class);
            startActivity(logoutIntent);
            finish();
        });
    }

    private void initializeViews() {
        homeButton = findViewById(R.id.imageView14);
        livetrackingButton = findViewById(R.id.imageView18);
        scheduleButton = findViewById(R.id.imageView15);
        logoutButton = findViewById(R.id.imageButton21);
        nameTextView = findViewById(R.id.nameTextView);
        mobileTextView = findViewById(R.id.mobileTextView);
        emailTextView = findViewById(R.id.emailTextView);
        addressTextView = findViewById(R.id.addressTextView);
        groupTextView = findViewById(R.id.groupTextView);
    }

    private class GetUserDetailsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String apiUrl = "https://c889-136-158-57-167.ngrok-free.app/api/user/getPassengerByUserName";

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);

                JSONObject requestBody = new JSONObject();
                requestBody.put("username", username);

                urlConnection.getOutputStream().write(requestBody.toString().getBytes());

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = urlConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        response.append(line);
                    }
                    bufferedReader.close();
                    return response.toString();
                } else {
                    Log.e("GetUserDetailsTask", "Response code: " + responseCode);
                    return null;
                }
            } catch (IOException | JSONException e) {
                Log.e("GetUserDetailsTask", "Exception: " + e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    Log.d("GetUserDetailsTask", "API Response: " + result);
                    JSONObject jsonResponse = new JSONObject(result);
                    JSONObject apiResult = jsonResponse.getJSONObject("api_result");
                    int code = apiResult.getInt("code");
                    if (code == 200) {
                        JSONArray data = apiResult.getJSONArray("data");
                        if (data.length() > 0) {
                            JSONObject passenger = data.getJSONObject(0);
                            String passengerName = passenger.getString("passenger_name");
                            String mobileNumber = passenger.getString("mobile_number");
                            String emailAddress = passenger.getString("email_add");
                            String address = passenger.getString("address");
                            String passengerGroup = passenger.getString("passenger_group");

                            // Set toast message in UI thread

                            nameTextView.setText(passengerName);
                            mobileTextView.setText(mobileNumber);
                            emailTextView.setText(emailAddress);
                            addressTextView.setText(address);
                            groupTextView.setText(passengerGroup);

                        } else {
                            Log.e("GetUserDetailsTask", "No data found in response");
                        }
                    } else {
                        Log.e("APIError", "Code: " + code);
                    }
                } catch (JSONException e) {
                    Log.e("GetUserDetailsTask", "JSON Exception: " + e.getMessage(), e);
                }
            } else {
                Log.e("APIError", "Response is null");
            }
        }
    }
}
