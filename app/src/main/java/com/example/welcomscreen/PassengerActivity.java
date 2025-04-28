package com.example.welcomscreen;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class PassengerActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker driverLocationMarker; // Marker para sa lokasyon ng driver

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Pagtanggap ng intent mula sa aplikasyon ng driver
        Intent intent = getIntent();
        if (intent != null) {
            double driverLatitude = intent.getDoubleExtra("driver_latitude", 0.0);
            double driverLongitude = intent.getDoubleExtra("driver_longitude", 0.0);
            if (driverLatitude != 0.0 && driverLongitude != 0.0) {
                LatLng driverLatLng = new LatLng(driverLatitude, driverLongitude);
                // Gumawa ng marker para sa lokasyon ng driver
                driverLocationMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("Driver Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(driverLatLng, 15));
            }
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add any additional customization or markers as needed
    }
}