package com.example.welcomscreen;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
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
import java.util.Locale;

public class EmployeeHomeScreen extends AppCompatActivity {

    private String username;
    private TextView bulletinTextView;
    private TextView morningPickupTextView, holidayTextView;
    private TextView postWorkDropoffTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_home_screen);

        ImageButton livetrackingButton = findViewById(R.id.imageView18);
        ImageButton scheduleButton = findViewById(R.id.imageView15);
        ImageButton profileButton = findViewById(R.id.imageView19);
        holidayTextView = findViewById(R.id.textView20);
        Spinner spinner = findViewById(R.id.spinner2);

        String[] options = {"Opting Out", "Opting In"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra("username");
        }

        livetrackingButton.setOnClickListener(v -> startNewActivity(PassengerActivity.class));
        scheduleButton.setOnClickListener(v -> startNewActivity(EmployeeSchedule.class));
        profileButton.setOnClickListener(v -> startNewActivity(EmployeeProfile.class));

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(calendar.getTime());

        TextView textDate = findViewById(R.id.calendartoday);
        textDate.setText(currentDate);

        // Bind TextViews for morning pickup and post work dropoff
        morningPickupTextView = findViewById(R.id.morningpickup);
        postWorkDropoffTextView = findViewById(R.id.returntrip);

        // Call APIs for bulletin announcement and holiday
        callAPIs();
    }

    private void startNewActivity(Class<?> cls) {
        if (username != null) {
            Log.d("EmployeeHomeScreen", "Button clicked");
            Intent intent = new Intent(EmployeeHomeScreen.this, cls);
            intent.putExtra("username", username);
            startActivity(intent);
        } else {
            // Handle null username
        }
    }

    private void callAPIs() {
        new GetDataFromAPITask().execute();
    }

    private class GetDataFromAPITask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... voids) {
            String[] results = new String[2];

            // Get announcements from API
            String announcementApiUrl = "https://c889-136-158-57-167.ngrok-free.app/api/passenger/getBulletinAnnouncement";
            String announcementRequestBody = "{\"admin_username\": \"Arvi\", \"date\": \"2024-05-19\"}";
            results[0] = fetchDataFromAPI(announcementApiUrl, announcementRequestBody);

            // Get holidays from API
            String holidayApiUrl = "https://c889-136-158-57-167.ngrok-free.app/api/passenger/getBulletinHoliday";
            String holidayRequestBody = "{\"admin_username\": \"Arvi\"}";
            results[1] = fetchDataFromAPI(holidayApiUrl, holidayRequestBody);

            return results;
        }

        @Override
        protected void onPostExecute(String[] results) {
            if (results != null && results.length == 2) {
                handleAnnouncementResponse(results[0]);
                handleHolidayResponse(results[1]);
            } else {
                Toast.makeText(EmployeeHomeScreen.this, "Failed to fetch data from the API", Toast.LENGTH_SHORT).show();
            }
        }

        private String fetchDataFromAPI(String apiUrl, String requestBody) {
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

        private void handleAnnouncementResponse(String response) {
            // Handle announcement response here
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject apiResult = jsonObject.getJSONObject("api_result");
                int code = apiResult.getInt("code");

                if (code == 200) {
                    JSONObject data = apiResult.getJSONObject("data");
                    if (data.has("announcement")) {
                        String announcement = data.getString("announcement");
                        bulletinTextView = findViewById(R.id.bulletin);
                        bulletinTextView.setText(announcement);
                    } else {
                        Toast.makeText(EmployeeHomeScreen.this, "No announcement available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EmployeeHomeScreen.this, "Failed to fetch data from the API", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void handleHolidayResponse(String response) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject apiResult = jsonObject.getJSONObject("api_result");
                int code = apiResult.getInt("code");

                if (code == 200) {
                    JSONArray dataArray = apiResult.getJSONArray("data");
                    StringBuilder holidayMessages = new StringBuilder();

                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject data = dataArray.getJSONObject(i);
                        if (data.has("holiday") && data.has("announcement")) {
                            String holiday = data.getString("holiday");
                            String announcement = data.getString("announcement");
                            holidayMessages.append("Holiday: ").append(holiday).append("\n");
                            holidayMessages.append("Announcement: ").append(announcement).append("\n\n");
                        }
                    }

                    // Make sure to use the correct ID of the TextView for holiday
                    holidayTextView = findViewById(R.id.textView20); // Change textView20 to the correct ID
                    holidayTextView.setText(holidayMessages.toString());

                    if (holidayMessages.length() == 0) {
                        Toast.makeText(EmployeeHomeScreen.this, "No holiday announcement available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EmployeeHomeScreen.this, "Failed to fetch data from the API", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}