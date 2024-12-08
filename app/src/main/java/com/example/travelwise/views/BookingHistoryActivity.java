package com.example.travelwise.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelwise.R;
import com.example.travelwise.models.Seat;
import com.example.travelwise.views.SeatSelectionActivity;

import java.util.ArrayList;
import java.util.List;

public class BookingHistoryActivity extends AppCompatActivity {

    private RecyclerView bookingHistoryRecyclerView;
    private BookingHistoryAdapter adapter;
    private List<BookingHistoryItem> bookingHistoryList;
    private String Bookid, date,location,seatnumber,email,busnumber;
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);
        email = getIntent().getStringExtra("USER_EMAIL");
        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "No email provided for booking history.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // Initialize RecyclerView
        bookingHistoryRecyclerView = findViewById(R.id.booking_history_recyclerview);
        bookingHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize data
        bookingHistoryList = new ArrayList<>();
        loadBookingHistoryData();

        // Set up the adapter
        adapter = new BookingHistoryAdapter(bookingHistoryList);
        bookingHistoryRecyclerView.setAdapter(adapter);
    }
    private void loadBookingHistoryData() {
        Seat seat = databaseHelper.getBookings(email);
        if (seat != null) {
            Bookid = seat.getBookid();
            date = seat.getDate();
            location = seat.getLocation();
            seatnumber = seat.getSeatNumber();
            busnumber = seat.getBusNumber();
            double total = seats() * 500;
            bookingHistoryList.add(new BookingHistoryItem(Bookid, date, location, seatnumber, seats(), total));
        } else {
            Toast.makeText(this, "No bookings found for this email.", Toast.LENGTH_SHORT).show();
        }
    }
    // Inner class representing a booking history item
    private static class BookingHistoryItem {
        private String bookingId;
        private String bookingDateTime;
        private String bookingLocations;
        private String seatsInfo;
        private int seatQty;
        private double totalAmount;

        public BookingHistoryItem(String bookingId, String bookingDateTime, String bookingLocations,
                                  String seatsInfo, int seatQty, double totalAmount) {
            this.bookingId = bookingId;
            this.bookingDateTime = bookingDateTime;
            this.bookingLocations = bookingLocations;
            this.seatsInfo = seatsInfo;
            this.seatQty = seatQty;
            this.totalAmount = totalAmount;
        }

        public String getBookingId() {
            return bookingId;
        }

        public String getBookingDateTime() {
            return bookingDateTime;
        }

        public String getBookingLocations() {
            return bookingLocations;
        }

        public String getSeatsInfo() {
            return seatsInfo;
        }

        public int getSeatQty() {
            return seatQty;
        }

        public double getTotalAmount() {
            return totalAmount;
        }
    }

    private class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.BookingHistoryViewHolder> {

        private List<BookingHistoryItem> bookingHistoryList;

        public BookingHistoryAdapter(List<BookingHistoryItem> bookingHistoryList) {
            this.bookingHistoryList = bookingHistoryList;
        }

        @NonNull
        @Override
        public BookingHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.booking_history_item, parent, false);
            return new BookingHistoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BookingHistoryViewHolder holder, int position) {
            BookingHistoryItem item = bookingHistoryList.get(position);

            holder.bookingIdTextView.setText(item.getBookingId());
            holder.bookingDateTimeTextView.setText(item.getBookingDateTime());
            holder.bookingLocationsTextView.setText("Route: " + item.getBookingLocations());
            holder.seatsInfoTextView.setText("Seats: " + item.getSeatsInfo());
            holder.seatQtyTextView.setText("Seat Qty: " + item.getSeatQty());
            holder.totalAmountTextView.setText("Total: LKR " + item.getTotalAmount());

            holder.cancelBookingButton.setOnClickListener(v -> {

                boolean isDeleted = databaseHelper.seatdel(email);
                if (isDeleted) {
                    System.out.println("Record deleted successfully.");
                } else {
                    System.out.println("No record found for the given email.");
                }
                refreshActivity();
            });

            holder.swapSeatsButton.setOnClickListener(v -> {
                String checking = "check";
                Intent intent = new Intent(BookingHistoryActivity.this, SeatSelectionActivity.class);
                intent.putExtra("USER_EMAIL", email);
                intent.putExtra("busnumber", busnumber);
                intent.putExtra("Date", date);
                intent.putExtra("FROM_LOCATION", location);
                intent.putExtra("CHECKING", checking);
                startActivity(intent);
            });
        }
        @Override
        public int getItemCount() {
            return bookingHistoryList.size();
        }

        public class BookingHistoryViewHolder extends RecyclerView.ViewHolder {

            TextView bookingIdTextView, bookingDateTimeTextView, bookingLocationsTextView,
                    seatsInfoTextView, seatQtyTextView, totalAmountTextView;
            Button cancelBookingButton, swapSeatsButton;

            public BookingHistoryViewHolder(@NonNull View itemView) {
                super(itemView);
                bookingIdTextView = itemView.findViewById(R.id.booking_id);
                bookingDateTimeTextView = itemView.findViewById(R.id.booking_date_time);
                bookingLocationsTextView = itemView.findViewById(R.id.booking_locations);
                seatsInfoTextView = itemView.findViewById(R.id.seats_info);
                seatQtyTextView = itemView.findViewById(R.id.seat_qty);
                totalAmountTextView = itemView.findViewById(R.id.total_amount);
                cancelBookingButton = itemView.findViewById(R.id.cancel_booking);
                swapSeatsButton = itemView.findViewById(R.id.swap_seats);
            }
        }
    }

    private void refreshActivity() {
        finish();
        startActivity(getIntent());
    }
    public int seats(){
        String input = seatnumber;
        String[] items = input.split(",");
        // Trim whitespace and count
        int count = 0;
        for (String item : items) {
            if (!item.trim().isEmpty()) {
                count++;
            }
        }
        return count;
    }
}
