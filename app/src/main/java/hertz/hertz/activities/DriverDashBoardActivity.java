package hertz.hertz.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.BaseAdapter;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
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
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import hertz.hertz.R;
import hertz.hertz.fragments.BookingInfoDialogFragment;
import hertz.hertz.fragments.ChatDialogFragment;
import hertz.hertz.helpers.AppConstants;
import hertz.hertz.model.AvailableDriver;
import hertz.hertz.model.Chat;
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
    private BroadcastReceiver broadcastReceiver;
    private boolean isChatWindowOpen;
    private boolean isListeningToRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_dashboard);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ParsePush.subscribeInBackground("Drivers", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("parse", "successfully subscribed to the broadcast channel [Drivers]");
                } else {
                    Log.e("parse", "failed to subscribe for push", e);
                }
            }
        });
        ParsePush.subscribeInBackground("D" + ParseUser.getCurrentUser().getObjectId(), new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("parse", "successfully subscribed to the broadcast channel [" + "D" + ParseUser.getCurrentUser().getObjectId() + "]");
                } else {
                    Log.e("parse", "failed to subscribe for push", e);
                }
            }
        });

        if (!isGPSEnabled()) {
            enableGPS();
        } else {
            if (AppConstants.GPS_TRACKER != null) {
                AppConstants.GPS_TRACKER.startGPSTracker();
                Log.d("gps", "start getting status");
            }
        }

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String data = intent.getStringExtra("com.parse.Data");
                try {
                    final JSONObject json = new JSONObject(data);
                    if (json.has("room")) {
                        showChatWindow(data);
                    } else {
                        final String status = json.getJSONObject("json").getString("bookingStatus");
                        final String bookingId = json.getJSONObject("json").getString("bookingId");
                        final double latitude = json.getJSONObject("json").getDouble("latitude");
                        final double longitude = json.getJSONObject("json").getDouble("longitude");

                        if (status.equals("Pending")) {
                            showBookingMarker(bookingId, latitude, longitude);
                        } else {
                            if (markers.containsKey(bookingId)) {
                                markers.get(bookingId).remove();
                            }
                        }
                    }
                } catch (JSONException e) {
                    Log.d("push","ERROR IN PARSING JSON --> " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        final LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(this);
        mgr.registerReceiver(broadcastReceiver, new IntentFilter("broadcast_action"));
        fetchAvailableBooking();
    }

    private void fetchAvailableBooking() {
        showCustomProgress(AppConstants.LOAD_FETCH_NEARBY_BOOKING);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Booking");
        query.whereEqualTo("status","Pending");
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                dismissCustomProgress();
                if (e == null) {
                    for (ParseObject o : objects) {
                        showBookingMarker(o.getObjectId(),o.getParseGeoPoint("origin").getLatitude(),
                                o.getParseGeoPoint("origin").getLongitude());
                    }
                } else {
                    Log.d("err","failed to fetch nearby booking --> " + e.getMessage());
                    showToast(e.getMessage());
                }
            }
        });
    }

    private void showChatWindow(final String data) {
        if (!isChatWindowOpen) {
            isChatWindowOpen = true;
            try {
                JSONObject obj = new JSONObject(data);
                final String room = obj.getJSONObject("json").getString("room");
                final String sender = obj.getJSONObject("json").getString("senderName");
                ChatDialogFragment chat = ChatDialogFragment.newInstance(room, sender);
                chat.setOnDismissListener(new ChatDialogFragment.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        isChatWindowOpen = false;
                        if (!isListeningToRoom) {
                            isListeningToRoom = true;
                            listenToRoom(room,data);
                        }
                    }
                });
                chat.show(getFragmentManager(), "chat");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("push","error --> " + e.toString());
            }
        }
    }

    private void showBookingMarker(String bookingId, double latitude, double longitude) {
        final Location bookingLocation = new Location("");
        bookingLocation.setLatitude(latitude);
        bookingLocation.setLongitude(longitude);

        final Location currentLocation = new Location("");
        currentLocation.setLatitude(latLng.latitude);
        currentLocation.setLongitude(latLng.longitude);

        final float distanceInMeters = currentLocation.distanceTo(bookingLocation);
        final float distanceInKiloMeter = distanceInMeters / 1000;

        if (distanceInKiloMeter > 0) {
            markers.put(bookingId, addMapMarker(googleMap, latitude, longitude, "Booking Id : " + bookingId, "", 1, false));
        }
    }

    private void listenToRoom(final String room, final String data) {
        AppConstants.FIREBASE.child("Chat").child(room).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                showChatWindow(data);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, GPSTrackerService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        Log.d("push", "on pause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("broadcast_action");
        this.registerReceiver(broadcastReceiver, filter);
        Log.d("push", "on resume");
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


    /** map marker listener */
    @Override
    public boolean onMarkerClick(Marker marker) {
        if (!isNetworkAvailable()) {
            showToast(AppConstants.ERR_CONNECTION);
        } else {
            if (marker.getTitle().contains("Booking Id")) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Booking");
                query.include("user");
                showProgressDialog(AppConstants.LOAD_BOOKING_INFO);
                String bookingId = marker.getTitle().replace("Booking Id : ","");
                query.getInBackground(bookingId, new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        dismissProgressDialog();
                        if (e == null) {
                            BookingInfoDialogFragment fragment = BookingInfoDialogFragment.newInstance(object);
                            fragment.show(getFragmentManager(),"booking");
                        } else {
                            Log.d("err","failed to get booking info --> " + e.getMessage());
                            showToast(AppConstants.ERR_GET_BOOKING_INFO);
                        }
                    }
                });
                Log.d("gps", "marker --> " + marker.getTitle());
            }
        }
        return true;
    }
}
