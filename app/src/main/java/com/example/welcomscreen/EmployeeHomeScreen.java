// package and imports
package com.example.welcomscreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EmployeeHomeScreen extends AppCompatActivity {

    private String username;
    private TextView bulletinTextView;
    private TextView todayMorningPickupTextView, tomorrowMorningPickupTextView, holidayTextView;
    private TextView todayReturnTripTextView, tomorrowReturnTripTextView;
    private Spinner morningpickup_spinner, morningpickup_spinner2, morningpickup_spinner3, morningpickup_spinner4;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private int todayScheduleId = 342;
    private int tomorrowScheduleId = 77;
    private String todayStatus = "";
    private String tomorrowStatus = "";
    private String todayMorningPickupTime = "", tomorrowMorningPickupTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_home_screen);

        ImageButton livetrackingButton = findViewById(R.id.imageView18);
        ImageButton scheduleButton = findViewById(R.id.imageView15);
        ImageButton profileButton = findViewById(R.id.imageView19);
        holidayTextView = findViewById(R.id.textView20);

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
        ((TextView) findViewById(R.id.calendartoday)).setText(currentDate);

        Calendar tomorrowCalendar = Calendar.getInstance();
        tomorrowCalendar.add(Calendar.DAY_OF_YEAR, 1);
        String tomorrowDate = sdf.format(tomorrowCalendar.getTime());
        ((TextView) findViewById(R.id.calendartommorrow)).setText(tomorrowDate);

        todayMorningPickupTextView = findViewById(R.id.textView36);
        todayReturnTripTextView = findViewById(R.id.textView37);
        tomorrowMorningPickupTextView = findViewById(R.id.textView30);
        tomorrowReturnTripTextView = findViewById(R.id.textView31);

        setupSpinners();
        callAPIs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        callAPIs();
    }

    private void setupSpinners() {
        morningpickup_spinner = findViewById(R.id.morningpickup_spinner);
        morningpickup_spinner2 = findViewById(R.id.morningpickup_spinner2);
        morningpickup_spinner3 = findViewById(R.id.morningpickup_spinner3);
        morningpickup_spinner4 = findViewById(R.id.morningpickup_spinner4);

        String[] options = {"Select option...", "Opt In", "Opt Out"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        morningpickup_spinner.setAdapter(adapter);
        morningpickup_spinner2.setAdapter(adapter);
        morningpickup_spinner3.setAdapter(adapter);
        morningpickup_spinner4.setAdapter(adapter);

        morningpickup_spinner.setSelection(0);
        morningpickup_spinner2.setSelection(0);
        morningpickup_spinner3.setSelection(0);
        morningpickup_spinner4.setSelection(0);

        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = parent.getItemAtPosition(position).toString();
                int spinnerId = parent.getId();

                if (!selection.equals("Select option...")) {
                    String status = selection.equalsIgnoreCase("Opt In") ? "in" : "opted_out";
                    SharedPreferences prefs = getSharedPreferences("user_details", MODE_PRIVATE);
                    int userId = prefs.getInt("user_id", -1);

                    if (spinnerId == R.id.morningpickup_spinner) {
                        ((TextView) findViewById(R.id.optin_out)).setText(selection);
                        postOptStatusToApi(userId, todayScheduleId, status);
                        todayStatus = status;
                    } else if (spinnerId == R.id.morningpickup_spinner2) {
                        ((TextView) findViewById(R.id.optin_out2)).setText(selection);
                        postOptStatusToApi(userId, todayScheduleId, status);
                        todayStatus = status;
                    } else if (spinnerId == R.id.morningpickup_spinner3) {
                        ((TextView) findViewById(R.id.optin_out3)).setText(selection);
                        postOptStatusToApi(userId, tomorrowScheduleId, status);
                        tomorrowStatus = status;
                    } else if (spinnerId == R.id.morningpickup_spinner4) {
                        ((TextView) findViewById(R.id.optin_out4)).setText(selection);
                        postOptStatusToApi(userId, tomorrowScheduleId, status);
                        tomorrowStatus = status;
                    }

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("today_status", todayStatus);
                    editor.putString("tomorrow_status", tomorrowStatus);
                    editor.apply();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        morningpickup_spinner.setOnItemSelectedListener(spinnerListener);
        morningpickup_spinner2.setOnItemSelectedListener(spinnerListener);
        morningpickup_spinner3.setOnItemSelectedListener(spinnerListener);
        morningpickup_spinner4.setOnItemSelectedListener(spinnerListener);
    }

    private void updateSpinnersFromSavedStatus() {
        SharedPreferences prefs = getSharedPreferences("user_details", MODE_PRIVATE);
        todayStatus = prefs.getString("today_status", "");
        tomorrowStatus = prefs.getString("tomorrow_status", "");

        String todayDisplayStatus = checkTimeMatch(todayMorningPickupTime) ? "Completed" :
                todayStatus.equals("in") ? "Opt In" : todayStatus.equals("opted_out") ? "Opt Out" : "";

        String tomorrowDisplayStatus = checkTimeMatch(tomorrowMorningPickupTime) ? "Completed" :
                tomorrowStatus.equals("in") ? "Opt In" : tomorrowStatus.equals("opted_out") ? "Opt Out" : "";

        if (!todayDisplayStatus.isEmpty()) {
            ((TextView) findViewById(R.id.optin_out)).setText(todayDisplayStatus);
            ((TextView) findViewById(R.id.optin_out2)).setText(todayDisplayStatus);
            int pos = todayDisplayStatus.equals("Opt In") ? 1 : todayDisplayStatus.equals("Opt Out") ? 2 : 0;
            morningpickup_spinner.setSelection(pos);
            morningpickup_spinner2.setSelection(pos);
        }

        if (!tomorrowDisplayStatus.isEmpty()) {
            ((TextView) findViewById(R.id.optin_out3)).setText(tomorrowDisplayStatus);
            ((TextView) findViewById(R.id.optin_out4)).setText(tomorrowDisplayStatus);
            int pos = tomorrowDisplayStatus.equals("Opt In") ? 1 : tomorrowDisplayStatus.equals("Opt Out") ? 2 : 0;
            morningpickup_spinner3.setSelection(pos);
            morningpickup_spinner4.setSelection(pos);
        }
    }

    private boolean checkTimeMatch(String apiTime) {
        if (apiTime == null || apiTime.isEmpty()) return false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String currentTime = sdf.format(new Date());
            return currentTime.equals(apiTime);
        } catch (Exception e) {
            return false;
        }
    }

    private void postOptStatusToApi(int passengerId, int scheduleId, String status) {
        executor.execute(() -> {
            try {
                URL url = new URL(ApiConfig.API_URL + "/api/passenger/createUpdatePassengerSchedule");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String jsonInputString = String.format("{\"admin_username\": \"ernani.viaje\", \"schedule_id\": %d, \"passenger_id\": %d, \"status\": \"%s\"}",
                        scheduleId, passengerId, status);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("UTF-8");
                    os.write(input, 0, input.length);
                    os.flush();
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
                        StringBuilder response = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) response.append(inputLine);

                        runOnUiThread(() -> {
                            Toast.makeText(EmployeeHomeScreen.this, "Status updated successfully", Toast.LENGTH_SHORT).show();
                            callAPIs();
                        });
                    }
                } else {
                    try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"))) {
                        StringBuilder errorResponse = new StringBuilder();
                        String line;
                        while ((line = errorReader.readLine()) != null) errorResponse.append(line);
                        String finalErrorMsg = errorResponse.toString();
                        runOnUiThread(() -> Toast.makeText(EmployeeHomeScreen.this, "Server error: " + finalErrorMsg, Toast.LENGTH_SHORT).show());
                    }
                }
            } catch (IOException e) {
                runOnUiThread(() -> Toast.makeText(EmployeeHomeScreen.this, "Failed to update status: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void startNewActivity(Class<?> cls) {
        if (username != null) {
            Intent intent = new Intent(EmployeeHomeScreen.this, cls);
            intent.putExtra("username", username);
            startActivity(intent);
        }
    }

    private void callAPIs() {
        executor.execute(this::fetchDataFromAPIs);
    }

    private void fetchDataFromAPIs() {
        SharedPreferences prefs = getSharedPreferences("user_details", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());

        String[] results = new String[3];

        results[0] = fetchDataFromAPI(ApiConfig.API_URL + "/api/passenger/getBulletinAnnouncement",
                "{\"admin_username\": \"ernani.viaje\", \"date\": \"" + currentDate + "\"}");
        results[1] = fetchDataFromAPI(ApiConfig.API_URL + "/api/passenger/getBulletinHoliday",
                "{\"admin_username\": \"ernani.viaje\"}");
        results[2] = fetchDataFromAPI(ApiConfig.API_URL + "/api/passenger/getScheduleByPassenger",
                String.format(Locale.getDefault(), "{\"admin_username\": \"ernani.viaje\", \"passenger_id\": %d, \"date\": \"%s\"}", userId, currentDate));

        runOnUiThread(() -> processAPIResults(results));
    }

    private void processAPIResults(String[] results) {
        if (results != null && results.length == 3) {
            handleAnnouncementResponse(results[0]);
            handleHolidayResponse(results[1]);
            handlePickupReturnResponse(results[2]);
            updateSpinnersFromSavedStatus();
        } else {
            Toast.makeText(EmployeeHomeScreen.this, "Failed to fetch data from the API", Toast.LENGTH_SHORT).show();
        }
    }

    private String fetchDataFromAPI(String apiUrl, String requestBody) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes());
                os.flush();
            }

            try (InputStream is = conn.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) response.append(line);
                return response.toString();
            }
        } catch (IOException e) {
            return null;
        }
    }

    private void handleAnnouncementResponse(String response) {
        if (response == null) return;
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject apiResult = jsonObject.getJSONObject("api_result");
            if (apiResult.getInt("code") == 200) {
                JSONObject data = apiResult.getJSONObject("data");
                if (data.has("announcement")) {
                    bulletinTextView = findViewById(R.id.bulletin);
                    bulletinTextView.setText(data.getString("announcement"));
                }
            }
        } catch (JSONException e) {
            Toast.makeText(this, "Error parsing announcement response", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleHolidayResponse(String response) {
        if (response == null) return;
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject apiResult = jsonObject.getJSONObject("api_result");
            if (apiResult.getInt("code") == 200) {
                JSONArray dataArray = apiResult.getJSONArray("data");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject data = dataArray.getJSONObject(i);
                    sb.append("Holiday: ").append(data.optString("holiday")).append("\n");
                    sb.append("Announcement: ").append(data.optString("announcement")).append("\n\n");
                }
                holidayTextView.setText(sb.toString());
            }
        } catch (JSONException e) {
            Toast.makeText(this, "Error parsing holiday response", Toast.LENGTH_SHORT).show();
        }
    }

    private void handlePickupReturnResponse(String response) {
        if (response == null) return;
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject apiResult = jsonObject.getJSONObject("api_result");
            if (apiResult.getInt("code") == 200) {
                JSONObject data = apiResult.getJSONObject("data");

                if (data.has("todaySchedule")) {
                    JSONObject today = data.getJSONObject("todaySchedule");
                    todayMorningPickupTime = today.optString("morning_pickup", "No Data");
                    todayMorningPickupTextView.setText(todayMorningPickupTime);
                    todayReturnTripTextView.setText(today.optString("post_work_dropoff", "No Data"));
                    todayScheduleId = today.optInt("schedule_id", todayScheduleId);
                    todayStatus = today.optString("status", todayStatus);
                    SharedPreferences.Editor editor = getSharedPreferences("user_details", MODE_PRIVATE).edit();
                    editor.putString("today_status", todayStatus);
                    editor.apply();
                }

                if (data.has("nextDaySchedule")) {
                    JSONObject nextDay = data.getJSONObject("nextDaySchedule");
                    tomorrowMorningPickupTime = nextDay.optString("morning_pickup", "No Data");
                    tomorrowMorningPickupTextView.setText(tomorrowMorningPickupTime);
                    tomorrowReturnTripTextView.setText(nextDay.optString("post_work_dropoff", "No Data"));
                    tomorrowScheduleId = nextDay.optInt("schedule_id", tomorrowScheduleId);
                    tomorrowStatus = nextDay.optString("status", tomorrowStatus);
                    SharedPreferences.Editor editor = getSharedPreferences("user_details", MODE_PRIVATE).edit();
                    editor.putString("tomorrow_status", tomorrowStatus);
                    editor.apply();
                }
            }
        } catch (JSONException e) {
            Toast.makeText(this, "Error parsing schedule response", Toast.LENGTH_SHORT).show();
        }
    }
}
