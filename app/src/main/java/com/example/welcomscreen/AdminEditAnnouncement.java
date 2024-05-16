package com.example.welcomscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminEditAnnouncement extends AppCompatActivity {

    EditText editTextAnnouncement;
    Button buttonSubmit;
    ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_announcement);

        imageButton = findViewById(R.id.imageButton6);
        imageButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminEditAnnouncement.this, AdminBulletin.class);
            startActivity(intent);
            finish();
        });

        editTextAnnouncement = findViewById(R.id.editText_announcement);
        buttonSubmit = findViewById(R.id.button_submit);

        editTextAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Allow user to edit the text when clicked
                editTextAnnouncement.setFocusableInTouchMode(true);
                editTextAnnouncement.requestFocus();
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEnteredText = editTextAnnouncement.getText().toString();
                Toast.makeText(AdminEditAnnouncement.this, "User input: " + userEnteredText, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
