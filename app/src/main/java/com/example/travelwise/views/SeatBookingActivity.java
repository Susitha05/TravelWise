package com.example.travelwise.views;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelwise.R;
import com.example.travelwise.models.Bus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class SeatBookingActivity extends AppCompatActivity {

    private EditText selectDate;
    private EditText selectTime;
    private RecyclerView busScheduleRecyclerView;
    private List<String[]> busSchedules; // Using List of String arrays for hardcoded data
    private BusScheduleAdapter busScheduleAdapter;
    private EditText fromLocationEditText;
    private EditText toLocationEditText;
    private View showMapButton;
    private View proceedSeatSelectionButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_booking);

        String UserEmail = getIntent().getStringExtra("USER_EMAIL");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        selectDate = findViewById(R.id.select_date);
        selectTime = findViewById(R.id.select_time);
        busScheduleRecyclerView = findViewById(R.id.bus_timetable_recyclerview);
        fromLocationEditText = findViewById(R.id.from_location);
        toLocationEditText = findViewById(R.id.to_location);
        showMapButton = findViewById(R.id.show_map_button);
        proceedSeatSelectionButton = findViewById(R.id.btn_proceed_seat_selection);

        updateButtonStates();

        selectDate.setOnClickListener(v -> openDatePicker());

        selectTime.setOnClickListener(v -> openTimePicker());

        setupBusScheduleRecyclerView();

        showMapButton.setOnClickListener(v -> {
            String fromLocation = fromLocationEditText.getText().toString();
            String toLocation = toLocationEditText.getText().toString();

            Toast.makeText(this, "Opening Map View...", Toast.LENGTH_SHORT).show();
            Intent webViewIntent = new Intent(SeatBookingActivity.this, MapsActivity.class);
            webViewIntent.putExtra("FROM_LOCATION", fromLocation);
            webViewIntent.putExtra("TO_LOCATION", toLocation);
            startActivity(webViewIntent);
        });

        proceedSeatSelectionButton.setOnClickListener(v -> {
            String fromLocation = fromLocationEditText.getText().toString().trim();
            String toLocation = toLocationEditText.getText().toString().trim();
            String date = selectDate.getText().toString().trim();
            String time = selectTime.getText().toString().trim();

            Intent seatSelectionIntent = new Intent(SeatBookingActivity.this, SeatSelectionActivity.class);
            seatSelectionIntent.putExtra("FROM_LOCATION", fromLocation);
            seatSelectionIntent.putExtra("TO_LOCATION", toLocation);
            seatSelectionIntent.putExtra("DATE", date);
            seatSelectionIntent.putExtra("TIME", time);
            seatSelectionIntent.putExtra("User_Email", UserEmail);
            seatSelectionIntent.putExtra("busnumber", busnumber);
            startActivity(seatSelectionIntent);
        });

        fromLocationEditText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                updateButtonStates();
            }
        });

        toLocationEditText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                updateButtonStates();
            }
        });

        selectDate.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                updateButtonStates();
            }
        });

        selectTime.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                updateButtonStates();
            }
        });
    }

    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String formattedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
            selectDate.setText(formattedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void openTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, selectedHour, selectedMinute) -> {
            String formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
            selectTime.setText(formattedTime);
        }, hour, minute, false);

        timePickerDialog.show();
    }
    private String busnumber,startime,endtime,bustype;

    private void setupBusScheduleRecyclerView() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        List<Map<String, String>> busDetailsList = databaseHelper.getBusDetails();

        busSchedules = new ArrayList<>();

        if (!busDetailsList.isEmpty()) {
            for (Map<String, String> busDetails : busDetailsList) {
                String busnumber = busDetails.get("busLicenseNo");
                String startime = busDetails.get("arrivalTime");
                String endtime = busDetails.get("departureTime");
                String bustype = busDetails.get("bustype");

                // Log details for debugging
                Log.d("BusDetails", "Bus License: " + busnumber);
                Log.d("BusDetails", "Arrival Time: " + startime);
                Log.d("BusDetails", "Departure Time: " + endtime);
                Log.d("BusDetails", "Bus Type: " + bustype);

                busSchedules.add(new String[]{busnumber, startime, endtime, bustype});
            }
        } else {
            Log.d("BusDetails", "No bus details found in the database.");
        }

        busScheduleAdapter = new BusScheduleAdapter(busSchedules);
        busScheduleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        busScheduleRecyclerView.setAdapter(busScheduleAdapter);
    }


    private void updateButtonStates() {
        boolean areLocationsValid = !fromLocationEditText.getText().toString().isEmpty() &&
                !toLocationEditText.getText().toString().isEmpty();
        showMapButton.setEnabled(areLocationsValid);

        boolean areAllFieldsValid = !selectDate.getText().toString().isEmpty() &&
                !selectTime.getText().toString().isEmpty() &&
                areLocationsValid;
        proceedSeatSelectionButton.setEnabled(areAllFieldsValid);
    }

    private static class SimpleTextWatcher implements android.text.TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(android.text.Editable s) {}
    }

    private class BusScheduleAdapter extends RecyclerView.Adapter<BusScheduleAdapter.BusScheduleViewHolder> {
        private final List<String[]> schedules;

        public BusScheduleAdapter(List<String[]> schedules) {
            this.schedules = schedules;
        }

        @NonNull
        @Override
        public BusScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.bus_schedule_item, parent, false);
            return new BusScheduleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BusScheduleViewHolder holder, int position) {
            String[] schedule = schedules.get(position);

            // Set the data
            holder.busRoute.setText(schedule[0]);
            holder.departureTime.setText(schedule[1]);
            holder.arrivalTime.setText(schedule[2]);
            holder.busType.setText(schedule[3]);

            // Add click listener for the card view
            holder.itemView.setOnClickListener(v -> {
                String selectedBusNumber = schedule[0]; // Bus number is the first element
                Toast.makeText(holder.itemView.getContext(), "Bus Number: " + selectedBusNumber, Toast.LENGTH_SHORT).show();

                // Pass the selected bus number to the SeatBookingActivity
                busnumber = selectedBusNumber;

                // Perform additional actions if needed
                Log.d("SelectedBus", "Bus Number Clicked: " + selectedBusNumber);
            });
        }

        @Override
        public int getItemCount() {
            return schedules.size();
        }

        public class BusScheduleViewHolder extends RecyclerView.ViewHolder {
            TextView busRoute, departureTime, arrivalTime, busType;

            public BusScheduleViewHolder(@NonNull View itemView) {
                super(itemView);
                busRoute = itemView.findViewById(R.id.route);
                departureTime = itemView.findViewById(R.id.departure_time);
                arrivalTime = itemView.findViewById(R.id.arrival_time);
                busType = itemView.findViewById(R.id.details);
            }
        }
    }

}
