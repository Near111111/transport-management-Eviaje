package com.example.welcomscreen;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class EditContactActivity extends AppCompatActivity {
    private EditText editTextName;
    private EditText editTextPhoneNumber;
    private EditText editTextEmail;
    private EditText editTextAddress;
    private Button buttonUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test111);

        // Initialize views
        editTextName = findViewById(R.id.editText1);
        editTextPhoneNumber = findViewById(R.id.editText2);
        editTextEmail = findViewById(R.id.editText3);
        editTextAddress = findViewById(R.id.editText4);
        buttonUpdate = findViewById(R.id.saveButton);

        // Retrieve contact index from intent extras
        int contactIndex = getIntent().getIntExtra("contactIndex", -1);
        if (contactIndex != -1) {
            ContactCard contact = AdminDriver.contactCardList.get(contactIndex);

            // Populate EditText fields with current contact details
            editTextName.setText(contact.getName());
            editTextPhoneNumber.setText(contact.getPhoneNumber());
            editTextEmail.setText(contact.getEmail());
            editTextAddress.setText(contact.getAddress());

            // Set onClickListener for update button
            buttonUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Update contact details with new values
                    ContactCard newContact = new ContactCard(
                            editTextName.getText().toString(),
                            editTextPhoneNumber.getText().toString(),
                            editTextEmail.getText().toString(),
                            editTextAddress.getText().toString()
                    );
                    // Update the contact in the list
                    AdminDriver.contactCardList.set(contactIndex, newContact);
                    // Notify adapter about the change
                    AdminDriver.contactAdapter.editContact(contactIndex, newContact);


                    // Finish the activity
                    finish();
                }
            });
        }
    }
}