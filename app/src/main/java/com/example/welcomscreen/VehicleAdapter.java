package com.example.welcomscreen;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

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
        holder.vehicleImage.setImageResource(vehicle.getImageResource());

        // Set the click listener for the edit button
        holder.editButton.setOnClickListener(v -> {
            try {
                // Handle edit button click event
                editVehicle(position);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    private void editVehicle(int position) {
        // Handle the edit action, for example by starting a new activity
        Vehicle vehicle = vehicleList.get(position);
        Intent intent = new Intent(context, EditVehicleActivity.class);
        intent.putExtra("vehicle_name", vehicle.getName());
        intent.putExtra("vehicle_model", vehicle.getModel());
        intent.putExtra("plate_number", vehicle.getPlateNumber());
        intent.putExtra("color", vehicle.getColor());
        intent.putExtra("sitting_capacity", vehicle.getSittingCapacity());
        intent.putExtra("coding", vehicle.getCoding());
        intent.putExtra("image_resource", vehicle.getImageResource());
        context.startActivity(intent);
    }

    static class VehicleViewHolder extends RecyclerView.ViewHolder {

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
