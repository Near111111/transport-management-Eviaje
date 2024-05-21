package com.example.welcomscreen;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;

public class AdminSchedule extends AppCompatActivity {

    private Spinner spinnerUserRole;
    private boolean isItemSelected = false;

    private ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_schedule);
        TextView textDate = findViewById(R.id.calendartoday);
        ImageButton shuttlesButton = findViewById(R.id.shuttles);
        ImageButton driverButton = findViewById(R.id.driver);
        ImageButton passengersButton = findViewById(R.id.passengers);
        ImageButton bulletinButton = findViewById(R.id.bulletin);

        shuttlesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Shuttles activity
                Intent intent = new Intent(AdminSchedule.this, AdminShuttles.class);
                startActivity(intent);
            }
        });

        driverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Driver activity
                Intent intent = new Intent(AdminSchedule.this, AdminDriver.class);
                startActivity(intent);
            }
        });

        passengersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Passengers activity
                Intent intent = new Intent(AdminSchedule.this, AdminPassengers.class);
                startActivity(intent);
            }
        });

        bulletinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Bulletin activity
                Intent intent = new Intent(AdminSchedule.this, AdminBulletin.class);
                startActivity(intent);
            }
        });

        // Get the current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(calendar.getTime());

        // Set the text of textDate to the current date
        textDate.setText(currentDate);

        // Initialize spinner
        spinnerUserRole = findViewById(R.id.menu1);
        setupSpinner();

        // Set spinner item click listener
        spinnerUserRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {  // "Create New Schedule" selected
                    Intent intent = new Intent(AdminSchedule.this, AdminNewSchedule.class);
                    startActivity(intent);
                    finish();
                } else if (position == 2) {  // "Register" selected
                    Intent intent = new Intent(AdminSchedule.this, AdminRegister.class);
                    startActivity(intent);
                    finish();
                } else if (position == 3) {
                    Intent intent = new Intent(AdminSchedule.this, AdminAddEditGroup.class);
                    startActivity(intent);
                    finish();
                }

                ((TextView) parent.getChildAt(0)).setTextColor(Color.TRANSPARENT);
                isItemSelected = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Fetch and populate the TableLayout with data from the API
        new FetchScheduleTask().execute("Arvi", currentDate);
    }

    private void setupSpinner() {
        // Define user roles directly in Java code
        String[] menu1 = new String[]{" ", "Create New Schedule", "Register", "Passengers Group"};

        // Create a custom adapter to hide the first item when opened
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, menu1) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                // Hide the first item when opened
                if (position == 0) {
                    // Hide the item by setting its height to 0
                    View emptyView = new View(getContext());
                    emptyView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
                    return emptyView;
                }
                // Call the default behavior for other items
                return super.getDropDownView(position, null, parent);
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserRole.setAdapter(adapter);

        // Set default selection to the first item
        spinnerUserRole.setSelection(0);
    }

    private class FetchScheduleTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject responseJson = null;
            try {
                URL url = new URL("https://c889-136-158-57-167.ngrok-free.app/api/passenger/getPassengerList");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                JSONObject requestData = new JSONObject();
                requestData.put("admin_username", params[0]);
                requestData.put("date", params[1]);

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(requestData.toString().getBytes());
                outputStream.flush();
                outputStream.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                responseJson = new JSONObject(response.toString());
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseJson;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    JSONObject apiResult = result.getJSONObject("api_result");
                    if (apiResult.getInt("code") == 200) {
                        populateTableLayout(apiResult.getJSONObject("data"));
                    } else {
                        // Handle API error response
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void populateTableLayout(JSONObject data) throws JSONException {
        TableLayout tl = findViewById(R.id.tbLayout);

        for (Iterator<String> it = data.keys(); it.hasNext(); ) {
            String city = it.next();
            JSONObject cityObj = data.getJSONObject(city);
            for (Iterator<String> iter = cityObj.keys(); iter.hasNext(); ) {
                String pickupLocation = iter.next();
                JSONArray passengersArray = cityObj.getJSONArray(pickupLocation);

                // Create TableRow
                TableRow tr = new TableRow(this);
                tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                // Alternate row color
                int backgroundColor = tl.getChildCount() % 2 == 0 ? Color.WHITE : Color.parseColor("#B3E4FB");
                tr.setBackgroundColor(backgroundColor);

                // Create TextView for pickup location
                TextView tvPickup = createTextView(pickupLocation, 4);
                tr.addView(tvPickup);

                // Create TextView for passenger list
                TextView tvPassengers = createTextView(getPassengersString(passengersArray), 4);
                tr.addView(tvPassengers);

                // Add TableRow to TableLayout
                tl.addView(tr);

                // Extract morningPickup and postWorkDropoff and set them to textView14 and textView27
                for (int i = 0; i < passengersArray.length(); i++) {
                    JSONObject passenger = passengersArray.getJSONObject(i);
                    String morningPickup = passenger.getString("morningPickup");
                    String postWorkDropoff = passenger.getString("postWorkDropoff");
                    if (i == 0) {
                        // Set morningPickup to textView14
                        TextView textView14 = findViewById(R.id.textView14);
                        textView14.setText(morningPickup);
                        // Set postWorkDropoff to textView27
                        TextView textView27 = findViewById(R.id.textView27);
                        textView27.setText(postWorkDropoff);
                    }
                }
            }
        }
    }

    private TextView createTextView(String text, int widthWeight) {
        TextView textView = new TextView(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = widthWeight;
        params.setMargins(8, 8, 8, 8); // Add margins if needed
        textView.setLayoutParams(params);
        textView.setText(text);
        textView.setPadding(5, 5, 5, 5);
        textView.setTextColor(Color.BLACK); // Adjust text color if needed
        return textView;
    }

    private String getPassengersString(JSONArray passengersArray) throws JSONException {
        StringBuilder passengersText = new StringBuilder();
        for (int j = 0; j < passengersArray.length(); j++) {
            JSONObject passenger = passengersArray.getJSONObject(j);
            String fullName = passenger.getString("firstName") + " " + passenger.getString("lastName");
            passengersText.append(fullName);
            if (j < passengersArray.length() - 1) {
                passengersText.append("\n"); // Add newline except for the last passenger
            }
        }
        return passengersText.toString();
    }
}
