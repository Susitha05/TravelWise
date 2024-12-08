package com.example.travelwise.views;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travelwise.R;

import java.util.Arrays;
import java.util.List;

public class SigninActivity extends AppCompatActivity {

    private EditText firstNameEditText, lastNameEditText, phoneEditText, nicEditText, emailEditText, passwordEditText, retypePasswordEditText;
    private Spinner citySpinner, userTypeSpinner;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        nicEditText = findViewById(R.id.nicEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        retypePasswordEditText = findViewById(R.id.retypePasswordEditText);
        citySpinner = findViewById(R.id.citySpinner);
        userTypeSpinner = findViewById(R.id.userTypeSpinner);
        signupButton = findViewById(R.id.signupButton);

        setupCityDropdown();
        setupUserTypeDropdown();

        signupButton.setOnClickListener(v -> validateAndSubmit());
    }

    private void setupCityDropdown() {

        List<String> cities = Arrays.asList(
                "Select Your City", "Colombo", "Kandy", "Galle", "Jaffna", "Negombo",
                "Trincomalee", "Anuradhapura", "Batticaloa", "Nuwara Eliya",
                "Ratnapura", "Badulla", "Matara", "Kurunegala",
                "Polonnaruwa", "Hambantota", "Vavuniya"
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(adapter);
    }

    private void setupUserTypeDropdown() {
        List<String> userTypes = Arrays.asList(
                "Select User Role", "Passenger", "Bus Owner", "Bus Driver"
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner.setAdapter(adapter);
    }

    private void validateAndSubmit() {

        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String nic = nicEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String retypePassword = retypePasswordEditText.getText().toString().trim();
        String city = citySpinner.getSelectedItem().toString();
        String userType = userTypeSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(firstName)) {
            showToast("First name is required");
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            showToast("Last name is required");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            showToast("Phone number is required");
            return;
        }

        if (!isValidPhoneNumber(phone)) {
            showToast("Please enter a valid phone number");
            return;
        }

        if (TextUtils.isEmpty(nic)) {
            showToast("NIC is required");
            return;
        }

        if (TextUtils.isEmpty(email) || !isValidEmail(email)) {
            showToast("Please enter a valid email address");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            showToast("Password is required");
            return;
        }

        if (!isPasswordValid(password)) {
            showToast("Password must be at least 6 characters including letters, numbers and special characters.");
            return;
        }

        if (!password.equals(retypePassword)) {
            showToast("Passwords do not match");
            return;
        }

        if (city.equals("Select Your City")) {
            showToast("Please select a city");
            return;
        }

        if (userType.equals("Select User Role")) {
            showToast("Please select a user role");
            return;
        }

        submitSignupData();
    }

    private boolean isValidPhoneNumber(String phone) {
        String regex = "^((\\+94)\\d{9}|(0)\\d{9})$";
        return phone.matches(regex);
    }

    private boolean isValidEmail(String email) {
        return email.length() <= 254 && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {

        return password.length() >= 6 &&
                password.matches(".*[A-Za-z].*") ||
                password.matches(".*\\d.*") ||
                password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void submitSignupData() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String nic = nicEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String city = citySpinner.getSelectedItem().toString();
        String userType = userTypeSpinner.getSelectedItem().toString();

        boolean isInserted = databaseHelper.insertUser(firstName, lastName, phone, nic, email, password, city, userType);


        if (isInserted) {
            showToast("Sign up successful!");
            Intent intent = new Intent(SigninActivity.this,LoginActivity.class);
            startActivity(intent);
        } else {
            showToast("Sign up failed. Email may already be registered.");
        }
    }

}
