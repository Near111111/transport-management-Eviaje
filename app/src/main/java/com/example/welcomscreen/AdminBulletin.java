package com.example.welcomscreen;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class AdminBulletin extends AppCompatActivity {

    ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_bulletin);

        ImageButton shuttlesButton = findViewById(R.id.shuttles);
        ImageButton driverButton = findViewById(R.id.driver);
        ImageButton passengersButton = findViewById(R.id.passengers);
        ImageButton bulletinButton = findViewById(R.id.calendar);
        ImageButton announcementButton = findViewById(R.id.announcementbutton);
        ImageButton holidayButton = findViewById(R.id.holidaybutton);

        shuttlesButton.setOnClickListener(v -> startActivity(new Intent(AdminBulletin.this, AdminShuttles.class)));
        driverButton.setOnClickListener(v -> startActivity(new Intent(AdminBulletin.this, AdminDriver.class)));
        passengersButton.setOnClickListener(v -> startActivity(new Intent(AdminBulletin.this, AdminPassengers.class)));
        bulletinButton.setOnClickListener(v -> startActivity(new Intent(AdminBulletin.this, AdminSchedule.class)));
        //announcementButton.setOnClickListener(v -> startActivity(new Intent(AdminBulletin.this, AdminEditAnnouncement.class)));

        announcementButton.setOnClickListener(v -> {

            TextView textAnnouncements = findViewById(R.id.textView20);
            String existingAnnouncements = textAnnouncements.getText().toString();

            Intent intent = new Intent(AdminBulletin.this, AdminEditAnnouncement.class);
            intent.putExtra("existingAnnouncements", existingAnnouncements);
            startActivity(intent);
        });

        holidayButton.setOnClickListener(v -> {
            TextView textHolidays = findViewById(R.id.textView19);
            String existingHolidays = textHolidays.getText().toString();
//            TextView textAnnouncements = findViewById(R.id.textView20);
//            String existingAnnouncements = textAnnouncements.getText().toString();

            Intent intent = new Intent(AdminBulletin.this, EditHoliday.class);
            intent.putExtra("existingHolidays", existingHolidays);
//            intent.putExtra("existingAnnouncements", existingAnnouncements);
            startActivity(intent);
        });

        // Get announcements from API
        String announcementApiUrl = "https://c889-136-158-57-167.ngrok-free.app/api/passenger/getBulletinAnnouncement";
        String announcementRequestBody = "{\"admin_username\": \"Arvi\", \"date\": \"2024-05-19\"}";
        new GetDataFromAPITask().execute(announcementApiUrl, announcementRequestBody, "announcement");

        // Get holidays from API
        String holidayApiUrl = "https://c889-136-158-57-167.ngrok-free.app/api/passenger/getBulletinHoliday";
        String holidayRequestBody = "{\"admin_username\": \"Arvi\"}";
        new GetDataFromAPITask().execute(holidayApiUrl, holidayRequestBody, "holiday");
    }

    private class GetDataFromAPITask extends AsyncTask<String, Void, String> {
        String dataType; // To distinguish between announcements and holidays

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
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(requestBody);
                writer.flush();
                writer.close();
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
                Toast.makeText(AdminBulletin.this, "Failed to fetch data from the API", Toast.LENGTH_SHORT).show();
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

                            SpannableStringBuilder dataStringBuilder = new SpannableStringBuilder();
                            dataStringBuilder.append("* ").append(announcement).append("\n\n");

                            if (data.has("passengerList")) {
                                JSONArray passengerList = data.getJSONArray("passengerList");
                                for (int i = 0; i < passengerList.length(); i++) {
                                    JSONObject passenger = passengerList.getJSONObject(i);
                                    String passengerInfo = passenger.getString("morning_pickup") + " - " +
                                            passenger.getString("post_work_dropoff") + ", " +
                                            passenger.getString("driver") + ", " +
                                            passenger.getString("shuttle") + ", " +
                                            passenger.getString("passenger_group");
                                    dataStringBuilder.append("* ").append(passengerInfo).append("\n\n");
                                }
                            }

                            TextView textAnnouncements = findViewById(R.id.textView20);
                            textAnnouncements.setText(dataStringBuilder);
                        } else {
                            Toast.makeText(AdminBulletin.this, "No announcement available", Toast.LENGTH_SHORT).show();
                        }
                    } else if (dataType.equals("holiday")) {
                        JSONArray data = apiResult.getJSONArray("data");

                        if (data.length() > 0) {
                            SpannableStringBuilder dataStringBuilder = new SpannableStringBuilder();

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject item = data.getJSONObject(i);
                                String holidayInfo = item.getString("holiday") + " - " +
                                       "\n\n" + item.getString("announcement");

                                SpannableString spannableHoliday = new SpannableString(holidayInfo + "\n\n\n");
                                spannableHoliday.setSpan(new StyleSpan(Typeface.BOLD), 0, item.getString("holiday").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                dataStringBuilder.append(spannableHoliday);
                            }

                            TextView textHolidays = findViewById(R.id.textView19);
                            textHolidays.setText(dataStringBuilder);
                        } else {
                            Toast.makeText(AdminBulletin.this, "No holiday data available", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(AdminBulletin.this, "Failed to fetch data from the API", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
