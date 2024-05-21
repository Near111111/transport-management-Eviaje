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

public class DriverProfile extends AppCompatActivity {

    private String username;

    private ImageButton homeButton, livetrackingButton, scheduleButton, logoutButton;
    private TextView nameTextView, mobileTextView, emailTextView, addressTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);
        Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra("username");
        }

        initializeViews();

        // Assuming the username is passed through Intent, adjust accordingly if not
        if (intent != null) {
            String username = intent.getStringExtra("username");
            if (username != null) {
                Log.d("DriverProfile", "Username received: " + username);
                new GetDriverDetailsTask().execute(username);
            } else {
                Log.e("DriverProfile", "Username is null");
            }
        } else {
            Log.e("DriverProfile", "Intent is null");
        }

        livetrackingButton.setOnClickListener(v -> {
            Intent trackingIntent = new Intent(DriverProfile.this, MapsActivity.class);
            startActivity(trackingIntent);
        });

        scheduleButton.setOnClickListener(v -> {
            if (username != null) {
                // Ilagay ang breakpoint sa linya sa ibaba
                Log.d("DriverHomeScreen", "profileButton clicked"); // Pwedeng lagyan ng log message para sa debugging
                Intent profileIntent = new Intent(DriverProfile.this, DriverNewSchedule.class);
                profileIntent.putExtra("username", username);
                startActivity(profileIntent);
            } else {
                // Kung ang username ay null, gawin ang mga naaangkop na hakbang dito, tulad ng pagpapakita ng mensahe ng error o iba pa.
            }
        });

        homeButton.setOnClickListener(v -> {

            if (username != null) {
                // Ilagay ang breakpoint sa linya sa ibaba
                Log.d("DriverHomeScreen", "profileButton clicked"); // Pwedeng lagyan ng log message para sa debugging
                Intent profileIntent = new Intent(DriverProfile.this, DrivenHomeScreen.class);
                profileIntent.putExtra("username", username);
                startActivity(profileIntent);
            } else {
                // Kung ang username ay null, gawin ang mga naaangkop na hakbang dito, tulad ng pagpapakita ng mensahe ng error o iba pa.
            }
        });

        logoutButton.setOnClickListener(v -> {
            Intent logoutIntent = new Intent(DriverProfile.this, MainActivity3.class);
            startActivity(logoutIntent);
            finish();
        });
    }

    private void initializeViews() {
        homeButton = findViewById(R.id.imageView14);
        livetrackingButton = findViewById(R.id.imageView18);
        scheduleButton = findViewById(R.id.imageView15);
        logoutButton = findViewById(R.id.imageButton21);
        nameTextView = findViewById(R.id.textView1);
        mobileTextView = findViewById(R.id.textView2);
        emailTextView = findViewById(R.id.textView3);
        addressTextView = findViewById(R.id.textView4);
    }

    private class GetDriverDetailsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String apiUrl = "https://c889-136-158-57-167.ngrok-free.app/api/user/getDriverByUserName";

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
                    Log.e("GetDriverDetailsTask", "Response code: " + responseCode);
                    return null;
                }
            } catch (IOException | JSONException e) {
                Log.e("GetDriverDetailsTask", "Exception: " + e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    Log.d("GetDriverDetailsTask", "API Response: " + result);
                    JSONObject jsonResponse = new JSONObject(result);
                    JSONObject apiResult = jsonResponse.getJSONObject("api_result");
                    int code = apiResult.getInt("code");
                    if (code == 200) {
                        JSONArray data = apiResult.getJSONArray("data");
                        if (data.length() > 0) {
                            JSONObject driver = data.getJSONObject(0);
                            String driverName = driver.getString("driver_name");
                            String mobileNumber = driver.getString("mobile_number");
                            String emailAddress = driver.getString("email_add");
                            String address = driver.getString("address");

                            nameTextView.setText(driverName);
                            mobileTextView.setText(mobileNumber);
                            emailTextView.setText(emailAddress);
                            addressTextView.setText(address);
                        } else {
                            Log.e("GetDriverDetailsTask", "No data found in response");
                        }
                    } else {
                        Log.e("APIError", "Code: " + code);
                    }
                } catch (JSONException e) {
                    Log.e("GetDriverDetailsTask", "JSON Exception: " + e.getMessage(), e);
                }
            } else {
                Log.e("APIError", "Response is null");
            }
        }
    }
}