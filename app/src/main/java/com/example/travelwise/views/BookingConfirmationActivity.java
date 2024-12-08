package com.example.travelwise.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.travelwise.R;

public class BookingConfirmationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);

        String fromLocation = getIntent().getStringExtra("from_location");
        String toLocation = getIntent().getStringExtra("to_location");
        String date = getIntent().getStringExtra("date");
        String time = getIntent().getStringExtra("time");
        String selectedSeats = getIntent().getStringExtra("selectedSeats");
        String User_Email = getIntent().getStringExtra("User_Email");
        String checking = getIntent().getStringExtra("CHECKING");
        String busnumber = getIntent().getStringExtra("busnumber");

        String locations = fromLocation + " - " + toLocation;
        String dateTime = date + ", " + time;

        // Hardcoded virtual ticket details
        String bookingId = "#12345";
        String driverName = "John Doe";
        String driverContact = "+94 777 123 456";
        String busLicense = busnumber;
        String seatQuantity = selectedSeats != null ? String.valueOf(selectedSeats.split(",").length) : "0";
        String pricePerSeat = "LKR 500";
        String totalAmount = "LKR 500".equals(pricePerSeat) && selectedSeats != null
                ? "LKR " + (Integer.parseInt(seatQuantity) * 500)
                : "LKR 0";

        // Setting values in TextViews
        TextView bookingIdTextView = findViewById(R.id.booking_id);
        bookingIdTextView.setText(bookingId);

        TextView dateTimeTextView = findViewById(R.id.booking_date_time);
        dateTimeTextView.setText(dateTime);

        TextView locationsTextView = findViewById(R.id.booking_locations);
        locationsTextView.setText(locations);

        TextView driverNameTextView = findViewById(R.id.driver_name);
        driverNameTextView.setText(driverName);

        TextView driverContactTextView = findViewById(R.id.driver_contact);
        driverContactTextView.setText(driverContact);

        TextView busLicenseTextView = findViewById(R.id.bus_license);
        busLicenseTextView.setText(busLicense);

        TextView seatsInfoTextView = findViewById(R.id.seats_info);
        seatsInfoTextView.setText(selectedSeats != null ? selectedSeats : "No seats selected");

        TextView seatQtyTextView = findViewById(R.id.seat_qty);
        seatQtyTextView.setText(seatQuantity);

        TextView pricePerSeatTextView = findViewById(R.id.price_per_seat);
        pricePerSeatTextView.setText(pricePerSeat);

        TextView totalAmountTextView = findViewById(R.id.total_amount);
        totalAmountTextView.setText("Total Amount: " + totalAmount);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button confirmBookingButton = findViewById(R.id.btn_confirm_booking);


        confirmBookingButton.setOnClickListener(v -> {
            try {
                DatabaseHelper databaseHelper = new DatabaseHelper(this);
                String BusNumber = busnumber;
                if(checking == null) {
                    boolean isInsert = databaseHelper.insertbooking(User_Email, locations, BusNumber, seatQuantity, date);

                    if (isInsert) {
                        Toast.makeText(this, "Your Booking Successful", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Booking failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }else if(checking.equals("check")){
                    boolean updatedseat = databaseHelper.swapseat(User_Email,BusNumber,selectedSeats);
                    if(updatedseat == true){
                        Toast.makeText(this, "Your Booking Updated", Toast.LENGTH_SHORT).show();

                    }else {
                        Toast.makeText(this, "Update failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(this, "An error occurred: ", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "An error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            finish();
            Intent intent = new Intent(BookingConfirmationActivity.this, DashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            if (User_Email != null && !User_Email.isEmpty()) {
                intent.putExtra("USER_EMAIL", User_Email);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Cannot proceed to Dashboard. User Email is missing.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
