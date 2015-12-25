package hertz.hertz.helpers;

import com.firebase.client.Firebase;
import com.firebase.geofire.GeoFire;

import hertz.hertz.services.GPSTrackerService;

/**
 * Created by rsbulanon on 11/11/15.
 */
public class AppConstants {

    public static final String BASE_URL = "https://hertz.firebaseio.com/";
    public static Firebase FIREBASE = null;
    public static GeoFire GEOFIRE = null;
    public static GPSTrackerService GPS_TRACKER = null;

    public static final String PARSE_APP_ID = "owxkS2NUrby5SC31HJPwXC8WrnJh4FniMVcuS0Of";
    public static final String PARSE_CLIENT_KEY = "ysZnHImfR08TMzgzFpUUNOnwBVL3Bzvi1yLfWYbZ";

    /** end points */
    public static final String BOOKINGS = "Bookings";

    /** device's current location */
    public static double LATITUDE;
    public static double LONGITUDE;

    /** warning messages */
    public static final String WARN_FIELD_REQUIRED = "This field is required!";
    public static final String WARN_INVALID_PASSENGER_COUNT = "Passenger count must be greater than zero";
    public static final String WARN_TOO_MANY_PASSENGER_COUNT = "Too many passenger count, seating capacity only limited to 20";
    public static final String WARN_FETCHING = "Currently fetching data, Please try again after a few seconds!";
    public static final String WARN_INVALID_EMAIL_FORMAT = "Invalid email format!";
    public static final String WARN_PLEASE_ENTER_YOUR_MESSAGE = "Please enter your message";
    public static final String WARN_SELECT_CAR_IMAGE = "Please select the car model's image";

    /** loading messages */
    public static final String LOAD_CREATE_ACCOUNT = "Creating your account, Please wait";
    public static final String LOAD_LOGIN = "Authenticating your credentials, Please wait";
    public static final String LOAD_BOOKING_INFO = "Getting booking info, Please wait...";
    public static final String LOAD_CREATING_BOOKING = "Creating booking, Please wait...";
    public static final String LOAD_CREATE_CAR = "Creating new car record, Please wait...";
    public static final String LOAD_FETCH_CAR = "Fetching car records, Please wait...";
    public static final String LOAD_DELETE_CAR = "Deleting car record, Please wait...";

    /** error messages */
    public static final String ERR_CONNECTION = "Connection error, Please check your network connection and try again!";
    public static final String ERR_LOGIN = "Either email or password is incorrect!";
    public static final String ERR_CREATE_ACCOUNT = "Sorry, but your email address was already taken!";
    public static final String ERR_EMAIL_NOT_VERIFIED = "Please validate your email address first to proceed!";
    public static final String ERR_CREATE_BOOKING = "Failed to create your booking ,Please try again!";
    public static final String ERR_GET_BOOKING_INFO = "Failed to get booking info,Please try again!";

    /** success messages */
    public static final String OK_ACCOUNT_CREATED = "Congratulations, Your account was successfully created"+
            ", Please do validate your email address!";
    public static final String OK_BOOKING_CREATED = "Congratulations, Your booking was successfully created";
    public static final String OK_NEW_CAR_ADDED = "Congratulations, New car record was successfully created";
    public static final String OK_CAR_DELETED = "Car record was successfully deleted!";
    public static final String OK_CAR_UPDATED = "Car record was successfully updated!";


    /** default values */
    public static final String DEFAULT_STATUS = "Awaiting";
    public static Double SRC_LATITUDE = 14.5195461;
    public static Double SRC_LONGITUDE = 121.0137973;

    /** map circle */
    public static final double RADIUS_IN_METERS = 1500.0; //equivalent to 1.5 kilometers
    public static final int MAP_CIRCLE_STROKE_COLOR = 0xff000000; //black outline
    public static final int MAP_CIRCLE_SHADE_COLOR = 0x44000000; //opaque black fill

    public static String FULL_NAME = "";

    public static final int REQUEST_ENABLE_GPS = 1;

}

