package com.example.welcomscreen;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
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

public class DriverNewSchedule extends AppCompatActivity {

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_driver_new_schedule);
        ImageButton livetrackingButton = findViewById(R.id.imageButton18);
        ImageButton homeButton = findViewById(R.id.imageButton19);
        ImageButton profileButton = findViewById(R.id.imageButton17);
        Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra("username");
        }

        livetrackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Driver activity
                Intent intent = new Intent(DriverNewSchedule.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username != null) {
                    // Ilagay ang breakpoint sa linya sa ibaba
                    Log.d("DriverHomeScreen", "profileButton clicked"); // Pwedeng lagyan ng log message para sa debugging
                    Intent profileIntent = new Intent(DriverNewSchedule.this, DrivenHomeScreen.class);
                    profileIntent.putExtra("username", username);
                    startActivity(profileIntent);
                } else {
                    // Kung ang username ay null, gawin ang mga naaangkop na hakbang dito, tulad ng pagpapakita ng mensahe ng error o iba pa.
                }
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username != null) {
                    // Ilagay ang breakpoint sa linya sa ibaba
                    Log.d("DriverHomeScreen", "profileButton clicked"); // Pwedeng lagyan ng log message para sa debugging
                    Intent profileIntent = new Intent(DriverNewSchedule.this, DriverProfile.class);
                    profileIntent.putExtra("username", username);
                    startActivity(profileIntent);
                } else {
                    // Kung ang username ay null, gawin ang mga naaangkop na hakbang dito, tulad ng pagpapakita ng mensahe ng error o iba pa.
                }
            }
        });

        // Get the current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(calendar.getTime());

        TextView textDate = findViewById(R.id.calendartoday);
        textDate.setText(currentDate);

        // Fetch and populate the TableLayout with data from the API
        new FetchScheduleTask().execute("Arvi", currentDate);
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