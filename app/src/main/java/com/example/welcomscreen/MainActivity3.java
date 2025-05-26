package com.example.welcomscreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity3 extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private ImageButton buttonLogin, backButton;
    private String loggedInUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        initializeViews();
        setLoginClickListener();
        setBackLogin();
    }

    private void initializeViews() {
        editTextUsername = findViewById(R.id.username_input);
        editTextPassword = findViewById(R.id.password_input);
        buttonLogin = findViewById(R.id.secondlog);
        backButton = findViewById(R.id.backButton);
    }

    private void setBackLogin() {
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity3.this, MainActivity2.class);
            startActivity(intent);
            finish();
        });
    }

    private void setLoginClickListener() {
        buttonLogin.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();
            new LoginTask().execute(username, password);
        });
    }

    private class LoginTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject responseJson = null;
            try {
                URL url = new URL(ApiConfig.API_URL + "/api/user/login");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                JSONObject requestData = new JSONObject();
                requestData.put("username", params[0]);
                requestData.put("password", params[1]);

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(requestData.toString().getBytes());
                outputStream.flush();
                outputStream.close();

                int responseCode = connection.getResponseCode();
                Log.d("LOGIN_RESPONSE_CODE", String.valueOf(responseCode));

                InputStreamReader streamReader;
                if (responseCode >= 200 && responseCode < 300) {
                    streamReader = new InputStreamReader(connection.getInputStream());
                } else {
                    streamReader = new InputStreamReader(connection.getErrorStream());
                    Log.e("LOGIN_ERROR_BODY", readStream(connection.getErrorStream()));
                }

                BufferedReader in = new BufferedReader(streamReader);
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                Log.d("LOGIN_RESPONSE_BODY", response.toString());
                responseJson = new JSONObject(response.toString());
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("LOGIN_ERROR", "Exception during login: " + e.getMessage(), e);
            }
            return responseJson;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            handleLoginResponse(result);
        }
    }

    private void handleLoginResponse(JSONObject result) {
        if (result != null) {
            try {
                JSONObject apiResult = result.getJSONObject("api_result");
                int code = apiResult.getInt("code");
                if (code == 200) {
                    JSONObject userDetails = apiResult.getJSONObject("data").getJSONObject("user_details");
                    loggedInUsername = userDetails.getString("username");
                    int userRole = userDetails.getInt("user_role");

                    if (userDetails.has("user_id")) {
                        int userId = userDetails.getInt("user_id");

                        SharedPreferences prefs = getSharedPreferences("user_details", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("user_id", userId);
                        editor.apply();

                        Log.d("LOGIN_DEBUG", "User ID saved: " + userId);
                    } else {
                        Log.e("LOGIN_DEBUG", "user_id not found in userDetails");
                    }

                    redirectBasedOnRole(userRole);
                } else {
                    handleAPIErrorResponse(code);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("MainActivity3", "Null response from server", e);
            }
        } else {
            Toast.makeText(MainActivity3.this, "Null response from server", Toast.LENGTH_SHORT).show();
        }
    }

    private void redirectBasedOnRole(int userRole) {
        Intent intent;
        switch (userRole) {
            case 1:
                intent = new Intent(MainActivity3.this, AdminSchedule.class);
                break;
            case 2:
                intent = new Intent(MainActivity3.this, EmployeeHomeScreen.class);
                intent.putExtra("username", loggedInUsername);
                break;
            case 3:
                intent = new Intent(MainActivity3.this, DrivenHomeScreen.class);
                intent.putExtra("username", loggedInUsername);
                break;
            default:
                Toast.makeText(MainActivity3.this, "Unknown User Role", Toast.LENGTH_SHORT).show();
                return;
        }
        startActivity(intent);
        finish();
    }

    private void handleAPIErrorResponse(int code) {
        Toast.makeText(MainActivity3.this, "API Error: " + code, Toast.LENGTH_SHORT).show();
    }
    private String readStream(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (Exception e) {
            return "Error reading stream: " + e.getMessage();
        }
    }
}