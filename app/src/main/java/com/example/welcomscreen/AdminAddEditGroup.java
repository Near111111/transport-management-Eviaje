package com.example.welcomscreen;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;

public class AdminAddEditGroup extends AppCompatActivity {

    private JSONObject jsonResponse;
    private TableLayout quezonCityTableLayout;
    private TableLayout manilaCityTableLayout;
    private String[] urls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_edit_group);

        ImageButton shuttlesButton = findViewById(R.id.shuttles);
        ImageButton driverButton = findViewById(R.id.driver);
        ImageButton passengersButton = findViewById(R.id.passengers);
        ImageButton calendarButton = findViewById(R.id.calendar);
        ImageButton bulletinButton = findViewById(R.id.bulletin);

        shuttlesButton.setOnClickListener(v -> startActivity(new Intent(AdminAddEditGroup.this, AdminShuttles.class)));
        driverButton.setOnClickListener(v -> startActivity(new Intent(AdminAddEditGroup.this, AdminDriver.class)));
        passengersButton.setOnClickListener(v -> startActivity(new Intent(AdminAddEditGroup.this, AdminPassengers.class)));
        calendarButton.setOnClickListener(v -> startActivity(new Intent(AdminAddEditGroup.this, AdminSchedule.class)));
        bulletinButton.setOnClickListener(v -> startActivity(new Intent(AdminAddEditGroup.this, AdminBulletin.class)));

        quezonCityTableLayout = findViewById(R.id.tbLayout);
        manilaCityTableLayout = findViewById(R.id.tbLayout1);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());

        urls = new String[]{ApiConfig.API_URL + "/api/passenger/getPassengerList"};

        String adminUsername = "Arvi";
//        String date = "2024-05-19";

        new FetchAvailableDataTask(adminUsername, currentDate).execute();
    }

    private class FetchAvailableDataTask extends AsyncTask<Void, Void, Boolean> {

        private String adminUsername;
        private String date;

        public FetchAvailableDataTask(String adminUsername, String date) {
            this.adminUsername = adminUsername;
            this.date = date;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                for (String url : urls) {
                    URL apiUrl = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                    JSONObject postData = new JSONObject();
                    postData.put("admin_username", adminUsername);
                    postData.put("date", date);
                    writer.write(postData.toString());
                    writer.flush();

                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();
                    jsonResponse = new JSONObject(response.toString());

                    Log.d("API_RESPONSE", jsonResponse.toString());

                    connection.disconnect();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success && jsonResponse != null) {
                try {
                    parseData(jsonResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AdminAddEditGroup.this, "Failed to parse data", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AdminAddEditGroup.this, "No city group data found", Toast.LENGTH_SHORT).show();
            }
        }

        private void parseData(JSONObject jsonResponse) throws JSONException {
            JSONObject data = jsonResponse.getJSONObject("api_result").getJSONObject("data");
            if (data.has("Quezon City")) {
                addCityGroupToTable(data.getJSONObject("Quezon City"), quezonCityTableLayout);
            }
            if (data.has("Manila City")) {
                addCityGroupToTable(data.getJSONObject("Manila City"), manilaCityTableLayout);
            }
        }

        private void addCityGroupToTable(JSONObject cityGroup, TableLayout tableLayout) throws JSONException {
            Iterator<String> keys = cityGroup.keys();
            while (keys.hasNext()) {
                String placeName = keys.next();
                JSONArray peopleArray = cityGroup.getJSONArray(placeName);
                for (int i = 0; i < peopleArray.length(); i++) {
                    JSONObject person = peopleArray.getJSONObject(i);
                    TableRow tableRow = new TableRow(AdminAddEditGroup.this);
                    tableRow.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    ));
                    String personInfo = person.getString("firstName") + " " + person.getString("lastName");
                    String addressInfo = placeName;
                    TextView address = createTextView(addressInfo, false, 1);
                    TextView textView = createTextView(personInfo, false, 1);
                    tableRow.addView(textView);
                    tableRow.addView(address);

                    int backgroundColor = tableLayout.getChildCount() % 2 == 0 ? Color.WHITE : Color.parseColor("#B3E4FB");
                    tableRow.setBackgroundColor(backgroundColor); // Setting the background color for the current row

                    tableLayout.addView(tableRow);
                }
            }
        }

        private TextView createTextView(String text, boolean isBold, int widthWeight) {
            TextView textView = new TextView(AdminAddEditGroup.this);
            TableRow.LayoutParams params = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.weight = widthWeight;
            params.setMargins(20, 20, 20, 20); // Add margins if needed
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
}
