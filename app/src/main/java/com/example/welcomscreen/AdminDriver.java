package com.example.welcomscreen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class AdminDriver extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private static List<ContactCard> contactCardList;
    private Button nextButton;
    private Button prevButton;
    private ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_driver);

        ImageButton shuttlesButton = findViewById(R.id.shuttles);
        ImageButton driverButton = findViewById(R.id.calendar);
        ImageButton passengersButton = findViewById(R.id.passengers);
        ImageButton bulletinButton = findViewById(R.id.bulletin);

        shuttlesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDriver.this, AdminShuttles.class);
                startActivity(intent);
            }
        });

        driverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDriver.this, AdminSchedule.class);
                startActivity(intent);
            }
        });

        passengersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDriver.this, AdminPassengers.class);
                startActivity(intent);
            }
        });

        bulletinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDriver.this, AdminBulletin.class);
                startActivity(intent);
            }
        });

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize pagination controls
        nextButton = findViewById(R.id.nextButton);
        prevButton = findViewById(R.id.prevButton);

        imageButton = findViewById(R.id.imageButton20);
        imageButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDriver.this, Test111.class);
            intent.putParcelableArrayListExtra("contactCardList", new ArrayList<>(contactCardList));
            startActivity(intent);
            finish();
        });

        // Initialize contactCardList
        contactCardList = new ArrayList<>();

        // Initialize and set adapter to RecyclerView
        contactAdapter = new ContactAdapter(contactCardList);
        recyclerView.setAdapter(contactAdapter);

        // Set onClickListeners for pagination controls
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

        // Fetch driver data from the API
        fetchDriverData();
    }

    private void fetchDriverData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(ApiConfig.API_URL + "/api/user/getAllDriver");
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
                            JSONObject driver = data.getJSONObject(i);
                            String name = driver.getString("driver_name");
                            String phoneNumber = String.valueOf(driver.getLong("mobile_number"));
                            String email = driver.getString("email_add");
                            String address = driver.getString("address");
                            String username = driver.getString("username"); // Assuming "username" field exists
                            contactCardList.add(new ContactCard(name, phoneNumber, email, address, username));
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
                                Toast.makeText(AdminDriver.this, errorMessage, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(AdminDriver.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
        private List<ContactCard> contactCard;
        private int itemsPerPage = 1;
        private int currentPage = 0;

        private Context context;

        public ContactAdapter(List<ContactCard> contactCard) {
            this.contactCard = contactCard;
        }

        @NonNull
        @Override
        public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_cardview_layout, parent, false);
            return new ContactViewHolder(view);
        }

        public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
            List<ContactCard> currentPageItems = getCurrentPageItems();
            final ContactCard contact = currentPageItems.get(position);

            holder.textViewName.setText(contact.getName());
            holder.textViewPhoneNumber.setText(contact.getPhoneNumber());
            holder.textViewEmail.setText(contact.getEmail());
            holder.textViewAddress.setText(contact.getAddress());

//            holder.editButton.setOnClickListener(v -> editDriver(position));
        }

//        private void editDriver(int position) {
//            AdminDriver.ContactCard contact = contactCardList.get(position);
//            Intent intent = new Intent(context, Test111.class);
//            intent.putExtra("contact", contact);
//            context.startActivity(intent);
//        }
        @Override
        public int getItemCount() {
            return getCurrentPageItems().size();
        }

        public class ContactViewHolder extends RecyclerView.ViewHolder {
            TextView textViewName;
            TextView textViewPhoneNumber;
            TextView textViewEmail;
            TextView textViewAddress;

            ImageView editButton;

            public ContactViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewName = itemView.findViewById(R.id.textView1);
                textViewPhoneNumber = itemView.findViewById(R.id.textView2);
                textViewEmail = itemView.findViewById(R.id.textView3);
                textViewAddress = itemView.findViewById(R.id.textView4);
                editButton = itemView.findViewById(R.id.editButton);
            }
        }

        private List<ContactCard> getCurrentPageItems() {
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
    }

    public static class ContactCard implements Parcelable {
        private String name;
        private String phoneNumber;
        private String email;
        private String address;
        private String username; // New field for username

        public ContactCard(String name, String phoneNumber, String email, String address, String username) {
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.email = email;
            this.address = address;
            this.username = username;
        }

        protected ContactCard(Parcel in) {
            name = in.readString();
            phoneNumber = in.readString();
            email = in.readString();
            address = in.readString();
            username = in.readString();
        }

        public static final Creator<ContactCard> CREATOR = new Creator<ContactCard>() {
            @Override
            public ContactCard createFromParcel(Parcel in) {
                return new ContactCard(in);
            }

            @Override
            public ContactCard[] newArray(int size) {
                return new ContactCard[size];
            }
        };

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

        public String getUsername() {
            return username; // Return the username
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeString(phoneNumber);
            dest.writeString(email);
            dest.writeString(address);
            dest.writeString(username);
        }
    }
}