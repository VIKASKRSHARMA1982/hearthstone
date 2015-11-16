package hertz.hertz.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hertz.hertz.R;
import hertz.hertz.adapters.PlaceAutocompleteAdapter;
import hertz.hertz.customviews.DrawerArrowDrawable;
import hertz.hertz.helpers.MapHelper;
import hertz.hertz.interfaces.OnCalculateDirectionListener;
import hertz.hertz.tasks.GetDirectionsAsyncTask;

/**
 * Created by rsbulanon on 11/13/15.
 */
public class HomeActivity extends BaseActivity implements OnMapReadyCallback,
                                                    NavigationView.OnNavigationItemSelectedListener,
                                                    OnCalculateDirectionListener,
                                                    GoogleApiClient.OnConnectionFailedListener,
                                                    AdapterView.OnItemClickListener {

    @Bind(R.id.drawerLayout) DrawerLayout drawerLayout;
    @Bind(R.id.drawerIndicator) ImageView drawerIndicator;
    @Bind(R.id.navDrawer) NavigationView navDrawer;
    @Bind(R.id.autoOrigin) AutoCompleteTextView autoOrigin;
    @Bind(R.id.autoDestination) AutoCompleteTextView autoDestination;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
    private GoogleApiClient googleApiClient;
    private DrawerArrowDrawable drawerArrowDrawable;
    private final Handler mDrawerActionHandler = new Handler();
    private static final long DRAWER_CLOSE_DELAY_MS = 250;
    private float offset;
    private boolean flipped;
    private GoogleMap map;
    private Place placeOrigin = null;
    private Place placeDesti = null;
    private PlaceAutocompleteAdapter mAdapter;
    private double latitude;
    private double longitude;
    private String selectedPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        initDrawerArrowDrawable();
        initGoogleClient();
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initGoogleClient() {
        googleApiClient = new GoogleApiClient
                .Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(Places.GEO_DATA_API)
                .addOnConnectionFailedListener( this )
                .build();
        mAdapter = new PlaceAutocompleteAdapter(this, googleApiClient, BOUNDS_GREATER_SYDNEY, null);
        autoOrigin.setAdapter(mAdapter);
        autoOrigin.setOnItemClickListener(this);

        autoDestination.setAdapter(mAdapter);
        autoDestination.setOnItemClickListener(this);
    }

    private void initDrawerArrowDrawable() {
        drawerArrowDrawable = new DrawerArrowDrawable(getResources());
        drawerArrowDrawable.setStrokeColor(Color.WHITE);
        drawerIndicator.setImageDrawable(drawerArrowDrawable);
        drawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                offset = slideOffset;
                // Sometimes slideOffset ends up so close to but not quite 1 or 0.
                if (slideOffset >= .995) {
                    flipped = true;
                    drawerArrowDrawable.setFlip(flipped);
                } else if (slideOffset <= .005) {
                    flipped = false;
                    drawerArrowDrawable.setFlip(flipped);
                }

                drawerArrowDrawable.setParameter(offset);
            }
        });
        //tvFullName = (TextView)navDrawer.findViewById(R.id.tvFullName);
        //tvFullName.setText(DaoHelper.getUserInfo().getFullName());
        navDrawer.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setScrollGesturesEnabled(true);
        final LatLng latLng = new LatLng( 14.696123, 121.117771);
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(11));
    }

    @OnClick(R.id.drawerIndicator)
    public void toggleDrawerMenu() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    private void plotDirection(LatLng origin, LatLng desti) {
        GetDirectionsAsyncTask asyncTask = new GetDirectionsAsyncTask(this);
        asyncTask.execute(findDirections(origin.latitude, origin.longitude,
                desti.latitude, desti.longitude, MapHelper.MODE_DRIVING));
        //addMapMarker(mapMunicipal, location.getLatitude(), location.getLongitude(), "You are here", "", -1);
    }

    public Map<String, String> findDirections(double fromPositionDoubleLat, double fromPositionDoubleLong, double toPositionDoubleLat, double toPositionDoubleLong, String mode) {
        Log.d("map", "find direction");
        Map<String, String> map = new HashMap<>();
        map.put(GetDirectionsAsyncTask.USER_CURRENT_LAT, String.valueOf(fromPositionDoubleLat));
        map.put(GetDirectionsAsyncTask.USER_CURRENT_LONG, String.valueOf(fromPositionDoubleLong));
        map.put(GetDirectionsAsyncTask.DESTINATION_LAT, String.valueOf(toPositionDoubleLat));
        map.put(GetDirectionsAsyncTask.DESTINATION_LONG, String.valueOf(toPositionDoubleLong));
        map.put(GetDirectionsAsyncTask.DIRECTIONS_MODE, mode);
        return map;
    }

    public PolylineOptions handleGetDirectionsResult(ArrayList<LatLng> directionPoints) {
        PolylineOptions rectLine = new PolylineOptions().width(5).color(Color.RED);
        for(int i = 0 ; i < directionPoints.size() ; i++) {
            rectLine.add(directionPoints.get(i));
        }
        return rectLine;
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        drawerLayout.closeDrawer(GravityCompat.START);
        mDrawerActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                navigate(menuItem.getItemId());
            }
        }, DRAWER_CLOSE_DELAY_MS);
        return true;
    }

    private void navigate(int menu) {

    }

    @Override
    public void onCalculationBegin() {
        Log.d("dir", "begin");
        showProgressDialog("Calculating direction, Please wait...");
    }

    @Override
    public void onCalculationFinished(ArrayList result) {
        Log.d("dir", "finished!");
        dismissProgressDialog();
        map.addPolyline(handleGetDirectionsResult(result));
    }

    @Override
    public void onCalculationException(Exception e) {
        dismissProgressDialog();
        showToast(e.toString());
        Log.d("dir", "on error --> " + e.toString());
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void showPlaceMarker(Place place) {
        map.addMarker(new MarkerOptions().position(new LatLng(place.getLatLng().latitude,
                place.getLatLng().longitude)).title(place.getName().toString())
                .snippet(place.getAddress().toString()).icon(BitmapDescriptorFactory
                .defaultMarker(selectedPlace.equals("origin") ? BitmapDescriptorFactory.HUE_GREEN
                        : BitmapDescriptorFactory.HUE_RED))).showInfoWindow();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 10));
        map.animateCamera(CameraUpdateFactory.zoomTo(14));
        if (placeOrigin != null && placeDesti != null) {
            Log.d("dir","must plot direction");
            plotDirection(placeOrigin.getLatLng(),placeDesti.getLatLng());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        final AutocompletePrediction item = mAdapter.getItem(position);
        final String placeId = item.getPlaceId();
        if (view.getId() == R.id.autoOrigin) {
            selectedPlace = "origin";
        } else {
            selectedPlace = "destination";
        }
        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(googleApiClient, placeId);
        placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
    }

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.d("places", "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            if (selectedPlace.equals("origin")) {
                placeOrigin = places.get(0);
                setPlace(placeOrigin);
            } else {
                placeDesti = places.get(0);
                setPlace(placeDesti);
            }
            places.release();
        }
    };

    private void setPlace(Place place) {
        latitude = place.getLatLng().latitude;
        longitude = place.getLatLng().longitude;
        showPlaceMarker(place);
    }
}
