package com.example.welcomscreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ContactsAdapter contactsAdapter;

    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        tableLayout = findViewById(R.id.tableLayout);

        // Test data
        Contact contact1 = new Contact("John Doe", "123-456-7890");
        Contact contact2 = new Contact("Jane Smith", "098-765-4321");
        Contact contact3 = new Contact("Bob Johnson", "111-222-3333");

        // Add contacts to the table
        addContactRow(contact1);
        addContactRow(contact2);
        addContactRow(contact3);
    }

    private void addContactRow(Contact contact) {
        TableRow row = new TableRow(this);

        TextView nameTextView = new TextView(this);
        nameTextView.setText(contact.getName());

        TextView phoneTextView = new TextView(this);
        phoneTextView.setText(contact.getPhoneNumber());

        row.addView(nameTextView);
        row.addView(phoneTextView);

        tableLayout.addView(row);
    }

    private class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {

        private List<Contact> contacts;

        public ContactsAdapter() {
            contacts = new ArrayList<>();
        }

        public void addContact(Contact contact) {
            contacts.add(contact);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.contact_item, parent, false);
            return new ContactViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
            Contact contact = contacts.get(position);
            holder.nameTextView.setText(contact.getName());
            holder.phoneTextView.setText(contact.getPhoneNumber());
        }

        @Override
        public int getItemCount() {
            return contacts.size();
        }

        private class ContactViewHolder extends RecyclerView.ViewHolder {

            TextView nameTextView;
            TextView phoneTextView;

            public ContactViewHolder(@NonNull View itemView) {
                super(itemView);
                nameTextView = itemView.findViewById(R.id.nameTextView);
                phoneTextView = itemView.findViewById(R.id.phoneTextView);
            }
        }
    }
}