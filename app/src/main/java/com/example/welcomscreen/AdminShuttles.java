package com.example.welcomscreen;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AdminShuttles extends AppCompatActivity {
    private Vehicle lastFetchedVehicle;
    private RecyclerView recyclerView;
    private VehicleAdapter vehicleAdapter;
    private List<Vehicle> vehicleList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_shuttles);

        // Initialize RecyclerView and other components
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        vehicleList = new ArrayList<>();
        vehicleAdapter = new VehicleAdapter(this, vehicleList);
        recyclerView.setAdapter(vehicleAdapter);

        new FetchShuttlesTask().execute();

        // Initialize buttons
        ImageButton buttonPlus = findViewById(R.id.button_plus);
        ImageButton buttonMinus = findViewById(R.id.button_minus);
        ImageButton shuttlesButton = findViewById(R.id.calendar);
        ImageButton driverButton = findViewById(R.id.driver);
        ImageButton passengersButton = findViewById(R.id.passengers);
        ImageButton bulletinButton = findViewById(R.id.bulletin);
        Button addButton = findViewById(R.id.add_button);

        // Set click listeners for buttons
        buttonPlus.setOnClickListener(v -> nextVehicle());
        buttonMinus.setOnClickListener(v -> previousVehicle());
        shuttlesButton.setOnClickListener(v -> startActivity(new Intent(AdminShuttles.this, AdminSchedule.class)));
        driverButton.setOnClickListener(v -> startActivity(new Intent(AdminShuttles.this, AdminDriver.class)));
        passengersButton.setOnClickListener(v -> startActivity(new Intent(AdminShuttles.this, AdminPassengers.class)));
        bulletinButton.setOnClickListener(v -> startActivity(new Intent(AdminShuttles.this, AdminBulletin.class)));
        addButton.setOnClickListener(v -> startActivity(new Intent(AdminShuttles.this, AddShuttle.class)));
    }
    private static final int EDIT_VEHICLE_REQUEST = 1;
    private static final int ADD_VEHICLE_REQUEST = 1;
    private int currentVehicleIndex = 0;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_VEHICLE_REQUEST) {
            if (resultCode == RESULT_OK) {
                // Fetch shuttles data again
                new FetchShuttlesTask().execute();
            }
        }

