package hertz.hertz.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import hertz.hertz.R;
import hertz.hertz.model.PlaceData;
import hertz.hertz.services.PlaceAPI;


public class CMapsActivity extends BaseActivity
        implements CNavDrawerFragment.NavigationDrawerCallbacks, View.OnClickListener {

    private static String TAG = CMapsActivity.class.getSimpleName();
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private CNavDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private AutoCompleteTextView editTextPick, editTextDest;
    ArrayAdapter<String> adapterPick, adapterDest;
    ArrayList<LatLng> arrLatLng;
    Polyline polyline;
    String placeIdOrigin = "", placeIdDest = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmap);
        mNavigationDrawerFragment = (CNavDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        /*imgLogo = (ImageView)findViewById(R.id.img_logo);
        btnBook = (Button)findViewById(R.id.btn_book);
        llSearch = (LinearLayout)findViewById(R.id.ll_map_search);
        llCall = (RelativeLayout)findViewById(R.id.ll_btn_calls);
        llDriver = (LinearLayout)findViewById(R.id.ll_driver);
        llPrice = (LinearLayout)findViewById(R.id.ll_map_price);


        llSearch.setVisibility(View.VISIBLE);
        btnBook.setVisibility(View.VISIBLE);
        imgLogo.setVisibility(View.VISIBLE);

        llCall.setVisibility(View.GONE);
        llDriver.setVisibility(View.GONE);
        llPrice.setVisibility(View.GONE);*/



        ImageButton btnReserve = (ImageButton) findViewById(R.id.btn_reserve);
        ImageButton btnInfo = (ImageButton) findViewById(R.id.btn_reserve);
        ImageButton btnAccount = (ImageButton) findViewById(R.id.btn_reserve);
        ImageButton btnContact = (ImageButton) findViewById(R.id.btn_reserve);

        btnInfo.setOnClickListener(this);
        btnAccount.setOnClickListener(this);
        btnContact.setOnClickListener(this);

        adapterPick = new ArrayAdapter<String>(this,R.layout.autocomplete_list_item);
        adapterPick.setNotifyOnChange(true);
        adapterDest = new ArrayAdapter<String>(this,R.layout.autocomplete_list_item);
        adapterDest.setNotifyOnChange(true);

        editTextPick = (AutoCompleteTextView) findViewById(R.id.et_pickUp);
        editTextDest = (AutoCompleteTextView) findViewById(R.id.et_destination);

        editTextPick.setAdapter(adapterPick);
        adapterPick.notifyDataSetChanged();
        editTextDest.setAdapter(adapterDest);
        adapterDest.notifyDataSetChanged();

        arrLatLng = new ArrayList<LatLng>();
        arrLatLng.add(null);
        arrLatLng.add(null);

        editTextDest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                placeIdDest = tempAutoCompList.get(position).getId();
                new GetLatLng(new GetLangLangListener() {
                    @Override
                    public void OnGetLatLang(LatLng ll) {
                        /*mMap.clear();
                        createMarkers();*/


                        arrLatLng.add(1, ll);
                        if(dest != null)

                            dest.remove();
                        dest = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                .position(ll)
                                .title("Destination"));
                        fixCam();

                    }
                }).execute(placeIdDest);


                if (!placeIdDest.isEmpty() && !placeIdOrigin.isEmpty()){
                    new GetDirections(new GetDirectionsListener() {
                        @Override
                        public void OnGetDirections(List<LatLng> doc) {

                            if (polyline != null)
                                polyline.remove();

                            PolylineOptions rectLine = new PolylineOptions().width(3).color(
                                    Color.RED);

                            rectLine.addAll(doc);

                            polyline = mMap.addPolyline(rectLine);
                        }
                    }).execute(placeIdOrigin, placeIdDest);//arrLatLng.get(0),arrLatLng.get(1));
                }
            }
        });

        editTextPick.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                placeIdOrigin = tempAutoCompList.get(position).getId();
                new GetLatLng(new GetLangLangListener() {
                    @Override
                    public void OnGetLatLang(LatLng ll) {
                        /*mMap.clear();
                        createMarkers();*/

                        arrLatLng.add(0,ll);
                        if(curr != null)
                            curr.remove();
                        curr = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                .position(ll)
                                .title("Pick Up Point"));
                        fixCam();

                    }
                }).execute(placeIdOrigin);

                if (!placeIdDest.isEmpty() && !placeIdOrigin.isEmpty()){
                    new GetDirections(new GetDirectionsListener() {
                        @Override
                        public void OnGetDirections(List<LatLng> doc) {

                            if (polyline != null)
                                polyline.remove();

                            PolylineOptions rectLine = new PolylineOptions().width(5).color(
                                    Color.BLUE);

                            rectLine.addAll(doc);
                            polyline = mMap.addPolyline(rectLine);
                        }
                    }).execute(placeIdOrigin, placeIdDest);//arrLatLng.get(0),arrLatLng.get(1));
                }

            }
        });

        editTextPick.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count % 3 == 1) {
                    adapterPick.clear();
                    new GetPlaces().execute(editTextPick.getText().toString());
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {

            }

        });

        editTextDest.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count % 3 == 1) {
                    adapterDest.clear();
                    new GetPlaces().execute(editTextDest.getText().toString());
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) { }

            public void afterTextChanged(Editable s) { }

        });


        setUpMapIfNeeded();
    }

    public void fixCam(){

        LatLngBounds.Builder bc = new LatLngBounds.Builder();
        if(bc != null) {

            for (LatLng item : arrLatLng) {
                if(item != null)
                   bc.include(item);
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 50));
        }
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
        if(actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }
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
            mMap = ((SupportMapFragment) CMapsActivity.this.getSupportFragmentManager()
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
        mMap.setMyLocationEnabled(true);
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
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
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
                adapterPick = new ArrayAdapter<String>(CMapsActivity.this, R.layout.autocomplete_list_item, strings);
                editTextPick.setAdapter(adapterPick);
                adapterPick.setNotifyOnChange(true);
                adapterPick.notifyDataSetChanged();
            }else if(editTextDest.isFocused()) {
                adapterDest = new ArrayAdapter<String>(CMapsActivity.this, R.layout.autocomplete_list_item, strings);
                editTextDest.setAdapter(adapterDest);
                adapterDest.setNotifyOnChange(true);
                adapterDest.notifyDataSetChanged();
            }

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
            listener.OnGetDirections(document);
        }

        @Override
        protected List<LatLng> doInBackground(String... params) {
            return mPlaceAPI.getDirections(params[0],params[1],"driving");
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



    interface GetDirectionsListener{
        public void OnGetDirections(List<LatLng> doc);
    }

    interface GetLangLangListener{
        public void OnGetLatLang(LatLng ll);
    }

    public void onClick(View view){


        Intent intent = null;
        switch(view.getId()) {
            case R.id.btn_book:
                /*if (editTextDest.getText().toString().trim().isEmpty()
                        || editTextPick.getText().toString().trim().isEmpty()
                        )
                {
                    showErrorMessage();
                } else */{

                    //placeIdOrigin = "", placeIdDest = ""
                    SharedPreferences.Editor editor = getSharedPreferences("placeids", MODE_PRIVATE).edit();
                    editor.putString("placeIdOrigin", placeIdOrigin);
                    editor.putString("placeIdDest", placeIdDest);
                    editor.commit();

                     startActivity(new Intent(this, CReservationActivity.class));
                }
                break;

            case R.id.btn_reserve:
                //intent = new Intent(CNavDrawerFragment.this.getActivity(),CInformationActivity.class);
                new SweetAlertDialog(CMapsActivity.this,SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Hertz")
                        .setContentText("Are you sure you want to logout your account?")
                        .setConfirmText("Yes")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                                ParseUser.logOut();
                                startActivity(new Intent(CMapsActivity.this, COpeningScreenActivity.class));
                                animateToLeft(CMapsActivity.this);
                                finish();
                            }
                        })
                        .setCancelText("No")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        }).show();

                break;
            case R.id.btn_info:
                intent = new Intent(CMapsActivity.this,CInformationActivity.class);
                this.startActivity(intent);
                break;
            case R.id.btn_account:
                intent = new Intent(CMapsActivity.this,CInformationMyAccountActivity.class);
                this.startActivity(intent);
                break;
            case R.id.btn_contact:
                intent = new Intent(CMapsActivity.this,CInformationContactActivtity.class);
                this.startActivity(intent);
                break;
        }
    }
    private void showErrorMessage(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CMapsActivity.this);
        dialogBuilder.setMessage("Incomplete details. Please select a place for destination.");
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();
    }
}
