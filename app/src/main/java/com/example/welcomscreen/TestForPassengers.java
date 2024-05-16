package com.example.welcomscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TestForPassengers extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_for_passengers);

        ImageButton imageButton20 = findViewById(R.id.imageButton20);
        imageButton20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEditMode();
            }
        });
    }

    private void toggleEditMode() {
        EditText editText1 = findViewById(R.id.editText1);
        EditText editText2 = findViewById(R.id.editText2);
        EditText editText3 = findViewById(R.id.editText3);
        EditText editText4 = findViewById(R.id.editText4);

        if (editText1.isEnabled()) {
            // Disabling edit mode
            editText1.setEnabled(false);
            editText2.setEnabled(false);
            editText3.setEnabled(false);
            editText4.setEnabled(false);
        } else {
            // Enabling edit mode
            editText1.setEnabled(true);
            editText2.setEnabled(true);
            editText3.setEnabled(true);
            editText4.setEnabled(true);
        }
    }
    public void saveChanges(View view) {
        EditText editText1 = findViewById(R.id.editText1);
        EditText editText2 = findViewById(R.id.editText2);
        EditText editText3 = findViewById(R.id.editText3);
        EditText editText4 = findViewById(R.id.editText4);

        // Get the text from EditText fields
        String text1 = editText1.getText().toString();
        String text2 = editText2.getText().toString();
        String text3 = editText3.getText().toString();
        String text4 = editText4.getText().toString();

        // Implement your save logic here
        // For example, you can save the texts to a database or SharedPreferences
        // You can also display a Toast or Snackbar to indicate that changes are saved
        Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(TestForPassengers.this, AdminPassengers.class);
        startActivity(intent);
    }
}