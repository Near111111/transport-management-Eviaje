package com.example.welcomscreen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class EditVehicleActivity extends AppCompatActivity {

    private Button button;

    private EditText nameEditText, modelEditText, plateNumberEditText, colorEditText, capacityEditText, codingEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_vehicle);

        nameEditText = findViewById(R.id.editTextName);
        modelEditText = findViewById(R.id.editTextModel);
        plateNumberEditText = findViewById(R.id.editTextPlateNumber);
        colorEditText = findViewById(R.id.editTextColor);
        capacityEditText = findViewById(R.id.editTextCapacity);
        codingEditText = findViewById(R.id.editTextCoding);

        // Retrieve the data from intent and set it to the EditText fields
        if (getIntent() != null) {
            nameEditText.setText(getIntent().getStringExtra("vehicle_name"));
            modelEditText.setText(getIntent().getStringExtra("vehicle_model"));
            plateNumberEditText.setText(getIntent().getStringExtra("plate_number"));
            colorEditText.setText(getIntent().getStringExtra("color"));
            capacityEditText.setText(String.valueOf(getIntent().getIntExtra("sitting_capacity", 0)));
            codingEditText.setText(getIntent().getStringExtra("coding"));
        }

        button = findViewById(R.id.backButton);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(EditVehicleActivity.this, AdminShuttles.class);
            startActivity(intent);
            finish();


        });


        // Implement the save logic and update the RecyclerView item if needed
    }
}
