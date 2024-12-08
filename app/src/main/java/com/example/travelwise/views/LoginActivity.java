package com.example.travelwise.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.travelwise.R;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private CheckBox rememberMeCheckBox;
    private SharedPreferences sharedPreferences;

    private DatabaseHelper databaseHelper;

    private static final String PREFS_NAME = "loginPrefs";
    private static final String EMAIL_KEY = "email";
    private static final String PASSWORD_KEY = "password";
    private static final String REMEMBER_ME_KEY = "remember_me";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check dark mode preference before setting content view
        SharedPreferences preferences = getSharedPreferences("app_preferences", MODE_PRIVATE);
        boolean isDarkModeEnabled = preferences.getBoolean("dark_mode_key", false);

        AppCompatDelegate.setDefaultNightMode(isDarkModeEnabled ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        // Apply system UI settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, systemBars.bottom);
            return insets;
        });


        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        rememberMeCheckBox = findViewById(R.id.rememberMe);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        databaseHelper = new DatabaseHelper(this);

        // Auto-fill email and password if "Remember Me" was checked
        if (sharedPreferences.getBoolean(REMEMBER_ME_KEY, false)) {
            emailEditText.setText(sharedPreferences.getString(EMAIL_KEY, ""));
            passwordEditText.setText(sharedPreferences.getString(PASSWORD_KEY, ""));
            rememberMeCheckBox.setChecked(true);
        }

        passwordEditText.setTransformationMethod(new PasswordTransformationMethod());

        setupEmailValidation();
        setupPasswordViewToggle();
        setupLoginButton();
        setupCreateAccountButton();
    }

    private void setupEmailValidation() {
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String email = s.toString().trim();
                if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    setEmailIconColor(R.color.red);
                } else {
                    setEmailIconColor(R.color.green);
                }
            }
        });

        emailEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && !isEmailValid()) {
                showToast("Please enter a valid email address.");
            }
        });
    }

    private void setEmailIconColor(int colorRes) {
        if (emailEditText.getCompoundDrawables()[2] != null) {
            emailEditText.getCompoundDrawables()[2].setColorFilter(
                    getResources().getColor(colorRes), PorterDuff.Mode.SRC_IN);
        }
    }

    private void setupPasswordViewToggle() {
        passwordEditText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                int drawableRight = 2;
                if (passwordEditText.getCompoundDrawables()[drawableRight] != null &&
                        event.getRawX() >= (passwordEditText.getRight() -
                                passwordEditText.getCompoundDrawables()[drawableRight].getBounds().width())) {
                    togglePasswordVisibility();
                    return true;
                }
            }
            return false;
        });
    }

    private void togglePasswordVisibility() {
        TransformationMethod currentMethod = passwordEditText.getTransformationMethod();
        passwordEditText.setTransformationMethod(currentMethod instanceof PasswordTransformationMethod ?
                null : new PasswordTransformationMethod());
        passwordEditText.setSelection(passwordEditText.length());
    }

    private void setupLoginButton() {
        findViewById(R.id.loginButton).setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (!isEmailValid()) {
                showToast("Please enter a valid email address.");
            } else if (!isPasswordValid()) {
                showToast("Password must be at least 6 characters long and contain letters, numbers, and symbols.");
            } else if (databaseHelper.checkUserCredentials(email, password)) {
                if (rememberMeCheckBox.isChecked()) {
                    sharedPreferences.edit()
                            .putString(EMAIL_KEY, email)
                            .putString(PASSWORD_KEY, password)
                            .putBoolean(REMEMBER_ME_KEY, true)
                            .apply();
                }
                goToDashboard(email);
            } else {
                showToast("Invalid email or password.");
            }
        });
    }

    private void setupCreateAccountButton() {
        findViewById(R.id.createAccountTextView).setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SigninActivity.class);
            startActivity(intent);
        });
    }

    private boolean isEmailValid() {
        String email = emailEditText.getText().toString().trim();
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid() {
        String password = passwordEditText.getText().toString().trim();
        return password.length() >= 6 &&
                (password.matches(".*[A-Za-z].*") ||
                        password.matches(".*\\d.*") ||
                        password.matches(".*[!@#$%^&*(),.?\":{}|<>].*"));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void goToDashboard(String email) {
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        intent.putExtra("USER_EMAIL", email);
        startActivity(intent);
        finish();
    }
}
