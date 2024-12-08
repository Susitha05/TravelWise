package com.example.travelwise.views;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travelwise.R;

import java.util.Calendar;

public class BusRegistrationActivity extends AppCompatActivity {

    private EditText edtBusLicenseNo, edtBusModel, edtSeatingCapacity, edtArrivalTime, edtDepartureTime;
    private Spinner spnBusType, spnRouteFrom, spnRouteTo;
    private Button btnRegisterBus;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_registration);

        dbHelper = new DatabaseHelper(this);

        // Initialize views
        edtBusLicenseNo = findViewById(R.id.busplate);
        edtBusModel = findViewById(R.id.busmodel);
        edtSeatingCapacity = findViewById(R.id.no_of_seats);
        edtArrivalTime = findViewById(R.id.Arrival_time);
        edtDepartureTime = findViewById(R.id.Departure_time);
        spnBusType = findViewById(R.id.busTypeSpinner);
        spnRouteFrom = findViewById(R.id.route_from);
        spnRouteTo = findViewById(R.id.route_to);
        btnRegisterBus = findViewById(R.id.save_button);

        // Set up TimePickers
        edtArrivalTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(edtArrivalTime);
            }
        });

        edtDepartureTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(edtDepartureTime);
            }
        });

        btnRegisterBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerBus();
            }
        });
    }

    private void showTimePicker(final EditText timeField) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                        String formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                        timeField.setText(formattedTime);
                    }
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void registerBus() {
        String busLicenseNo = edtBusLicenseNo.getText().toString().trim();
        String busModel = edtBusModel.getText().toString().trim();
        String busType = spnBusType.getSelectedItem().toString();
        String routeFrom = spnRouteFrom.getSelectedItem().toString();
        String routeTo = spnRouteTo.getSelectedItem().toString();
        String arrivalTime = edtArrivalTime.getText().toString().trim();
        String departureTime = edtDepartureTime.getText().toString().trim();
        String seatingCapacityStr = edtSeatingCapacity.getText().toString().trim();

        if (busLicenseNo.isEmpty() || busModel.isEmpty() || seatingCapacityStr.isEmpty()
                || arrivalTime.isEmpty() || departureTime.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int seatingCapacity;
        try {
            seatingCapacity = Integer.parseInt(seatingCapacityStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid seating capacity", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isInserted = dbHelper.insertBus(busLicenseNo, busModel, busType, seatingCapacity,
                routeFrom, routeTo, arrivalTime, departureTime);

        if (isInserted) {
            Toast.makeText(this, "Bus registered successfully!", Toast.LENGTH_SHORT).show();
            clearFields();
            Intent intent = new Intent(BusRegistrationActivity.this,DashboardActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Error registering bus!", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        edtBusLicenseNo.setText("");
        edtBusModel.setText("");
        edtSeatingCapacity.setText("");
        edtArrivalTime.setText("");
        edtDepartureTime.setText("");
        spnBusType.setSelection(0);
        spnRouteFrom.setSelection(0);
        spnRouteTo.setSelection(0);
    }
}
