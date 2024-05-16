package com.example.welcomscreen;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity3 extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private ImageButton buttonLogin, backButton;



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
    }
    private void setBackLogin(){
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v ->{
            Intent intent = new Intent(MainActivity3.this, MainActivity2.class);
            startActivity(intent);
            finish();
        });
        ;
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
                URL url = new URL("https://transport-management-r02p.onrender.com/api/user/login");
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
                    int userRole = userDetails.getInt("user_role");
                    redirectBasedOnRole(userRole);
                } else {
                    handleAPIErrorResponse(code);
                }
            } catch (JSONException e) {
                e.printStackTrace();
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
                break;
            case 3:
                intent = new Intent(MainActivity3.this, DrivenHomeScreen.class);
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
}