package hertz.hertz.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import hertz.hertz.R;
import hertz.hertz.model.PlaceData;
import hertz.hertz.services.PlaceAPI;


public class CMapsAssignedDriverActivity extends ActionBarActivity
        implements CNavDrawerFragment.NavigationDrawerCallbacks {

    private static String TAG = CMapsAssignedDriverActivity.class.getSimpleName();

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private CNavDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;


    private AutoCompleteTextView editTextPick, editTextDest;
    ArrayAdapter<String> adapterPick, adapterDest;

    ArrayList<LatLng> arrLatLng;

    Polyline polyline;
    String placeIdOrigin = "", placeIdDest = "";

    Button btnCall;
    LinearLayout llSearch, llDriver, llPrice;
    RelativeLayout llCall;
    ImageView imgLogo;

    TextView tvPrice;
    int price = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmap_driver);

        btnCall = (Button) findViewById(R.id.btn_call);
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + "09023456789"));
                startActivity(intent);
            }
        });

        tvPrice = (TextView)findViewById(R.id.tv_price);

        //editor.putString("placeIdOrigin", placeIdOrigin);
        //editor.putString("placeIdDest", placeIdDest);
        SharedPreferences prefs = getSharedPreferences("placeids", MODE_PRIVATE);
        placeIdDest = prefs.getString("placeIdDest", "");
        placeIdOrigin = prefs.getString("placeIdOrigin", "");

        arrLatLng = new ArrayList<LatLng>();


         isSedan = this.getIntent().getBooleanExtra("isSedan", false);
        price = this.getIntent().getIntExtra("price", 0);


        tvPrice.setText("ESTIMATED PRICE: P"+price);

        setUpMapIfNeeded();
    }
    boolean isSedan;
    public void fixCam(){

        LatLngBounds.Builder bc = new LatLngBounds.Builder();

        for (LatLng item : arrLatLng) {
            bc.include(item);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 50));
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments


    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            //getMenuInflater().inflate(R.menu.cmap, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_cmap, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
           /* ((CMapsActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));*/
        }
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.

            //mMap = ((GoogleMap) view.findViewById(R.id.map));
            /*mMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map))
                    .getMap();
*/

            mMap = ((SupportMapFragment) CMapsAssignedDriverActivity.this.getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();



            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */


    Marker curr, dest;

    private void setUpMap() {
    /*    new GetDirectionsDuration(new GetDirectionsDurationListener() {
            @Override
            public void OnGetDuration(Integer mins) {
                int price = 1750, excess = 550;

                if(!isSedan){
                    price = 3100;
                    excess = 800;
                }

                tvPrice.setText("ESTIMATED PRICE: P"+ (price+ (Math.abs(mins / 60)-3)*excess));

            }


        }).execute(placeIdOrigin, placeIdDest);*/


        new GetDirections(new GetDirectionsListener() {
            @Override
            public void OnGetDirections(ArrayList<LatLng> doc) {

                if (polyline != null)
                    polyline.remove();

                PolylineOptions rectLine = new PolylineOptions().width(5).color(
                        Color.BLUE);

                rectLine.addAll(doc);
                arrLatLng=
                        doc;
                polyline = mMap.addPolyline(rectLine);

                fixCam();
            }
        }).execute(placeIdOrigin, placeIdDest);//arrLatLng.get(0),arrLatLng.get(1));

        /*mMap.setMyLocationEnabled(true);


        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location currLoc) {

                curr = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        .position(new LatLng(currLoc.getLatitude(), currLoc.getLongitude()))
                        .title("Pick Up Point"));

                dest = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .position(new LatLng(currLoc.getLatitude(), currLoc.getLongitude()))
                        .title("Destination"));

                mMap.animateCamera(CameraUpdateFactory
                        .newLatLngZoom(new LatLng(currLoc.getLatitude()
                                , currLoc.getLongitude()), 12.0f));

                mMap.setMyLocationEnabled(false);
            }
        });*/

    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        // Get rid of our Place API Handlers
/*        if (mThreadHandler != null) {
            mThreadHandler.removeCallbacksAndMessages(null);
            mHandlerThread.quit();
        }*/
    }

    ArrayList<PlaceData> tempAutoCompList;
    class GetPlaces extends AsyncTask<String, Void, ArrayList<String>> {

        PlaceAPI mPlaceAPI = new PlaceAPI();

        @Override
        // three dots is java for an array of strings
        protected ArrayList<String> doInBackground(String... args) {
            tempAutoCompList = new ArrayList<PlaceData>();
            tempAutoCompList = mPlaceAPI.autocomplete(args[0]);

            ArrayList<String> addressList = new ArrayList<String>();

            for(PlaceData pData: tempAutoCompList){
                addressList.add(pData.getDesc());
            }

            return addressList;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);

            if(editTextPick.isFocused()) {
                adapterPick = new ArrayAdapter<String>(CMapsAssignedDriverActivity.this, R.layout.autocomplete_list_item, strings);
                editTextPick.setAdapter(adapterPick);
                adapterPick.setNotifyOnChange(true);
                adapterPick.notifyDataSetChanged();
            }else if(editTextDest.isFocused()) {
                adapterDest = new ArrayAdapter<String>(CMapsAssignedDriverActivity.this, R.layout.autocomplete_list_item, strings);
                editTextDest.setAdapter(adapterDest);
                adapterDest.setNotifyOnChange(true);
                adapterDest.notifyDataSetChanged();
            }

        }
    }

    class GetDirectionsDuration extends AsyncTask<String, Void, Integer> {

        GetDirectionsDurationListener listener;
        public GetDirectionsDuration(GetDirectionsDurationListener listener){
            this.listener = listener;
        }


        PlaceAPI mPlaceAPI = new PlaceAPI();

        @Override
        protected void onPostExecute(Integer document) {
            super.onPostExecute(document);
            listener.OnGetDuration(document);
        }

        @Override
        protected Integer doInBackground(String... params) {
            return mPlaceAPI.getDurationMins(params[0],params[1],"driving");
        }
    }

    class GetDirections extends AsyncTask<String, Void, List<LatLng>> {

        GetDirectionsListener listener;
        public GetDirections(GetDirectionsListener listener){
            this.listener = listener;
        }


        PlaceAPI mPlaceAPI = new PlaceAPI();

        @Override
        protected void onPostExecute(List<LatLng> document) {
            super.onPostExecute(document);

            ArrayList<LatLng> temp = new ArrayList<LatLng>();
            temp.addAll(document);

            listener.OnGetDirections(temp);
        }

        @Override
        protected List<LatLng> doInBackground(String... params) {
            return mPlaceAPI.getDirections(params[0], params[1], "driving");
        }
    }


    class GetLatLng extends AsyncTask<String, Void, LatLng> {

        GetLangLangListener listener;
        public GetLatLng(GetLangLangListener listener){
          this.listener = listener;
        }


        PlaceAPI mPlaceAPI = new PlaceAPI();
        @Override
        protected LatLng doInBackground(String... params) {
            return mPlaceAPI.getLatLng(params[0]);
        }

        @Override
        protected void onPostExecute(LatLng latLng) {
            super.onPostExecute(latLng);
            listener.OnGetLatLang(latLng);
        }
    }



    interface GetDirectionsDurationListener{
        public void OnGetDuration(Integer doc);
    }

    interface GetDirectionsListener{
        public void OnGetDirections(ArrayList<LatLng> doc);
    }

    interface GetLangLangListener{
        public void OnGetLatLang(LatLng ll);
    }

    public void onClick(View view){


        switch(view.getId()) {
            case R.id.btn_book:
                if (!editTextDest.getText().toString().trim().isEmpty()
                        && !editTextPick.getText().toString().trim().isEmpty()) {
                    showErrorMessage();
                } else {
                    startActivity(new Intent(this, CReservationActivity.class));
                }
                break;
        }
    }
    private void showErrorMessage(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CMapsAssignedDriverActivity.this);
        dialogBuilder.setMessage("Incomplete details. Please select a place for destination.");
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();
    }
}
