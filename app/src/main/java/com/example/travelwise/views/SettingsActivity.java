package com.example.travelwise.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.travelwise.R;

public class SettingsActivity extends AppCompatActivity {

    private Switch notificationsSwitch, darkModeSwitch, locationSwitch;
    private Button saveSettingsButton;
    private SharedPreferences sharedPreferences;

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("user_settings", MODE_PRIVATE);

        // Initialize views from the XML layout
        notificationsSwitch = findViewById(R.id.notifications_switch);
        darkModeSwitch = findViewById(R.id.dark_mode_switch);
        locationSwitch = findViewById(R.id.location_switch);
        saveSettingsButton = findViewById(R.id.save_settings_button);

        // Set default state for all switches (Off)
        notificationsSwitch.setChecked(false);
        darkModeSwitch.setChecked(false);
        locationSwitch.setChecked(false);

        // Load saved preferences
        loadSettings();

        // Save settings when the save button is clicked
        saveSettingsButton.setOnClickListener(v -> {
            saveSettings(); // Save the current settings to SharedPreferences
            Toast.makeText(SettingsActivity.this, "Settings Saved", Toast.LENGTH_SHORT).show();
            finish(); // Close this activity and return to the previous one
        });

        // Handle notifications switch change
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                requestPushNotificationPermission();
            } else {
                Toast.makeText(SettingsActivity.this, "Push Notifications Disabled", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle dark mode switch change
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                Toast.makeText(SettingsActivity.this, "Dark Mode Enabled", Toast.LENGTH_SHORT).show();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                Toast.makeText(SettingsActivity.this, "Light Mode Enabled", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle location switch change
        locationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                requestLocationPermissions();
            } else {
                disableLocation();
            }
        });
    }

    private void loadSettings() {
        // Load the settings from SharedPreferences
        boolean notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", false);
        boolean darkModeEnabled = sharedPreferences.getBoolean("dark_mode_enabled", false);
        boolean locationEnabled = sharedPreferences.getBoolean("location_enabled", false);

        // Set the views based on the loaded preferences
        notificationsSwitch.setChecked(notificationsEnabled);
        darkModeSwitch.setChecked(darkModeEnabled);
        locationSwitch.setChecked(locationEnabled);

        // Apply Dark Mode if the saved preference is true
        if (darkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void saveSettings() {
        // Save the current settings to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("notifications_enabled", notificationsSwitch.isChecked());
        editor.putBoolean("dark_mode_enabled", darkModeSwitch.isChecked());
        editor.putBoolean("location_enabled", locationSwitch.isChecked());
        editor.apply();
    }

    // Request notification permissions (for Android 13+)
    private void requestPushNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 (API 33) and above, check for POST_NOTIFICATIONS permission
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request permission if not granted
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE);
            } else {
                // Permission already granted
                Toast.makeText(this, "Push Notifications Enabled", Toast.LENGTH_SHORT).show();
            }
        } else {
            // For Android versions below Android 13, notifications are granted by default
            Toast.makeText(this, "Push Notifications Enabled", Toast.LENGTH_SHORT).show();
        }
    }

    // Request location permissions (ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION)
    private void requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // Request permissions if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted
            enableLocation();
        }
    }

    // Handle permission request result for location
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted
                enableLocation();
            } else {
                // Location permission denied
                Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Push Notification permission granted
                Toast.makeText(this, "Push Notifications Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                // Push Notification permission denied
                Toast.makeText(this, "Push Notifications Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Enable the location service if not already enabled
    private void enableLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // If location is not enabled, prompt user to enable it
            Toast.makeText(this, "Location is disabled, please enable it in settings", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    // Disable location service (you cannot disable the GPS from here, it must be done manually)
    private void disableLocation() {
        // Stop using location data (you can't disable the GPS from here, it must be done manually)
    }
}
