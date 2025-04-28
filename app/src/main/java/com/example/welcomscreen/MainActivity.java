package com.example.welcomscreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

public class MainActivity extends AppCompatActivity {

    private static final long DELAY_TIME_MILLISECONDS = 2000; // 2 seconds delay

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge display for newer devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }

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
