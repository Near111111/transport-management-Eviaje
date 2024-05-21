package com.example.welcomscreen;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class AdminRegister extends AppCompatActivity {

    private EditText editTextFirstName, editTextLastName, editTextUsername, editTextPassword, editTextMobileNumber, editTextEmailAdd;
    private Spinner spinnerUserRole, spinnerCityGroup, spinnerCityLocations;
    private ImageButton buttonRegister;

    private final HashMap<String, Integer> cityGroupMap = new HashMap<String, Integer>() {{
        put("Quezon City Group", 1);
        put("Manila City Group", 2);
    }};

    private final HashMap<String, Integer> locationMap = new HashMap<String, Integer>() {{
        put("Litex, Quezon City", 1);
        put("Coa, Quezon City", 2);
        put("Q. Ave Quezon City", 3);
        put("SM Sta. Mesa", 4);
        put("Zamora St. Quirino", 5);
        put("Taft Avenue, Manila", 6);
        put("Monumento", 7);
        put("Blumentritt Market", 8);
        put("UST", 9);
        put("Doroteo Jose LRT Station", 10);
        put("Plaza Lacson", 11);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);
        initializeViews();
        setupSpinners();
        setRegisterClickListener();
    }

    private void initializeViews() {
        editTextFirstName = findViewById(R.id.firstname);
        editTextLastName = findViewById(R.id.lastname);
        editTextUsername = findViewById(R.id.RegUser);
        editTextPassword = findViewById(R.id.RegPass);
        editTextMobileNumber = findViewById(R.id.RegPass2);
        editTextEmailAdd = findViewById(R.id.RegPass4);
        spinnerUserRole = findViewById(R.id.spinner);
        spinnerCityGroup = findViewById(R.id.spinnerCityGroup);
        spinnerCityLocations = findViewById(R.id.spinnerCityLocations);
        buttonRegister = findViewById(R.id.imageButton3);
        ImageButton imageButton2 = findViewById(R.id.imageButton2);
        imageButton2.setOnClickListener(v -> {
            Intent intent = new Intent(AdminRegister.this, AdminSchedule.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupSpinners() {
        // Setup user roles spinner
        String[] userRoles = new String[]{"Admin", "Employee", "Driver"};
        ArrayAdapter<String> userRoleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userRoles);
        userRoleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserRole.setAdapter(userRoleAdapter);

        // Setup city group spinner
        String[] cityGroups = new String[]{"Quezon City Group", "Manila City Group"};
        ArrayAdapter<String> cityGroupAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityGroups);
        cityGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCityGroup.setAdapter(cityGroupAdapter);

        // Add listener to city group spinner
        spinnerCityGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateCityLocations(cityGroups[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void updateCityLocations(String cityGroup) {
        String[] manilaLocations = new String[]{"Monumento", "Blumentritt Market", "UST", "Doroteo Jose LRT Station", "Plaza Lacson"};
        String[] quezonLocations = new String[]{"Litex, Quezon City", "Coa, Quezon City", "Q. Ave Quezon City", "SM Sta. Mesa", "Zamora St. Quirino", "Taft Avenue, Manila"};

        ArrayAdapter<String> locationAdapter;

        if (cityGroup.equals("Manila City Group")) {
            locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, manilaLocations);
        } else {
            locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, quezonLocations);
        }

        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCityLocations.setAdapter(locationAdapter);
    }

    private void setRegisterClickListener() {
        buttonRegister.setOnClickListener(v -> {
            String firstName = editTextFirstName.getText().toString();
            String lastName = editTextLastName.getText().toString();
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();
            String mobileNumber = editTextMobileNumber.getText().toString();
            String emailAdd = editTextEmailAdd.getText().toString();
            int userRolePosition = spinnerUserRole.getSelectedItemPosition();
            int userRole = userRolePosition + 1; // Adjust for index starting from 0

            String selectedCityGroup = (String) spinnerCityGroup.getSelectedItem();
            String selectedLocation = (String) spinnerCityLocations.getSelectedItem();
            int cityGroupId = cityGroupMap.get(selectedCityGroup);
            int locationId = locationMap.get(selectedLocation);

            new RegisterTask().execute(firstName, lastName, username, password, String.valueOf(userRole), mobileNumber, emailAdd, String.valueOf(locationId), String.valueOf(cityGroupId));
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private class RegisterTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject responseJson = null;
            try {
                URL url = new URL("https://c889-136-158-57-167.ngrok-free.app/api/user/register");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                JSONObject requestData = new JSONObject();
                requestData.put("admin_username", "Arvi");
                requestData.put("first_name", params[0]);
                requestData.put("last_name", params[1]);
                requestData.put("username", params[2]);
                requestData.put("password", params[3]);
                requestData.put("user_role", params[4]);
                requestData.put("mobile_number", params[5]);
                requestData.put("email_add", params[6]);
                requestData.put("pickup_dropoff_id", params[7]);
                requestData.put("passenger_group_id", params[8]);
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
            handleRegistrationResponse(result);
        }
    }

    private void handleRegistrationResponse(JSONObject result) {
        if (result != null) {
            try {
                JSONObject apiResult = result.getJSONObject("api_result");
                int code = apiResult.getInt("code");
                if (code == 200) {
                    Toast.makeText(AdminRegister.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    // Redirect to login activity or any other appropriate action
                } else {
                    handleAPIErrorResponse(code);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(AdminRegister.this, "Null response from server", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleAPIErrorResponse(int code) {
        Toast.makeText(AdminRegister.this, "API Error: " + code, Toast.LENGTH_SHORT).show();
    }
}
