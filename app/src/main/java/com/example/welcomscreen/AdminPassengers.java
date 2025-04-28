package com.example.welcomscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AdminPassengers extends AppCompatActivity {
    private RecyclerView recyclerView;
    public static ContactAdapterForPassengers contactAdapter;
    public static List<ContactCardForPassengers> contactCardList;
    private Button nextButton;
    private Button prevButton;
    private ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_passengers);

        ImageButton shuttlesButton = findViewById(R.id.shuttles);
        ImageButton driverButton = findViewById(R.id.driver);
        ImageButton passengersButton = findViewById(R.id.calendar);
        ImageButton bulletinButton = findViewById(R.id.bulletin);

        shuttlesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPassengers.this, AdminShuttles.class);
                startActivity(intent);
            }
        });

        driverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPassengers.this, AdminDriver.class);
                startActivity(intent);
            }
        });

        passengersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPassengers.this, AdminSchedule.class);
                startActivity(intent);
            }
        });

        bulletinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPassengers.this, AdminBulletin.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        nextButton = findViewById(R.id.nextButton);
        prevButton = findViewById(R.id.prevButton);

        imageButton = findViewById(R.id.imageButton20);
        imageButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminPassengers.this, TestForPassengers.class);
            startActivity(intent);
            finish();
        });

        contactCardList = new ArrayList<>();
        contactAdapter = new ContactAdapterForPassengers(contactCardList);
        recyclerView.setAdapter(contactAdapter);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactAdapter.nextPage();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactAdapter.previousPage();
            }
        });

        fetchPassengerData();
    }

    private void fetchPassengerData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://c889-136-158-57-167.ngrok-free.app/api/user/getAllPassenger");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");

                    // Create JSON object for the request parameters
                    JSONObject requestData = new JSONObject();
                    requestData.put("admin_username", "Arvi");

                    // Write request parameters to the connection
                    connection.setDoOutput(true);
                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(requestData.toString().getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();

                    // Get the response from the server
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    bufferedReader.close();
                    inputStream.close();

                    // Parse JSON response
                    JSONObject jsonResponse = new JSONObject(stringBuilder.toString());
                    JSONObject apiResult = jsonResponse.getJSONObject("api_result");
                    int code = apiResult.getInt("code");
                    if (code == 200) {
                        JSONArray data = apiResult.getJSONArray("data");
                        contactCardList.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject passenger = data.getJSONObject(i);
                            String name = passenger.getString("passenger_name");
                            String phoneNumber = String.valueOf(passenger.getLong("mobile_number"));
                            String email = passenger.getString("email_add");
                            String address = passenger.getString("address");
                            String group = passenger.getString("passenger_group");
                            contactCardList.add(new ContactCardForPassengers(name, phoneNumber, email, address, group));
                        }

                        // Update UI on the main thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                contactAdapter.refresh();
                            }
                        });
                    } else {
                        // Handle API error
                        final String errorMessage = "API Error: " + code;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AdminPassengers.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    connection.disconnect();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    // Handle exception
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AdminPassengers.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    public static class ContactAdapterForPassengers extends RecyclerView.Adapter<ContactAdapterForPassengers.ContactViewHolder> {
        private List<ContactCardForPassengers> contactCard;
        private int itemsPerPage = 1;
        private int currentPage = 0;

        public ContactAdapterForPassengers(List<ContactCardForPassengers> contactCard) {
            this.contactCard = contactCard;
        }

        @NonNull
        @Override
        public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_cardview_passenger, parent, false);
            return new ContactViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
            List<ContactCardForPassengers> currentPageItems = getCurrentPageItems();
            final int adapterPosition = holder.getAdapterPosition();
            final ContactCardForPassengers contact = currentPageItems.get(position);

            holder.textViewName.setText(contact.getName());
            holder.textViewPhoneNumber.setText(contact.getPhoneNumber());
            holder.textViewEmail.setText(contact.getEmail());
            holder.textViewAddress.setText(contact.getAddress());
            holder.textViewGroup.setText(contact.getGroup());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), EditContactActivityForPassengers.class);
                    intent.putExtra("contactIndex", adapterPosition);
                    v.getContext().startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return getCurrentPageItems().size();
        }

        public static class ContactViewHolder extends RecyclerView.ViewHolder {
            TextView textViewName;
            TextView textViewPhoneNumber;
            TextView textViewEmail;
            TextView textViewAddress;
            TextView textViewGroup;

            public ContactViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewName = itemView.findViewById(R.id.textView1);
                textViewPhoneNumber = itemView.findViewById(R.id.textView2);
                textViewEmail = itemView.findViewById(R.id.textView3);
                textViewAddress = itemView.findViewById(R.id.textView4);
                textViewGroup = itemView.findViewById(R.id.group);
            }
        }

        private List<ContactCardForPassengers> getCurrentPageItems() {
            int start = currentPage * itemsPerPage;
            int end = Math.min(start + itemsPerPage, contactCard.size());
            return contactCard.subList(start, end);
        }

        public void refresh() {
            notifyDataSetChanged();
        }

        public void nextPage() {
            if ((currentPage + 1) * itemsPerPage < contactCard.size()) {
                currentPage++;
                refresh();
            }
        }

        public void previousPage() {
            if (currentPage > 0) {
                currentPage--;
                refresh();
            }
        }

        public void editContact(int index, ContactCardForPassengers newContact) {
            contactCard.set(index, newContact);
            refresh();
        }
    }

    public static class ContactCardForPassengers {
        private String name;
        private String phoneNumber;
        private String email;
        private String address;
        private String group;

        public ContactCardForPassengers(String name, String phoneNumber, String email, String address, String group) {
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.email = email;
            this.address = address;
            this.group = group;
        }

        public String getName() {
            return name;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getEmail() {
            return email;
        }

        public String getAddress() {
            return address;
        }

        public String getGroup() {
            return group;
        }
    }
}
