package com.example.welcomscreen;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DrivenHomeScreen extends AppCompatActivity {

    private String username;
    private int userId;
    private String currentDate;

    private TextView bulletinTextView;
    private TextView morningPickupTextView, holidayTextView, postWorkDropoffTextView;
    private TextView tomorrowMorningPickUpTextView, tomorrowReturnTripTextView;
    private TextView dispatchMorningTextView, dispatchReturnTextView;

    private final Handler handler = new Handler();
    private Runnable morningRunnable, returnRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driven_home_screen);

        ImageButton livetrackingButton = findViewById(R.id.imageView18);
        ImageButton scheduleButton = findViewById(R.id.imageView15);
        ImageButton profileButton = findViewById(R.id.imageView19);

        holidayTextView = findViewById(R.id.textView20);
        dispatchMorningTextView = findViewById(R.id.textView34);
        dispatchReturnTextView = findViewById(R.id.textView35);

        Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra("username");
            userId = intent.getIntExtra("userId", -1);
        }

        livetrackingButton.setOnClickListener(v -> startNewActivity(MapsActivity.class));
        scheduleButton.setOnClickListener(v -> startNewActivity(DriverNewSchedule.class));
        profileButton.setOnClickListener(v -> startNewActivity(DriverProfile.class));

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        currentDate = sdf.format(calendar.getTime());

        TextView textDate = findViewById(R.id.calendartoday);
        textDate.setText(currentDate);

        morningPickupTextView = findViewById(R.id.morningpickup);
        postWorkDropoffTextView = findViewById(R.id.returntrip);
        tomorrowMorningPickUpTextView = findViewById(R.id.tommorrowMorningPickUp);
        tomorrowReturnTripTextView = findViewById(R.id.tommorrowReturnTrip);

        callAPIs();
    }

    private void startNewActivity(Class<?> cls) {
        if (username != null) {
            Intent intent = new Intent(DrivenHomeScreen.this, cls);
            intent.putExtra("username", username);
            startActivity(intent);
        }
    }

    private void callAPIs() {
        new GetDataFromAPITask().execute();
    }

    private class GetDataFromAPITask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... voids) {
            String[] results = new String[3];

            String announcementApiUrl = ApiConfig.API_URL + "/api/passenger/getBulletinAnnouncement";
            String announcementRequestBody = "{\"admin_username\": \"Arvi\", \"date\": \"2024-05-19\"}";
            results[0] = fetchDataFromAPI(announcementApiUrl, announcementRequestBody);

            String holidayApiUrl = ApiConfig.API_URL + "/api/passenger/getBulletinHoliday";
            String holidayRequestBody = "{\"admin_username\": \"Arvi\"}";
            results[1] = fetchDataFromAPI(holidayApiUrl, holidayRequestBody);

            String scheduleApiUrl = ApiConfig.API_URL + "/api/passenger/getScheduleByPassenger";
            int testPassengerId = (userId == -1) ? 66 : userId;
            String scheduleRequestBody = String.format(Locale.getDefault(),
                    "{\"admin_username\": \"ernani.viaje\", \"passenger_id\": %d, \"date\": \"%s\"}",
                    testPassengerId, currentDate);
            results[2] = fetchDataFromAPI(scheduleApiUrl, scheduleRequestBody);

            return results;
        }

        @Override
        protected void onPostExecute(String[] results) {
            if (results != null && results.length == 3) {
                handleAnnouncementResponse(results[0]);
                handleHolidayResponse(results[1]);
                handleScheduleResponse(results[2]);
            } else {
                Toast.makeText(DrivenHomeScreen.this, "Failed to fetch data from the API", Toast.LENGTH_SHORT).show();
            }
        }

        private String fetchDataFromAPI(String apiUrl, String requestBody) {
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);

                OutputStream outputStream = urlConnection.getOutputStream();
                outputStream.write(requestBody.getBytes());
                outputStream.flush();
                outputStream.close();

                int responseCode = urlConnection.getResponseCode();

                InputStream inputStream = (responseCode >= 200 && responseCode < 300)
                        ? urlConnection.getInputStream()
                        : urlConnection.getErrorStream();

                if (inputStream == null) return null;

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) response.append(line);
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
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject apiResult = jsonObject.getJSONObject("api_result");
                if (apiResult.getInt("code") == 200) {
                    String announcement = apiResult.getJSONObject("data").getString("announcement");
                    bulletinTextView = findViewById(R.id.bulletin);
                    bulletinTextView.setText(announcement);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void handleHolidayResponse(String response) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject apiResult = jsonObject.getJSONObject("api_result");
                if (apiResult.getInt("code") == 200) {
                    JSONArray dataArray = apiResult.getJSONArray("data");
                    StringBuilder holidayMessages = new StringBuilder();
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject data = dataArray.getJSONObject(i);
                        holidayMessages.append("Holiday: ")
                                .append(data.getString("holiday"))
                                .append("\nAnnouncement: ")
                                .append(data.getString("announcement")).append("\n\n");
                    }
                    holidayTextView.setText(holidayMessages.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void handleScheduleResponse(String response) {
            if (response == null || response.trim().isEmpty()) return;

            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject apiResult = jsonObject.getJSONObject("api_result");
                if (apiResult.getInt("code") == 200) {
                    JSONObject data = apiResult.getJSONObject("data");

                    if (data.has("todaySchedule")) {
                        JSONObject today = data.getJSONObject("todaySchedule");

                        String morningPickup = today.optString("morning_pickup", "Not available");
                        String returnTrip = today.optString("post_work_dropoff", "Not available");

                        morningPickupTextView.setText(morningPickup);
                        postWorkDropoffTextView.setText(returnTrip);

                        startCountdown(morningPickup, dispatchMorningTextView);
                        startCountdown(returnTrip, dispatchReturnTextView);
                    }
                    if (data.has("nextDaySchedule")) {
                        JSONObject next = data.getJSONObject("nextDaySchedule");
                        tomorrowMorningPickUpTextView.setText(next.optString("morning_pickup", "Not available"));
                        tomorrowReturnTripTextView.setText(next.optString("post_work_dropoff", "Not available"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void startCountdown(String timeStr, TextView displayView) {
        if (timeStr == null || timeStr.equals("Not available")) {
            displayView.setText("Not scheduled");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            Date scheduleTime = sdf.parse(timeStr);
            Calendar now = Calendar.getInstance();
            Calendar scheduled = Calendar.getInstance();
            scheduled.setTime(scheduleTime);
            scheduled.set(Calendar.YEAR, now.get(Calendar.YEAR));
            scheduled.set(Calendar.MONTH, now.get(Calendar.MONTH));
            scheduled.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));

            Runnable countdownRunnable = new Runnable() {
                @Override
                public void run() {
                    long millisLeft = scheduled.getTimeInMillis() - System.currentTimeMillis();
                    if (millisLeft > 0) {
                        long hours = millisLeft / (1000 * 60 * 60);
                        long minutes = (millisLeft / (1000 * 60)) % 60;
                        displayView.setText("Dispatching in " + hours + "h " + minutes + "m");
                        handler.postDelayed(this, 1000);
                    } else {
                        displayView.setText("Completed");
                    }
                }
            };

            handler.post(countdownRunnable);

        } catch (ParseException e) {
            displayView.setText("Invalid time");
        }
    }
}
