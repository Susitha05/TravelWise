package com.example.travelwise.views;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.travelwise.models.Driver;
import com.example.travelwise.models.Seat;
import com.example.travelwise.models.User; // Import the User class
import com.example.travelwise.models.seatnumber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TravelWise.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_USERS = "users";
    public static final String TABLE_BOOKING = "seat_bookings";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_FIRST_NAME = "first_name";
    private static final String COLUMN_LAST_NAME = "last_name";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_NIC = "nic";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_CITY = "city";
    private static final String COLUMN_USER_TYPE = "user_type";
    public static final String COLUMN_BUSNUMBER = "bus_number";
    public static final String COLUMN_SEAT_NUMBERS = "seat_numbers";
    public static final String COLUMN_DESTINATION = "destination";
    public static final String COLUMN_TRIP_DATE = "trip_date";

    public static final String BUS_TABLE = "Bus";
    public static final String COLUMN_BUS_ID = "BusID";
    public static final String COLUMN_BUS_LICENSE = "BusLicenseNo";
    public static final String COLUMN_BUS_MODEL = "BusModel";
    public static final String COLUMN_BUS_TYPE = "BusType";
    public static final String COLUMN_SEATING_CAPACITY = "SeatingCapacity";
    public static final String COLUMN_ROUTE_FROM = "RouteFrom";
    public static final String COLUMN_ROUTE_TO = "RouteTo";
    public static final String COLUMN_ARRIVAL_TIME = "ArrivalTime";
    public static final String COLUMN_DEPARTURE_TIME = "DepartureTime";

    public static final String DRIVER_TABLE = "Driver";
    public static final String COLUMN_DRIVER_ID = "DriverID";
    public static final String COLUMN_DRIVER_FIRST_NAME = "DriverFirstName"; // Linking to first_name in TABLE_USERS
    public static final String COLUMN_DRIVER_BUS_LICENSE = "BusLicense";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_FIRST_NAME + " TEXT, "
                + COLUMN_LAST_NAME + " TEXT, "
                + COLUMN_PHONE + " TEXT, "
                + COLUMN_NIC + " TEXT, "
                + COLUMN_EMAIL + " TEXT UNIQUE, "
                + COLUMN_PASSWORD + " TEXT, "
                + COLUMN_CITY + " TEXT, "
                + COLUMN_USER_TYPE + " TEXT"
                + ")";

        String SEAT_BOOKING = "CREATE TABLE " + TABLE_BOOKING + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_EMAIL + " TEXT, "
                + COLUMN_DESTINATION + " TEXT, "
                + COLUMN_BUSNUMBER + " TEXT, "
                + COLUMN_SEAT_NUMBERS + " TEXT, "
                + COLUMN_TRIP_DATE + " TEXT "
                + ")";
        String CREATE_BUS_TABLE = "CREATE TABLE " + BUS_TABLE + " ("
                + COLUMN_BUS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_BUS_LICENSE + " TEXT, "
                + COLUMN_BUS_MODEL + " TEXT, "
                + COLUMN_BUS_TYPE + " TEXT, "
                + COLUMN_SEATING_CAPACITY + " INTEGER, "
                + COLUMN_ROUTE_FROM + " TEXT, "
                + COLUMN_ROUTE_TO + " TEXT, "
                + COLUMN_ARRIVAL_TIME + " TEXT, "
                + COLUMN_DEPARTURE_TIME + " TEXT)";

        String CREATE_DRIVER_TABLE = "CREATE TABLE " + DRIVER_TABLE + " ("
                + COLUMN_DRIVER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DRIVER_FIRST_NAME + " TEXT, "
                + COLUMN_DRIVER_BUS_LICENSE + " TEXT, "
                + "FOREIGN KEY(" + COLUMN_DRIVER_FIRST_NAME + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_FIRST_NAME + "), "
                + "FOREIGN KEY(" + COLUMN_DRIVER_BUS_LICENSE + ") REFERENCES " + BUS_TABLE + "(" + COLUMN_BUS_LICENSE + ")"
                + ")";
        db.execSQL(SEAT_BOOKING);
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_DRIVER_TABLE);
        db.execSQL(CREATE_BUS_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKING);
        db.execSQL("DROP TABLE IF EXISTS " + BUS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DRIVER_TABLE);
        onCreate(db);
    }

    public boolean insertUser(String firstName, String lastName, String phone, String nic, String email, String password, String city, String userType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FIRST_NAME, firstName);
        values.put(COLUMN_LAST_NAME, lastName);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_NIC, nic);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_CITY, city);
        values.put(COLUMN_USER_TYPE, userType);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();

        return result != -1; // Return true if the insertion was successful
    }

    public boolean checkUserCredentials(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});
        boolean isValid = cursor.moveToFirst(); // True if a record exists
        cursor.close();
        db.close();
        return isValid;
    }
    /**
     * Retrieve user data by email.
     * @param email The email of the user to retrieve.
     * @return A User object containing the user's details, or null if not found.
     */
    public User getUserDataByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        User user = null;
        if (cursor.moveToFirst()) {
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE));
            String nic = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NIC));
            String city = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CITY));
            String userType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_TYPE));

            user = new User(firstName, lastName, phone, nic, email, city, userType);
        }

        cursor.close();
        db.close();

        return user;
    }

    public boolean updateUser(String nic, String name, String phone, String city) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FIRST_NAME, name);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_CITY, city);

        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_NIC + "=?", new String[]{nic});
        db.close();
        return rowsAffected > 0;
    }

    public boolean insertbooking(String email, String destination, String busnumber, String seats, String tripdate ){
        SQLiteDatabase sdb = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL,email);
        values.put(COLUMN_DESTINATION,destination);
        values.put(COLUMN_BUSNUMBER,busnumber);
        values.put(COLUMN_SEAT_NUMBERS,seats);
        values.put(COLUMN_TRIP_DATE,tripdate);
        long NewRow = sdb.insert(TABLE_BOOKING,null,values);
        if (NewRow != -1){
            return true;
        }else{
            return false;
        }
    }
    public Seat getBookings(String email){
        SQLiteDatabase sdb = this.getReadableDatabase();
        String query ="Select * from seat_bookings where email = ?";
        Cursor cursor = sdb.rawQuery(query, new String[]{email});
        Seat seat = null;

        if(cursor.moveToFirst()){
            String Bookid = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String Dates = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRIP_DATE));
            String location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESTINATION));
            String busnumber = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BUSNUMBER));
            String seatnumber = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SEAT_NUMBERS));
            seat = new Seat(Bookid, Dates, location, busnumber, seatnumber);
        }
        cursor.close();
        sdb.close();
        return seat;
    }
    public seatnumber getSeatNumbers(String busnum) {
        SQLiteDatabase sdb = this.getReadableDatabase();
        String query = "SELECT seat_numbers FROM seat_bookings WHERE bus_number = ?";
        Cursor cursor = sdb.rawQuery(query, new String[]{busnum});
        seatnumber seatNumbers = null;
        if (cursor.moveToFirst()) {
             String seatNum = cursor.getString(cursor.getColumnIndexOrThrow("seat_numbers"));
             String username = cursor.getString(cursor.getColumnIndexOrThrow("email"));
             seatNumbers= new seatnumber(seatNum,username);
        }
        cursor.close();
        sdb.close();
        return seatNumbers;
    }
    public boolean seatdel(String email) {
        SQLiteDatabase sdb = this.getWritableDatabase();
        String whereClause = "email = ?";
        String[] whereArgs = { email };
        int deletedRows = sdb.delete(TABLE_BOOKING, whereClause, whereArgs);
        sdb.close();
        return deletedRows > 0;
    }
    public boolean swapseat(String useremail,String busnumber,String Seat){
        SQLiteDatabase sdb = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SEAT_NUMBERS,Seat);

        long rowAffect= sdb.update(TABLE_BOOKING,values,COLUMN_EMAIL + "= ? and "+ COLUMN_BUSNUMBER +"=?", new String[]{useremail,busnumber});

        if(rowAffect != -1){
            return true;
        }else {
            return false;
        }
    }
    public boolean insertBus(String busLicenseNo, String busModel, String busType,
                             int seatingCapacity, String routeFrom, String routeTo,
                             String arrivalTime, String departureTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BUS_LICENSE, busLicenseNo);
        values.put(COLUMN_BUS_MODEL, busModel);
        values.put(COLUMN_BUS_TYPE, busType);
        values.put(COLUMN_SEATING_CAPACITY, seatingCapacity);
        values.put(COLUMN_ROUTE_FROM, routeFrom);
        values.put(COLUMN_ROUTE_TO, routeTo);
        values.put(COLUMN_ARRIVAL_TIME, arrivalTime);
        values.put(COLUMN_DEPARTURE_TIME, departureTime);

        long result = db.insert(BUS_TABLE, null, values);
        return result != -1; // Return true if insertion was successful
    }

    public List<String> getBusDriverDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> driverDetails = new ArrayList<>();

        String query = "SELECT " + COLUMN_FIRST_NAME + ", " + COLUMN_CITY + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_TYPE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{"Bus Driver"});

        if (cursor.moveToFirst()) {
            do {
                String firstName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME));
                String city = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CITY));

                driverDetails.add(firstName + "-" + city);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return driverDetails;
    }

    public boolean assignDriverToBus(String driverFirstName, String busLicense) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DRIVER_FIRST_NAME, driverFirstName);
        values.put(COLUMN_DRIVER_BUS_LICENSE, busLicense);

        long result = db.insert(DRIVER_TABLE, null, values);

        return result != -1;
    }


    public List<Map<String, String>> getBusDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Map<String, String>> busDetailsList = new ArrayList<>();

        String[] columns = {
                COLUMN_BUS_LICENSE,
                COLUMN_ROUTE_FROM,
                COLUMN_ROUTE_TO,
                COLUMN_ARRIVAL_TIME,
                COLUMN_DEPARTURE_TIME,
                COLUMN_BUS_TYPE
        };

        Cursor cursor = db.query(
                BUS_TABLE,
                columns,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Map<String, String> busDetails = new HashMap<>();
                busDetails.put("busLicenseNo", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BUS_LICENSE)));
                busDetails.put("routeFrom", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROUTE_FROM)));
                busDetails.put("routeTo", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROUTE_TO)));
                busDetails.put("arrivalTime", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ARRIVAL_TIME)));
                busDetails.put("departureTime", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEPARTURE_TIME)));
                busDetails.put("busType", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BUS_TYPE)));

                busDetailsList.add(busDetails);
            } while (cursor.moveToNext());

            cursor.close();
        }
        return busDetailsList;
    }

    public boolean deleteBusByLicense(String busLicense) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete("Bus", "busLicenseNo = ?", new String[]{busLicense});
        db.close();
        return rowsAffected > 0;
    }

    public List<Driver> getAllDrivers() {
        List<Driver> drivers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Driver", null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") Driver driver = new Driver(
                        cursor.getString(cursor.getColumnIndex(COLUMN_DRIVER_FIRST_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DRIVER_BUS_LICENSE))
                );
                drivers.add(driver);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return drivers;
    }
    public int getPassengerCount() {
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT seat_numbers FROM seat_bookings", null);

        if (cursor.moveToFirst()) {
            do {
                String seatNumbers = cursor.getString(0); // Get comma-separated seat numbers
                if (seatNumbers != null && !seatNumbers.isEmpty()) {
                    count += seatNumbers.split(",").length; // Count entries in seat numbers
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return count;
    }
}
