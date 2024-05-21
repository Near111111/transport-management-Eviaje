package com.example.welcomscreen;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    private static final float PROXIMITY_THRESHOLD = 50; // 50 meters

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Marker userLocationMarker;

    // Define the static waypoints (stops)
    private final LatLng[] waypointsQuezon = {
            new LatLng(14.737940, 121.127990), // Litex QC
            new LatLng(14.688430, 121.074530), // COA, Quezon City
            new LatLng(14.635410, 121.023560), // Q. Ave Quezon City
            new LatLng(14.602400, 121.013489), // SM Sta. Mesa
            new LatLng(14.582680, 120.997290), // Quirino Ave & E Zamora St
            new LatLng(14.583320, 120.984154) // Taft Avenue, Manila
    };

    private final LatLng[] waypointsManila = {
            new LatLng(14.656120, 120.995240), // Blumentrit
            new LatLng(14.577880, 121.067932), // University Of Santo Tomas Manila
            new LatLng(14.605330, 120.980240), // Doroteo Jose LRT Station PH
            new LatLng(14.598850, 120.980301) // Plaza Lacson
    };

    private List<Marker> waypointMarkers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Define location callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    updateLocation(location);
                }
            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Check for location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        mMap.setMyLocationEnabled(true);

        // Start location updates
        startLocationUpdates();

        // Show Quezon Group waypoints
        Button quezonGroupButton = findViewById(R.id.quezonGroupButton);
        Button manilaGroupButton = findViewById(R.id.manilaGroupButton);

        quezonGroupButton.setOnClickListener(v -> {
            clearWaypoints();
            addWaypoints(waypointsQuezon);
            quezonGroupButton.setEnabled(false); // Disable the button once the waypoints are added
            manilaGroupButton.setEnabled(true); // Enable the Manila button
        });

        // Show Manila Group waypoints
        manilaGroupButton.setOnClickListener(v -> {
            clearWaypoints();
            addWaypoints(waypointsManila);
            manilaGroupButton.setEnabled(false); // Disable the button once the waypoints are added
            quezonGroupButton.setEnabled(true); // Enable the Quezon button
        });
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000); // 5 seconds
        locationRequest.setFastestInterval(2000); // 2 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void updateLocation(Location location) {
        if (location == null) {
            return;
        }

        // Update user's location marker
        LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (userLocationMarker != null) {
            userLocationMarker.setPosition(userLatLng);
        } else {
            userLocationMarker = mMap.addMarker(new MarkerOptions().position(userLatLng).title("User Location"));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15));

        // Check and remove passed waypoints
        removePassedWaypoints(userLatLng);
    }

    private void addWaypoints(LatLng[] waypoints) {
        for (LatLng waypoint : waypoints) {
            Marker marker = mMap.addMarker(new MarkerOptions().position(waypoint).title("Stop"));
            waypointMarkers.add(marker);
        }
    }

    private void clearWaypoints() {
        for (Marker marker : waypointMarkers) {
            marker.remove();
        }
        waypointMarkers.clear();
    }

    private void removePassedWaypoints(LatLng userLatLng) {
        Iterator<Marker> iterator = waypointMarkers.iterator();
        while (iterator.hasNext()) {
            Marker marker = iterator.next();
            float[] results = new float[1];
            Location.distanceBetween(userLatLng.latitude, userLatLng.longitude, marker.getPosition().latitude, marker.getPosition().longitude, results);
            if (results[0] < PROXIMITY_THRESHOLD) {
                marker.remove();
                iterator.remove();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    onMapReady(mMap);
                }
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
