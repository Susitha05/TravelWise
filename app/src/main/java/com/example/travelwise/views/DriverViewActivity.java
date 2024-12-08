package com.example.travelwise.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelwise.R;
import com.example.travelwise.views.DatabaseHelper;
import com.example.travelwise.views.DriverAdapter;
import com.example.travelwise.models.Driver;

import java.util.ArrayList;
import java.util.List;

public class DriverViewActivity extends AppCompatActivity {

    private TextView passengerCount;
    private RecyclerView recyclerView;
    private DatabaseHelper dbHelper;
    private DriverAdapter driverAdapter;
    private Button showSeatsButton;
    private Button getDirectionsButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ensure the correct layout file is set
        setContentView(R.layout.activity_driver_view);

        // Initialize UI elements
        passengerCount = findViewById(R.id.passengerCount);
        recyclerView = findViewById(R.id.recycler_view_assigned_buses);
        showSeatsButton = findViewById(R.id.showSeats_button);  // Button for seat selection
        getDirectionsButton = findViewById(R.id.getDirections_button);  // Button for directions

        // Check if RecyclerView is null
        if (recyclerView == null) {
            throw new NullPointerException("RecyclerView not found! Check your XML layout.");
        }

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        driverAdapter = new DriverAdapter(new ArrayList<>());
        recyclerView.setAdapter(driverAdapter);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Load data
        loadPassengerCount();
        loadDrivers();

        // Set OnClickListener for showSeatsButton
        showSeatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start SeatSelectionActivity when the button is clicked
                Intent intent = new Intent(DriverViewActivity.this, SeatSelectionActivity.class);
                startActivity(intent);
            }
        });

        // Set OnClickListener for getDirectionsButton
        getDirectionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start MapsActivity when the button is clicked
                Intent intent = new Intent(DriverViewActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadPassengerCount() {
        try {
            int count = dbHelper.getPassengerCount(); // Get passenger count from DatabaseHelper
            passengerCount.setText(String.valueOf(count));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading passenger count", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDrivers() {
        try {
            // Use instance of DatabaseHelper to retrieve drivers
            List<Driver> drivers = dbHelper.getAllDrivers();
            driverAdapter.updateData(drivers); // Update RecyclerView with the drivers' data
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading drivers", Toast.LENGTH_SHORT).show();
        }
    }
}
