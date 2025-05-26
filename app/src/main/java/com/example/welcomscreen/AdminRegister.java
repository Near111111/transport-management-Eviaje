package com.example.welcomscreen;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

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

        ImageButton backButton = findViewById(R.id.imageButton2);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminRegister.this, AdminSchedule.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupSpinners() {
        String[] userRoles = {"Admin", "Employee", "Driver"};
        ArrayAdapter<String> userRoleAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, userRoles);
        userRoleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserRole.setAdapter(userRoleAdapter);

        String[] cityGroups = {"Quezon City Group", "Manila City Group"};
        ArrayAdapter<String> cityGroupAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, cityGroups);
        cityGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCityGroup.setAdapter(cityGroupAdapter);

        spinnerCityGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateCityLocations(cityGroups[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Required empty implementation
            }
        });
    }

    private void updateCityLocations(String cityGroup) {
        String[] quezonLocations = {"Litex, Quezon City", "Coa, Quezon City", "Q. Ave Quezon City",
                "SM Sta. Mesa", "Zamora St. Quirino", "Taft Avenue, Manila"};
        String[] manilaLocations = {"Monumento", "Blumentritt Market", "UST",
                "Doroteo Jose LRT Station", "Plaza Lacson"};

        String[] locations = "Manila City Group".equals(cityGroup) ? manilaLocations : quezonLocations;

        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, locations);
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
            int userRole = spinnerUserRole.getSelectedItemPosition() + 1;

            String selectedCityGroup = (String) spinnerCityGroup.getSelectedItem();
            String selectedLocation = (String) spinnerCityLocations.getSelectedItem();
            int cityGroupId = cityGroupMap.get(selectedCityGroup);
            int locationId = locationMap.get(selectedLocation);

            new RegisterTask().execute(firstName, lastName, username, password,
                    String.valueOf(userRole), mobileNumber, emailAdd,
                    String.valueOf(locationId), String.valueOf(cityGroupId));
        });
    }

    private class RegisterTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject responseJson = null;
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(ApiConfig.API_URL + "/api/user/register");
                connection = (HttpURLConnection) url.openConnection();
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

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                responseJson = new JSONObject(response.toString());

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
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