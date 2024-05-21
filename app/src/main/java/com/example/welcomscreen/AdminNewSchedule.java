package com.example.welcomscreen;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdminNewSchedule extends AppCompatActivity {

    private ImageButton imageButton, buttonSave, buttonCalendar;
    private Spinner spinnerDriver, spinnerShuttle, spinnerCityGroups;
    private EditText editTextMorningPickup, editTextPostWorkDropoff;
    private TableLayout tableLayout;

    private String adminUsername;
    private List<String> driverList = new ArrayList<>();
    private List<String> shuttleList = new ArrayList<>();
    private List<String> cityGroupList = new ArrayList<>();
    private String[] urls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_schedule);

        adminUsername = "Arvi"; // Set the admin username

        // Find views by their IDs
        imageButton = findViewById(R.id.imageButton4);
        spinnerDriver = findViewById(R.id.spinner_driver);
        spinnerShuttle = findViewById(R.id.spinner_shuttle);
        spinnerCityGroups = findViewById(R.id.spinner_city_groups);
        editTextMorningPickup = findViewById(R.id.textView28);
        editTextPostWorkDropoff = findViewById(R.id.textView29);
        buttonSave = findViewById(R.id.imageButton13);
        buttonCalendar = findViewById(R.id.imageButton8);
        tableLayout = findViewById(R.id.tbLayout1);

        // Set click listener for the imageButton to navigate back to AdminSchedule activity
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminNewSchedule.this, AdminSchedule.class);
                startActivity(intent);
                finish();
            }
        });

        // Set click listener for the save button
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewSchedule();
            }
        });

        buttonCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Driver activity
                Intent intent = new Intent(AdminNewSchedule.this, ChooseDate.class);
                startActivity(intent);
            }
        });

        // Call AsyncTask to fetch available drivers, shuttles, and passenger groups from the API
        urls = new String[]{
                "https://c889-136-158-57-167.ngrok-free.app/api/user/getAllDriver",
                "https://c889-136-158-57-167.ngrok-free.app/api/shuttles/selectAllShuttles",
                "https://c889-136-158-57-167.ngrok-free.app/api/passenger/getPassengerGroup"
        };
        new FetchAvailableDataTask().execute(urls);

        // Set item selected listener for the city groups spinner
        spinnerCityGroups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCityGroup = cityGroupList.get(position);
                int passengerGroupId = convertCityGroupNameToId(selectedCityGroup);
                new FetchPassengerListTask().execute(passengerGroupId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void populateSpinner(Spinner spinner, List<String> data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void saveNewSchedule() {
        String morningPickup = editTextMorningPickup.getText().toString();
        String postWorkDropoff = editTextPostWorkDropoff.getText().toString();
        String selectedDriver = spinnerDriver.getSelectedItem().toString();
        String selectedShuttle = spinnerShuttle.getSelectedItem().toString();
        String selectedCityGroup = spinnerCityGroups.getSelectedItem().toString();

        // Get the current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(calendar.getTime());

        // Convert city group name to its corresponding ID
        int passengerGroupId = convertCityGroupNameToId(selectedCityGroup);

        // Create JSON object for the new schedule
        JSONObject newScheduleObject = new JSONObject();
        try {
            newScheduleObject.put("admin_username", adminUsername);
            newScheduleObject.put("morning_pickup", morningPickup);
            newScheduleObject.put("post_work_dropoff", postWorkDropoff);
            newScheduleObject.put("driver", selectedDriver);
            newScheduleObject.put("shuttle", selectedShuttle);
            newScheduleObject.put("date", currentDate);
            newScheduleObject.put("passenger_group_id", passengerGroupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Call AsyncTask to post the new schedule to the API
        new PostNewScheduleTask().execute("https://c889-136-158-57-167.ngrok-free.app/api/passenger/createNewSchedule", newScheduleObject.toString());
    }

    private int convertCityGroupNameToId(String cityName) {
        // Implement your logic here to map city group names to IDs
        if (cityName.equals("Quezon City")) {
            return 1;
        } else if (cityName.equals("Manila City")) {
            return 2;
        }
        return 0; // Return 0 or handle other cases accordingly
    }

    private class FetchAvailableDataTask extends AsyncTask<String[], Void, Void> {

        @Override
        protected Void doInBackground(String[]... urls) {
            try {
                for (String url : urls[0]) {
                    URL apiUrl = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    // Set the admin username parameter
                    JSONObject requestData = new JSONObject();
                    requestData.put("admin_username", adminUsername);

                    // Write the request data to the connection output stream
                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(requestData.toString().getBytes());
                    outputStream.flush();
                    outputStream.close();

                    // Read the response from the API
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    // Parse the JSON response and extract the available data
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONObject apiResult = jsonResponse.getJSONObject("api_result");
                    JSONArray data = apiResult.optJSONArray("data");

                    if (url.contains("getAllDriver") && data != null) {
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject user = data.getJSONObject(i);
                            driverList.add(user.getString("driver_name"));
                        }
                    } else if (url.contains("selectAllShuttles") && data != null) {
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject shuttle = data.getJSONObject(i);
                            shuttleList.add(shuttle.getString("model"));
                        }
                    } else if (url.contains("getPassengerGroup") && data != null) {
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject group = data.getJSONObject(i);
                            cityGroupList.add(group.getString("passengerGroup"));
                        }
                    }

                    // Disconnect the connection
                    connection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // Populate spinners with available data
            if (!driverList.isEmpty()) {
                populateSpinner(spinnerDriver, driverList);
            } else {
                Toast.makeText(AdminNewSchedule.this, "No available drivers", Toast.LENGTH_SHORT).show();
            }

            if (!shuttleList.isEmpty()) {
                populateSpinner(spinnerShuttle, shuttleList);
            } else {
                Toast.makeText(AdminNewSchedule.this, "No available shuttles", Toast.LENGTH_SHORT).show();
            }

            if (!cityGroupList.isEmpty()) {
                // Special handling for city group spinner
                ArrayAdapter<String> cityGroupAdapter = new ArrayAdapter<String>(AdminNewSchedule.this, android.R.layout.simple_spinner_item, cityGroupList) {
                    @NonNull
                    @Override
                    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                        TextView view = (TextView) super.getView(position, convertView, parent);
                        view.setTextColor(Color.WHITE);
                        return view;
                    }
                };
                cityGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCityGroups.setAdapter(cityGroupAdapter);
            } else {
                Toast.makeText(AdminNewSchedule.this, "No available passenger groups", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class FetchPassengerListTask extends AsyncTask<Integer, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(Integer... params) {
            JSONArray passengerList = null;
            try {
                URL url = new URL("https://c889-136-158-57-167.ngrok-free.app/api/passenger/getPassengerListGroupById");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Create JSON object with passenger_group_id
                JSONObject requestObject = new JSONObject();
                requestObject.put("passenger_group_id", params[0]);

                // Write the request data to the connection output stream
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(requestObject.toString().getBytes());
                outputStream.flush();
                outputStream.close();

                // Read the response from the API
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                // Parse the JSON response and extract the passenger list data
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONObject apiResult = jsonResponse.getJSONObject("api_result");
                passengerList = apiResult.optJSONArray("data");

                // Disconnect the connection
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return passengerList;
        }

        @Override
        protected void onPostExecute(JSONArray passengerList) {
            super.onPostExecute(passengerList);
            if (passengerList != null) {
                updateTableLayout(passengerList);
            } else {
                Toast.makeText(AdminNewSchedule.this, "Failed to retrieve passenger list", Toast.LENGTH_SHORT).show();
            }
        }

        private void updateTableLayout(JSONArray passengerList) {
            try {
                // Clear existing table rows
                tableLayout.removeAllViews();

                // Add header row
                TableRow headerRow = new TableRow(AdminNewSchedule.this);
                headerRow.addView(createTextView("First Name", true, 1));
                headerRow.addView(createTextView("Last Name", true, 1));
                headerRow.addView(createTextView("Pickup/Dropoff", true, 1));
                tableLayout.addView(headerRow);

                // Add passenger data rows
                for (int i = 0; i < passengerList.length(); i++) {
                    JSONObject passenger = passengerList.getJSONObject(i);
                    TableRow row = new TableRow(AdminNewSchedule.this);
                    row.addView(createTextView(passenger.getString("firstName"), false, 1)); // Adjust key as needed
                    row.addView(createTextView(passenger.getString("lastName"), false, 1)); // Adjust key as needed
                    row.addView(createTextView(passenger.getString("pickupDropoff"), false, 1)); // Adjust key as needed
                    tableLayout.addView(row);
                    int backgroundColor = tableLayout.getChildCount() % 2 == 0 ? Color.WHITE : Color.parseColor("#B3E4FB");
                    row.setBackgroundColor(backgroundColor); // Setting the background color for the current row
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        private TextView createTextView(String text, boolean isBold, int widthWeight) {
            TextView textView = new TextView(AdminNewSchedule.this);
            TableRow.LayoutParams params = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.weight = widthWeight;
            params.setMargins(8, 8, 8, 8); // Add margins if needed
            textView.setLayoutParams(params);
            textView.setText(text);
            textView.setPadding(5, 5, 5, 5);
            textView.setTextColor(Color.BLACK);
            // Adjust text color if needed
            if (isBold) {
                textView.setTypeface(null, Typeface.BOLD);
            }
            return textView;
        }
    }

    private class PostNewScheduleTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Write the new schedule JSON object to the connection output stream
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(params[1].getBytes());
                outputStream.flush();
                outputStream.close();

                // Get the response code
                int responseCode = connection.getResponseCode();

                // Disconnect the connection
                connection.disconnect();

                // Return true if the response code is 200 (OK), false otherwise
                return responseCode == HttpURLConnection.HTTP_OK;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                Toast.makeText(AdminNewSchedule.this, "New schedule saved successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AdminNewSchedule.this, "Failed to save new schedule", Toast.LENGTH_SHORT).show();
            }
        }
    }
}