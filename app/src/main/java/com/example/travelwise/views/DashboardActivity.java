package com.example.travelwise.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.travelwise.R;
import com.example.travelwise.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends AppCompatActivity {

    private TextView dashnametxt;
    private ImageView settingsImageView;
    private CardView bookingHistoryCard, bookingCancelCard, addBusCard, viewBusCard, ViewTurnCard, busDetailsCard;
    private DatabaseHelper databaseHelper;
    private String userName, userPhone, userNIC, userType, userCity;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        String email = getIntent().getStringExtra("USER_EMAIL");
        if (email == null || email.isEmpty()) {
            Log.e("DashboardActivity", "Email is null or empty!");
            return; // Prevent further execution
        }

        databaseHelper = new DatabaseHelper(this);

        retrieveUserData(email);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainlayer), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, systemBars.bottom);
            return insets;
        });

        // Set user name
        dashnametxt = findViewById(R.id.dashusername);
        if (userName != null) {
            dashnametxt.setText(email);
        } else {
            dashnametxt.setText("," + email);
        }

        settingsImageView = findViewById(R.id.settings);
        settingsImageView.setOnClickListener(v -> {
            Intent settingsIntent = new Intent(DashboardActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        });


        bookingHistoryCard = findViewById(R.id.bookingHistory);
        bookingCancelCard = findViewById(R.id.bookingCancel);
        addBusCard = findViewById(R.id.AddBus);
        viewBusCard = findViewById(R.id.EditBusDetails);
        ViewTurnCard = findViewById(R.id.ViewTurn);
        busDetailsCard = findViewById(R.id.busDetails);

        // Show or hide CardViews based on userType
        if ("Passenger".equals(userType)) {
            bookingHistoryCard.setVisibility(CardView.VISIBLE);
            bookingCancelCard.setVisibility(CardView.VISIBLE);

            addBusCard.setVisibility(CardView.GONE);
            viewBusCard.setVisibility(CardView.GONE);
            ViewTurnCard.setVisibility(CardView.GONE);
            busDetailsCard.setVisibility(CardView.GONE);
        }
        else if("Bus Owner".equals(userType)){
            addBusCard.setVisibility(CardView.VISIBLE);
            viewBusCard.setVisibility(CardView.VISIBLE);

            bookingHistoryCard.setVisibility(CardView.GONE);
            bookingCancelCard.setVisibility(CardView.GONE);
            ViewTurnCard.setVisibility(CardView.GONE);
            busDetailsCard.setVisibility(CardView.GONE);
        }
        else if("Bus Driver".equals(userType)){
            ViewTurnCard.setVisibility(CardView.VISIBLE);
            busDetailsCard.setVisibility(CardView.VISIBLE);

            addBusCard.setVisibility(CardView.GONE);
            viewBusCard.setVisibility(CardView.GONE);
            bookingHistoryCard.setVisibility(CardView.GONE);
            bookingCancelCard.setVisibility(CardView.GONE);
        }

        // Set OnClickListener for bookingHistoryCard
        bookingHistoryCard.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, BookingHistoryActivity.class);
            intent.putExtra("USER_EMAIL",email);
            startActivity(intent);
        });

        bookingCancelCard.setOnClickListener(v -> {
            Toast.makeText(DashboardActivity.this, "You Havn't cancel a booking yet !", Toast.LENGTH_SHORT).show();
        });
        addBusCard.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, BusRegistrationActivity.class);
            intent.putExtra("USER_NAME", userName);
            startActivity(intent);
        });

        viewBusCard.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, DriverAssignActivity.class);
            startActivity(intent);
        });
        busDetailsCard.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, DriverViewActivity.class);

            startActivity(intent);
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_account) {
                Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
                intent.putExtra("USER_EMAIL", email);
                intent.putExtra("USER_NAME", userName);
                intent.putExtra("USER_PHONE", userPhone);
                intent.putExtra("USER_NIC", userNIC);
                intent.putExtra("USER_CITY", userCity);
                intent.putExtra("USER_TYPE", userType);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_bookings) {
                Intent intent = new Intent(DashboardActivity.this, SeatBookingActivity.class);
                intent.putExtra("USER_EMAIL",email);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_location) {
                Intent mapsIntent = new Intent(DashboardActivity.this, MapsActivity.class);
                startActivity(mapsIntent);
                return true;
            } else if (itemId == R.id.nav_logout) {
                logoutUser();
                return true;
            }
            return false;
        });
    }

    private void retrieveUserData(String email) {
        User user = databaseHelper.getUserDataByEmail(email);

        if (user != null) {
            userName = user.getFirstName();
            userPhone = user.getPhone();
            userNIC = user.getNic();
            userCity = user.getCity();
            userType = user.getUserType();
        } else {
            userName = "Guest";
            userPhone = "N/A";
            userNIC = "N/A";
            userCity = "N/A";
            userType = "N/A";
        }
    }

    private void logoutUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent loginIntent = new Intent(DashboardActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
}
