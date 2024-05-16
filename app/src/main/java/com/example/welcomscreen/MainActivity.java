package com.example.welcomscreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final long DELAY_TIME_MILLISECONDS = 2000; // 2 seconds delay

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Apply window insets to adjust padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Delayed redirect to MainActivity2
        redirectToMainActivity2WithDelay();
    }

    private void redirectToMainActivity2WithDelay() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                redirectToMainActivity2();
            }
        }, DELAY_TIME_MILLISECONDS);
    }

    private void redirectToMainActivity2() {
        // Create an Intent to start MainActivity2
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);
        // Finish MainActivity to prevent returning to it when pressing back button
        finish();
    }
}