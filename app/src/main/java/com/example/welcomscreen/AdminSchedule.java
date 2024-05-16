package com.example.welcomscreen;

import android.content.Intent;
import android.graphics.Color;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
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
                }else if(position == 3){
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

        // Populate the TableLayout
        populateTableLayout();
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

    private void populateTableLayout() {
        TableLayout tl = findViewById(R.id.tbLayout);

        // Simulating API response
        String apiResponse = "{\"pickups\": [" +
                "{\"pickup\": \"Pickup 1\", \"passengerList\": [\"Passenger 1\", \"Passenger 2\"]}, " +
                "{\"pickup\": \"Pickup 2\", \"passengerList\": [\"Passenger 3\", \"Passenger 4\"]}, " +
                "{\"pickup\": \"Pickup 3\", \"passengerList\": [\"Passenger 5\", \"Passenger 6\"]}, " +
                "{\"pickup\": \"Pickup 4\", \"passengerList\": [\"Passenger 7\", \"Passenger 8\"]}, " +
                "{\"pickup\": \"Pickup 5\", \"passengerList\": [\"Passenger 9\", \"Passenger 10\"]}, " +
                "{\"pickup\": \"Pickup 6\", \"passengerList\": [\"Passenger 11\", \"Passenger 12\"]}, " +
                "{\"pickup\": \"Pickup 7\", \"passengerList\": [\"Passenger 13\", \"Passenger 14\"]}, " +
                "{\"pickup\": \"Pickup 8\", \"passengerList\": [\"Passenger 15\", \"Passenger 16\"]}, " +
                "{\"pickup\": \"Pickup 9\", \"passengerList\": [\"Passenger 17\", \"Passenger 18\"]}, " +
                "{\"pickup\": \"Pickup 10\", \"passengerList\": [\"Passenger 19\", \"Passenger 20\"]}, " +
                "{\"pickup\": \"Pickup 11\", \"passengerList\": [\"Passenger 21\", \"Passenger 22\"]}, " +
                "{\"pickup\": \"Pickup 12\", \"passengerList\": [\"Passenger 23\", \"Passenger 24\"]}, " +
                "{\"pickup\": \"Pickup 13\", \"passengerList\": [\"Passenger 25\", \"Passenger 26\"]}, " +
                "{\"pickup\": \"Pickup 14\", \"passengerList\": [\"Passenger 27\", \"Passenger 28\"]}, " +
                "{\"pickup\": \"Pickup 15\", \"passengerList\": [\"Passenger 29\", \"Passenger 30\"]}, " +
                "{\"pickup\": \"Pickup 16\", \"passengerList\": [\"Passenger 31\", \"Passenger 32\"]}, " +
                "{\"pickup\": \"Pickup 17\", \"passengerList\": [\"Passenger 33\", \"Passenger 34\"]}, " +
                "{\"pickup\": \"Pickup 18\", \"passengerList\": [\"Passenger 35\", \"Passenger 36\"]}, " +
                "{\"pickup\": \"Pickup 19\", \"passengerList\": [\"Passenger 37\", \"Passenger 38\"]}, " +
                "{\"pickup\": \"Pickup 20\", \"passengerList\": [\"Passenger 39\", \"Passenger 40\"]}" +
                "]}";

        try {
            JSONObject responseObj = new JSONObject(apiResponse);
            JSONArray pickupsArray = responseObj.getJSONArray("pickups");

            for (int i = 0; i < pickupsArray.length(); i++) {
                JSONObject pickupObj = pickupsArray.getJSONObject(i);
                String pickup = pickupObj.getString("pickup");
                JSONArray passengersArray = pickupObj.getJSONArray("passengerList");

                // Create TableRow
                TableRow tr = new TableRow(this);
                tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                // Set background color
                int backgroundColor = i % 2 == 0 ? Color.WHITE : Color.parseColor("#B3E4FB");
                tr.setBackgroundColor(backgroundColor);

                // Create TextView for pickup
                TextView tvPickup = createTextView(pickup, 4);
                tr.addView(tvPickup);

                // Create TextView for passenger list
                TextView tvPassengers = createTextView(getPassengersString(passengersArray), 4);
                tr.addView(tvPassengers);

                // Add TableRow to TableLayout
                tl.addView(tr);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Method to create TextView with given text and style
    private TextView createTextView(String text, int widthWeight) {
        TextView textView = new TextView(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = widthWeight;
        params.setMargins(8, 8, 8, 8); // Add margins if needed
        textView.setLayoutParams(params);
        textView.setText(text);
        textView.setPadding(5, 5, 5, 5);
        textView.setTextColor(Color.BLACK); // Adjust text color if needed
        textView.setGravity(Gravity.CENTER); // Center the text horizontally
        return textView;
    }

    // Method to get formatted string for passenger list (vertical)
    private String getPassengersString(JSONArray passengersArray) throws JSONException {
        StringBuilder passengersText = new StringBuilder();
        for (int j = 0; j < passengersArray.length(); j++) {
            passengersText.append(passengersArray.getString(j));
            if (j < passengersArray.length() - 1) {
                passengersText.append("\n"); // Add newline except for the last passenger
            }
        }
        return passengersText.toString();
    }
}
