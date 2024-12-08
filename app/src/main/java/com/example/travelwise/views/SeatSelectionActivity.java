package com.example.travelwise.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.travelwise.R;
import com.example.travelwise.models.seatnumber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SeatSelectionActivity extends AppCompatActivity {
    private int primaryColor;
    private int reservedSeatColor;
    private int defaultColor;
    private int selectedTextColor;
    private int defaultTextColor;
    private TextView seatsSelectedTextView;
    private int selectedSeatCount = 0;
    private String seatstot;
    DatabaseHelper dbconnection = new DatabaseHelper(this);


    // List to hold references to seat card views
    private List<CardView> seatCardViews = new ArrayList<>();

    // List to track selected seats
    private List<String> selectedSeats = new ArrayList<>();

    // Reserved seat IDs
    private final List<Integer> reservedSeats = List.of(R.id.seat2, R.id.seat5);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection);

        // Fetch colors from colors.xml
        primaryColor = getResources().getColor(R.color.primaryColor, null);
        reservedSeatColor = getResources().getColor(R.color.reservedSeatColor, null);
        defaultColor = getResources().getColor(R.color.white, null);
        selectedTextColor = getResources().getColor(R.color.white, null);
        defaultTextColor = getResources().getColor(R.color.black, null);
        seatsSelectedTextView = findViewById(R.id.seats_selected);

        String fromLocation = getIntent().getStringExtra("FROM_LOCATION");
        String toLocation = getIntent().getStringExtra("TO_LOCATION");
        String date = getIntent().getStringExtra("DATE");
        String time = getIntent().getStringExtra("TIME");
        String User_Email = getIntent().getStringExtra("User_Email");
        String busnumber = getIntent().getStringExtra("busnumber");
        for (int i = 1; i <= 30; i++) {
            String seatId = "seat" + i;
            int resId = getResources().getIdentifier(seatId, "id", getPackageName());
            CardView seat = findViewById(resId);
            seatCardViews.add(seat);

            // Set reserved seats as not clickable and color them
            if (reservedSeats.contains(resId)) {
                seat.setCardBackgroundColor(reservedSeatColor);
                seat.setClickable(false);
            } else {
                // Set default color and click listener for selectable seats
                seat.setCardBackgroundColor(defaultColor);
                seat.setOnClickListener(this::onSeatClick);
            }
        }
        findViewById(R.id.confirm_button).setOnClickListener(v -> {
            // Create an Intent to start BookingConfirmationActivity
            String checking = null;
            checking = getIntent().getStringExtra("CHECKING");
            Intent bookingIntent = new Intent(SeatSelectionActivity.this, BookingConfirmationActivity.class);
            bookingIntent.putExtra("CHECKING",checking);
            // Build a string with the selected seats
            StringBuilder selectedSeatsString = new StringBuilder();
            for (String seatNumber : selectedSeats) {
                selectedSeatsString.append(seatNumber).append(",");
            }

            if (selectedSeatsString.length() > 0) {
                selectedSeatsString.deleteCharAt(selectedSeatsString.length() - 1);
            }
            bookingIntent.putExtra("selectedSeats", selectedSeatsString.toString());
            bookingIntent.putExtra("from_location", fromLocation);
            bookingIntent.putExtra("to_location", toLocation);
            bookingIntent.putExtra("date", date);
            bookingIntent.putExtra("time", time);
            bookingIntent.putExtra("User_Email", User_Email);
            bookingIntent.putExtra("busnumber",busnumber);


            String selectseat = selectedSeatsString.toString();
            String Destination = fromLocation + toLocation;
            startActivity(bookingIntent);
        });
    }

    private void onSeatClick(View view) {
        CardView seat = (CardView) view;
        TextView seatText = (TextView) seat.getChildAt(0);
        String seatNumber = seatText.getText().toString();

        if (selectedSeats.contains(seatNumber)) {
            // Deselect the seat
            seat.setCardBackgroundColor(defaultColor);
            seatText.setTextColor(defaultTextColor);
            selectedSeats.remove(seatNumber);
            selectedSeatCount--;
        } else {
            // Select the seat
            seat.setCardBackgroundColor(primaryColor);
            seatText.setTextColor(selectedTextColor);
            selectedSeats.add(seatNumber);
            selectedSeatCount++;
        }

        updateSeatCount();
    }

    private void updateSeatCount() {
        seatsSelectedTextView.setText("Seats Selected: " + selectedSeatCount);
    }
}