//        if(requestCode == ADD_VEHICLE_REQUEST) {
//            if(resultCode == RESULT_OK){
//                new FetchShuttlesTask().execute();
//            }
//        }
    }
    private void nextVehicle() {
        if (!vehicleList.isEmpty() && currentVehicleIndex < vehicleList.size() - 1) {
            currentVehicleIndex++;
            recyclerView.smoothScrollToPosition(currentVehicleIndex);
        } else {
            Toast.makeText(this, "No more vehicles to show", Toast.LENGTH_SHORT).show();
        }
    }

    private void previousVehicle() {
        if (!vehicleList.isEmpty() && currentVehicleIndex > 0) {
            currentVehicleIndex--;
            recyclerView.smoothScrollToPosition(currentVehicleIndex);
        } else {
            Toast.makeText(this, "No previous vehicles to show", Toast.LENGTH_SHORT).show();
        }
    }

    private class FetchShuttlesTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("https://c889-136-158-57-167.ngrok-free.app/api/shuttles/selectAllShuttles");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);            // JSON object with required parameters
                JSONObject requestData = new JSONObject();
                requestData.put("admin_username", "Arvi");

                // Write JSON data to the connection output stream
                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeBytes(requestData.toString());
                outputStream.flush();
                outputStream.close();

                // Get response code
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    return response.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    JSONObject apiResult = jsonResponse.getJSONObject("api_result");
                    JSONArray jsonArray = apiResult.getJSONArray("data");

                    // Clear existing vehicleList before adding new vehicles
                    vehicleList.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Vehicle vehicle = new Vehicle(
                                jsonObject.getString("name"),
                                jsonObject.getString("model"),
                                jsonObject.getString("plateNumber"),
                                jsonObject.getString("color"),
                                Integer.parseInt(jsonObject.getString("sitingCapacity")),
                                jsonObject.getString("coding")
//                                0
                        );
                        vehicleList.add(vehicle);

                        // Store the last fetched vehicle
                        if (i == jsonArray.length() - 1) {
                            lastFetchedVehicle = vehicle;
                        }
                    }
                    // Notify adapter only after adding all vehicles
                    vehicleAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AdminShuttles.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AdminShuttles.this, "Failed to fetch shuttles data", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public static class Vehicle {
        private String name;
        private String model;
        private String plateNumber;
        private String color;
        private int sittingCapacity;
        private String coding;
        private int imageResource;

        public Vehicle(String name, String model, String plateNumber, String color, int sittingCapacity, String coding) {
            this.name = name;
            this.model = model;
            this.plateNumber = plateNumber;
            this.color = color;
            this.sittingCapacity = sittingCapacity;
            this.coding = coding;
//            this.imageResource = imageResource;
        }

        // Getters
        public String getName() {
            return name;
        }

        public String getModel() {
            return model;
        }

        public String getPlateNumber() {
            return plateNumber;
        }

        public String getColor() {
            return color;
        }

        public int getSittingCapacity() {
            return sittingCapacity;
        }

        public String getCoding() {
            return coding;
        }

//        public int getImageResource() {
//            return imageResource;
//        }
    }

    public static class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

        private List<Vehicle> vehicleList;
        private Context context;

        public VehicleAdapter(Context context, List<Vehicle> vehicleList) {
            this.context = context;
            this.vehicleList = vehicleList;
        }

        @NonNull
        @Override
        public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_vehicle, parent, false);
            return new VehicleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
            Vehicle vehicle = vehicleList.get(position);
            holder.vehicleName.setText(vehicle.getName());
            holder.vehicleModel.setText("Model: " + vehicle.getModel());
            holder.plateNumber.setText("Plate Number: " + vehicle.getPlateNumber());
            holder.color.setText("Color: " + vehicle.getColor());
            holder.sittingCapacity.setText("Sitting Capacity: " + vehicle.getSittingCapacity());
            holder.coding.setText("Coding: " + vehicle.getCoding());
//            holder.vehicleImage.setImageResource(vehicle.getImageResource());

            // Set the click listener for the edit button
            holder.editButton.setOnClickListener(v -> editVehicle(position));
        }

        @Override
        public int getItemCount() {
            return vehicleList.size();
        }

        private void editVehicle(int position) {
            Vehicle vehicle = vehicleList.get(position);
            Intent intent = new Intent(context, EditVehicleActivity.class);
            intent.putExtra("name", vehicle.getName());
            intent.putExtra("model", vehicle.getModel());
            intent.putExtra("plate_number", vehicle.getPlateNumber());
            intent.putExtra("color", vehicle.getColor());
            intent.putExtra("sitting_capacity", vehicle.getSittingCapacity());
            intent.putExtra("coding", vehicle.getCoding());
//            intent.putExtra("image_resource", vehicle.getImageResource());
            context.startActivity(intent);
        }

        public static class VehicleViewHolder extends RecyclerView.ViewHolder {

            TextView vehicleName, vehicleModel, plateNumber, color, sittingCapacity, coding;
            ImageView vehicleImage;
            ImageButton editButton;

            public VehicleViewHolder(@NonNull View itemView) {
                super(itemView);
                vehicleName = itemView.findViewById(R.id.vehicle_name);
                vehicleModel = itemView.findViewById(R.id.vehicle_model);
                plateNumber = itemView.findViewById(R.id.plate_number);
                color = itemView.findViewById(R.id.color);
                sittingCapacity = itemView.findViewById(R.id.sitting_capacity);
                coding = itemView.findViewById(R.id.coding);
                vehicleImage = itemView.findViewById(R.id.vehicle_image);
                editButton = itemView.findViewById(R.id.edit_button);
            }
        }
    }
}