package com.example.travelwise.views;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelwise.R;
import com.example.travelwise.views.DatabaseHelper;
import com.example.travelwise.models.Bus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DriverAssignActivity extends AppCompatActivity {

    private RecyclerView recyclerViewAddedBuses;
    private BusAdapter busAdapter;
    private List<Bus> busList;

    private Spinner driverSpinner;
    private Button assignButton, deleteButton;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_assign); // Ensure this matches your layout XML file name

        dbHelper = new DatabaseHelper(this); // Initialize DBHelper

        // Initialize views
        recyclerViewAddedBuses = findViewById(R.id.recycler_view_added_buses);
        driverSpinner = findViewById(R.id.driver_spinner);
        assignButton = findViewById(R.id.assign_button);
        deleteButton = findViewById(R.id.delete_button);

        busList = new ArrayList<>();
        populateBusList(); // Load data from database
        busAdapter = new BusAdapter(busList);
        recyclerViewAddedBuses.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAddedBuses.setAdapter(busAdapter);

        ArrayAdapter<String> driverAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, getAvailableDrivers());
        driverAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        driverSpinner.setAdapter(driverAdapter);


        assignButton.setOnClickListener(view -> assignDriver());

        deleteButton.setOnClickListener(view -> deleteSelectedBus());
    }

    private void populateBusList() {

        List<Map<String, String>> busDetailsList = dbHelper.getBusDetails();

        for (Map<String, String> busDetails : busDetailsList) {
            Bus bus = new Bus(
                    busDetails.get("busLicenseNo"),
                    busDetails.get("routeFrom"),
                    busDetails.get("routeTo"),
                    busDetails.get("arrivalTime"),
                    busDetails.get("departureTime")
            );
            busList.add(bus);
        }
    }

    private List<String> getAvailableDrivers() {

        return dbHelper.getBusDriverDetails();
    }

    private void assignDriver() {
        int selectedPosition = busAdapter.getSelectedPosition();
        if (selectedPosition != -1) {
            Bus selectedBus = busList.get(selectedPosition);
            String selectedDriver = driverSpinner.getSelectedItem().toString();

            boolean isAssigned = dbHelper.assignDriverToBus(selectedDriver, selectedBus.getBusLicense());

            if (isAssigned) {
                Toast.makeText(this, "Driver " + selectedDriver + " assigned to bus " + selectedBus.getBusLicense(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to assign driver to bus!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please select a bus to assign a driver!", Toast.LENGTH_SHORT).show();
        }
    }


    private void deleteSelectedBus() {
        int selectedPosition = busAdapter.getSelectedPosition();
        if (selectedPosition != -1) {
            Bus selectedBus = busList.get(selectedPosition);

            boolean isDeleted = dbHelper.deleteBusByLicense(selectedBus.getBusLicense());

            if (isDeleted) {

                busList.remove(selectedPosition);
                busAdapter.notifyItemRemoved(selectedPosition);  // Notify adapter about item removal
                Toast.makeText(this, "Bus deleted successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to delete the bus!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please select a bus to delete!", Toast.LENGTH_SHORT).show();
        }
    }
}
