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
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hertz.hertz.R;
import hertz.hertz.customviews.DrawerArrowDrawable;
import hertz.hertz.helpers.MapHelper;
import hertz.hertz.interfaces.OnCalculateDirectionListener;
import hertz.hertz.tasks.GetDirectionsAsyncTask;

/**
 * Created by rsbulanon on 11/13/15.
 */
public class HomeActivity extends BaseActivity implements OnMapReadyCallback,
                                                    NavigationView.OnNavigationItemSelectedListener,
                                                    OnCalculateDirectionListener {

    @Bind(R.id.drawerLayout) DrawerLayout drawerLayout;
    @Bind(R.id.drawerIndicator) ImageView drawerIndicator;
    @Bind(R.id.navDrawer) NavigationView navDrawer;
    private DrawerArrowDrawable drawerArrowDrawable;
    private final Handler mDrawerActionHandler = new Handler();
    private static final long DRAWER_CLOSE_DELAY_MS = 250;
    private float offset;
    private boolean flipped;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        initDrawerArrowDrawable();
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
}
