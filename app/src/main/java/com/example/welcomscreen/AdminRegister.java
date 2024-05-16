package com.example.welcomscreen;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class AdminRegister extends AppCompatActivity {

    private EditText editTextFirstName, editTextLastName, editTextUsername, editTextPassword;
    private Spinner spinnerUserRole;
    private ImageButton buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);
        initializeViews();
        setupSpinner();
        setRegisterClickListener();
    }

    private void initializeViews() {
        editTextFirstName = findViewById(R.id.firstname);
        editTextLastName = findViewById(R.id.lastname);
        editTextUsername = findViewById(R.id.RegUser);
        editTextPassword = findViewById(R.id.RegPass);
        spinnerUserRole = findViewById(R.id.spinner);
        buttonRegister = findViewById(R.id.imageButton3);
        ImageButton imageButton2 = findViewById(R.id.imageButton2);
        imageButton2.setOnClickListener(v -> {
            Intent intent = new Intent(AdminRegister.this, AdminSchedule.class);
            startActivity(intent);
            finish();

        });
    }

    private void setupSpinner() {
        // Define user roles directly in Java code
        String[] userRoles = new String[]{"Admin", "Employee", "Driver"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userRoles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserRole.setAdapter(adapter);
    }

    private void setRegisterClickListener() {
        buttonRegister.setOnClickListener(v -> {
            String firstName = editTextFirstName.getText().toString();
            String lastName = editTextLastName.getText().toString();
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();
            int userRolePosition = spinnerUserRole.getSelectedItemPosition();
            int userRole = userRolePosition + 1; // Adjust for index starting from 0
            new RegisterTask().execute(firstName, lastName, username, password, String.valueOf(userRole));
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private class RegisterTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject responseJson = null;
            try {
                URL url = new URL("localhost:4000/api/user/register");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                JSONObject requestData = new JSONObject();
                requestData.put("admin_username", "Arvi123");
                requestData.put("first_name", params[0]);
                requestData.put("last_name", params[1]);
                requestData.put("username", params[2]);
                requestData.put("password", params[3]);
                requestData.put("user_role", params[4]);
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