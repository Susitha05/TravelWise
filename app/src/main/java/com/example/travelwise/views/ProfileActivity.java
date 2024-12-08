package com.example.travelwise.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.travelwise.views.DatabaseHelper;
import com.example.travelwise.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class ProfileActivity extends AppCompatActivity {

    private EditText nicEditText, nameEditText, emailEditText, mobileNoEditText, cityEditText, roleEditText;
    private Button editButton, saveChangesButton, changePicButton;
    private ImageView profilePicImageView;

    private DatabaseHelper databaseHelper;

    private Bitmap selectedProfilePic;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 101;
    private static final int GALLERY_REQUEST_CODE = 104;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        databaseHelper = new DatabaseHelper(this);

        // Initialize the views
        nicEditText = findViewById(R.id.nic);
        nameEditText = findViewById(R.id.user_name);
        emailEditText = findViewById(R.id.user_email);
        mobileNoEditText = findViewById(R.id.user_mobileno);
        roleEditText = findViewById(R.id.role);
        cityEditText = findViewById(R.id.user_city);
        editButton = findViewById(R.id.edit_button);
        saveChangesButton = findViewById(R.id.save_changes_button);
        changePicButton = findViewById(R.id.profile_pic_add_button);
        profilePicImageView = findViewById(R.id.profile_pic);

        String email = getIntent().getStringExtra("USER_EMAIL");

        loadUserData(email);

        nicEditText.setEnabled(false);
        roleEditText.setEnabled(false);
        emailEditText.setEnabled(false);


        setFieldsEditable(false);


        Bitmap savedProfilePic = loadProfilePicture();
        if (savedProfilePic != null) {
            profilePicImageView.setImageBitmap(savedProfilePic);
        }


        editButton.setOnClickListener(v -> {
            setFieldsEditable(true);
            nameEditText.requestFocus();
        });

        saveChangesButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String phone = mobileNoEditText.getText().toString();
            String city = cityEditText.getText().toString();

            boolean isUpdated = databaseHelper.updateUser(nicEditText.getText().toString(), name, phone, city);
            if (isUpdated) {
                Toast.makeText(ProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                setFieldsEditable(false);
            } else {
                Toast.makeText(ProfileActivity.this, "Failed to Update Profile", Toast.LENGTH_SHORT).show();
            }

            if (selectedProfilePic != null) {
                saveProfilePicture(selectedProfilePic);
            }
            finish();
        });


        changePicButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(ProfileActivity.this, android.Manifest.permission.CAMERA) != PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(ProfileActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                }, CAMERA_PERMISSION_REQUEST_CODE);
            } else {
                showImageSourceDialog();
            }
        });
    }

    private void loadUserData(String email) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query("Users", null, "email=?", new String[]{email}, null, null, null);

        if (cursor.moveToFirst()) {
            nicEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow("nic")));
            nameEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow("first_name")));
            emailEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            mobileNoEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            cityEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow("city")));
            roleEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow("user_type")));
        }
        cursor.close();
        db.close();
    }

    private void setFieldsEditable(boolean isEditable) {
        nameEditText.setEnabled(isEditable);
        cityEditText.setEnabled(isEditable);
        mobileNoEditText.setEnabled(isEditable);
    }

    private void showImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Profile Picture Source")
                .setItems(new String[]{"Take a Photo", "Choose from Gallery"}, (dialog, which) -> {
                    if (which == 0) {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_PERMISSION_REQUEST_CODE);
                    } else {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
                    }
                })
                .create()
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap bitmap = null;
            if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
                bitmap = (Bitmap) data.getExtras().get("data");
            } else if (requestCode == GALLERY_REQUEST_CODE) {
                if (data != null && data.getData() != null) {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (bitmap != null) {
                bitmap = getRoundedBitmap(bitmap); // Apply rounding
                profilePicImageView.setImageBitmap(bitmap);
                selectedProfilePic = bitmap;
            }

        }
    }

    private Bitmap getRoundedBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = Math.min(width, height);
        int x = (width - size) / 2;
        int y = (height - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(bitmap, x, y, size, size);
        Bitmap roundedBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(roundedBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        Rect rect = new Rect(0, 0, size, size);
        RectF rectF = new RectF(rect);

        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(squaredBitmap, rect, rect, paint);

        squaredBitmap.recycle();
        return roundedBitmap;
    }

    private void saveProfilePicture(Bitmap bitmap) {
        try {
            FileOutputStream fos = openFileOutput("profile_picture.png", MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap loadProfilePicture() {
        try {
            FileInputStream fis = openFileInput("profile_picture.png");
            return getRoundedBitmap(BitmapFactory.decodeStream(fis));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                showImageSourceDialog();
            } else {
                Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
