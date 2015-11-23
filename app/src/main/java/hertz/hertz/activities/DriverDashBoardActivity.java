package hertz.hertz.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.firebase.client.FirebaseError;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.HashMap;

import hertz.hertz.R;
import hertz.hertz.fragments.BookingInfoDialogFragment;
import hertz.hertz.helpers.AppConstants;
import hertz.hertz.model.AvailableDriver;
import hertz.hertz.services.GPSTrackerService;

/**
 * Created by rsbulanon on 11/17/15.
 */
public class DriverDashBoardActivity extends BaseActivity implements OnMapReadyCallback ,
                                                                    GoogleMap.OnMarkerClickListener {

    private GoogleMap googleMap;
    private LatLng latLng = new LatLng(0,0);
    private GeoQuery geoQuery;
    private HashMap<String,Marker> markers = new HashMap<>();
    private Circle circle;
    private Marker yourMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_dashboard);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Log.d("parse", "on create");
        ParsePush.subscribeInBackground("Driver002", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("parse", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("parse", "failed to subscribe for push", e);
                }
            }
        });
        Log.d("parse", "is GPS Enabled --> " + isGPSEnabled());
        if (!isGPSEnabled()) {
            enableGPS();
        }

        /** mark driver as available */
/*        AvailableDriver driver = new AvailableDriver("Driver0002","Maud","Flanders","DEF 456",
                "09321622825",true);
        AppConstants.GEOFIRE.getFirebase().child("AvailableDriver").child("Driver002").setValue(driver);*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, GPSTrackerService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            GPSTrackerService.ServiceBinder binder = (GPSTrackerService.ServiceBinder) iBinder;
            if (AppConstants.GPS_TRACKER == null) {
                AppConstants.GPS_TRACKER = binder.getService();
                if (AppConstants.GPS_TRACKER.onTrackGPSListener == null) {
                    AppConstants.GPS_TRACKER.onTrackGPSListener = new GPSTrackerService.OnTrackGPSListener() {
                        @Override
                        public void onLocationChanged(double latitude, double longitude) {
                            Log.d("gps", "lat --> " + latitude + "  long --> " + longitude);
                            latLng = new LatLng(latitude,longitude);
                            if (googleMap != null) {
                                moveCamera(googleMap,latLng);
                            }
                            AppConstants.GEOFIRE.setLocation("AvailDriver002",new GeoLocation(latitude,longitude));
                        }

                        @Override
                        public void onGetLocationFailed(String provider) {
                            Log.d("gps","failed to get using ---> " + provider);
                        }

                        @Override
                        public void onGetLocationException(Exception e) {
                            Log.d("gps","Exception --> " + e.toString());
                        }
                    };
                    AppConstants.GPS_TRACKER.startGPSTracker();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        googleMap.setOnMarkerClickListener(this);
    }

    private void moveCamera(GoogleMap googleMap, LatLng latLng) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
        if (yourMarker != null) {
            yourMarker.remove();
        }
        if (circle != null) {
            circle.remove();
            circle = null;
        }
        circle = googleMap.addCircle(drawMarkerWithCircle(5000, googleMap, latLng));
        yourMarker = addMapMarker(googleMap, latLng.latitude, latLng.longitude, "You're currently here", "", -1, true);
        if (geoQuery == null) {
            initGeoQuery();
        }
        geoQuery.setCenter(new GeoLocation(latLng.latitude, latLng.longitude));
        geoQuery.setRadius(5);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.REQUEST_ENABLE_GPS) {
            if (resultCode == RESULT_CANCELED) {
                if (!isGPSEnabled()) {
                    enableGPS();
                } else {
                    if (AppConstants.GPS_TRACKER != null) {
                        AppConstants.GPS_TRACKER.startGPSTracker();
                        Log.d("gps", "RESULT OK ENABLED");
                    }
                }
            }
        }
    }

    private void initGeoQuery() {
        geoQuery = AppConstants.GEOFIRE.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), 5);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.d("geo", "key --> " + key + " location --> " + location.latitude + "," + location.longitude);
                markers.put(key, addMapMarker(googleMap, location.latitude, location.longitude,
                        key, "", 1, false));
            }

            @Override
            public void onKeyExited(String key) {
                Log.d("geo", "umexit na");
                markers.remove(key);
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                Log.d("geo", "gumalaw");
            }

            @Override
            public void onGeoQueryReady() {
                Log.d("geo", "geo query ready!");
            }

            @Override
            public void onGeoQueryError(FirebaseError error) {
                Log.d("geo", "firebase error --> " + error.getMessage());
            }
        });
    }

    /** map marker listener */
    @Override
    public boolean onMarkerClick(Marker marker) {
        if (!isNetworkAvailable()) {
            showToast(AppConstants.ERR_CONNECTION);
        } else {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Booking");
            query.include("user");
            showProgressDialog(AppConstants.LOAD_BOOKING_INFO);
            query.getInBackground(marker.getTitle(), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    dismissProgressDialog();
                    if (e == null) {
                        BookingInfoDialogFragment fragment = BookingInfoDialogFragment.newInstance(object);
                        fragment.show(getFragmentManager(),"booking");
                    } else {
                        showToast(AppConstants.ERR_GET_BOOKING_INFO);
                    }
                }
            });
            Log.d("gps", "marker --> " + marker.getTitle());
        }
        return true;
    }
}
