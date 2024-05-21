package com.example.welcomscreen;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;

public class EmployeeHomeScreen extends AppCompatActivity {

    private String username;
    private TextView bulletinTextView;
    private TextView morningPickupTextView;
    private TextView postWorkDropoffTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_home_screen);

        ImageButton livetrackingButton = findViewById(R.id.imageView18);
        ImageButton scheduleButton = findViewById(R.id.imageView15);
        ImageButton profileButton = findViewById(R.id.imageView19);
        Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra("username");
        }
        livetrackingButton.setOnClickListener(v -> {
            if (username != null) {
                Log.d("EmployeeHomeScreen", "profileButton clicked");
                Intent profileIntent = new Intent(EmployeeHomeScreen.this, PassengerActivity.class);
                profileIntent.putExtra("username", username);
                startActivity(profileIntent);
            } else {
                // Handle null username
            }
        });

        scheduleButton.setOnClickListener(v -> {
            if (username != null) {
                Log.d("EmployeeHomeScreen", "profileButton clicked");
                Intent profileIntent = new Intent(EmployeeHomeScreen.this, EmployeeSchedule.class);
                profileIntent.putExtra("username", username);
                startActivity(profileIntent);
            } else {
                // Handle null username
            }
        });

        profileButton.setOnClickListener(v -> {
            if (username != null) {
                Log.d("EmployeeHomeScreen", "profileButton clicked");
                Intent profileIntent = new Intent(EmployeeHomeScreen.this, EmployeeProfile.class);
                profileIntent.putExtra("username", username);
                startActivity(profileIntent);
            } else {
                // Handle null username
            }
        });

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(calendar.getTime());

        TextView textDate = findViewById(R.id.calendartoday);
        textDate.setText(currentDate);

        // Bind TextViews for morning pickup and post work dropoff
        morningPickupTextView = findViewById(R.id.morningpickup);
        postWorkDropoffTextView = findViewById(R.id.returntrip);

        String announcementApiUrl = "https://c889-136-158-57-167.ngrok-free.app/api/passenger/getBulletinAnnouncement";
        String announcementRequestBody = "{\"admin_username\":\"Arvi\",\"date\":\"" + currentDate + "\"}";

        new GetDataFromAPITask().execute(announcementApiUrl, announcementRequestBody, "announcement");

        String passengerApiUrl = "https://c889-136-158-57-167.ngrok-free.app/api/passenger/getPassengerList";
        String passengerRequestBody = "{\"admin_username\":\"Arvi\",\"date\":\"" + currentDate + "\"}";
        new GetPassengerListTask().execute(passengerApiUrl, passengerRequestBody);
    }

    private class GetDataFromAPITask extends AsyncTask<String, Void, String> {
        String dataType;

        @Override
        protected String doInBackground(String... params) {
            String apiUrl = params[0];
            String requestBody = params[1];
            dataType = params[2];

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);

                OutputStream outputStream = urlConnection.getOutputStream();
                outputStream.write(requestBody.getBytes());
                outputStream.flush();
                outputStream.close();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                inputStream.close();
                urlConnection.disconnect();

                return response.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                handleApiResponse(result, dataType);
            } else {
                Toast.makeText(EmployeeHomeScreen.this, "Failed to fetch data from the API", Toast.LENGTH_SHORT).show();
            }
        }

        private void handleApiResponse(String response, String dataType) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject apiResult = jsonObject.getJSONObject("api_result");
                int code = apiResult.getInt("code");

                if (code == 200) {
                    if (dataType.equals("announcement")) {
                        JSONObject data = apiResult.getJSONObject("data");
                        if (data.has("announcement")) {
                            String announcement = data.getString("announcement");
                            bulletinTextView = findViewById(R.id.bulletin);
                            bulletinTextView.setText(announcement);
                        } else {
                            Toast.makeText(EmployeeHomeScreen.this, "No announcement available", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(EmployeeHomeScreen.this, "Failed to fetch data from the API", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class GetPassengerListTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String apiUrl = params[0];
            String requestBody = params[1];

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);

                OutputStream outputStream = urlConnection.getOutputStream();
                outputStream.write(requestBody.getBytes());
                outputStream.flush();
                outputStream.close();

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    return response.toString();
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("GetPassengerListTask", "Result: " + result);
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject data = jsonObject.getJSONObject("api_result").getJSONObject("data");
                    // Iterate through each location and passenger to find the morningPickup and postWorkDropoff
                    for (Iterator<String> it = data.keys(); it.hasNext();) {
                        String location = it.next();
                        JSONObject locationData = data.getJSONObject(location);
                        for (Iterator<String> locIt = locationData.keys(); locIt.hasNext();) {
                            String subLocation = locIt.next();
                            JSONArray passengers = locationData.getJSONArray(subLocation);
                            if (passengers.length() > 0) {
                                JSONObject firstPassenger = passengers.getJSONObject(0);
                                String morningPickup = firstPassenger.getString("morningPickup");
                                String postWorkDropoff = firstPassenger.getString("postWorkDropoff");
                                Log.d("GetPassengerListTask", "SubLocation: " + subLocation + " Morning Pickup: " + morningPickup + " Post Work Dropoff: " + postWorkDropoff);

                                // Update the TextViews with the morningPickup and postWorkDropoff times
                                runOnUiThread(() -> {
                                    morningPickupTextView.setText(morningPickup);
                                    postWorkDropoffTextView.setText(postWorkDropoff);
                                });
                                return;
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(EmployeeHomeScreen.this, "Failed to fetch passenger data from the API", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
