package hertz.hertz.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Locale;

import hertz.hertz.R;
import hertz.hertz.model.Car;
import hertz.hertz.services.PlaceAPI;

public class CChooseRideActivity extends ActionBarActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */

    public static Car currentCar;


    RelativeLayout rlLoading;

    ViewPager mViewPager;
    boolean isSedan = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cvehicle);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        rlLoading = (RelativeLayout) findViewById(R.id.loading_layout);
        rlLoading.setVisibility(View.GONE);

        final ImageView imgVehicleTab = (ImageView) findViewById(R.id.img_tabs_vehicle);
        imgVehicleTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSedan) {
                    imgVehicleTab.setBackgroundResource(R.drawable.vehicle_header_suv);
                    mViewPager.setCurrentItem(1, true);
                } else {
                    imgVehicleTab.setBackgroundResource(R.drawable.vehicle_header_sedan);
                    mViewPager.setCurrentItem(0, true);
                }
                isSedan = !isSedan;
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cvehicle, menu);
        return true;
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
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch(position){
                default:
                case 0:
                    return new CRideSedanFragment();

                case 1:
                    return new CRideSuvFragment();

            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
    }

    String placeIdOrigin = "", placeIdDest = "";

    public void onClick(View view){

        if(rlLoading.getVisibility() == View.GONE) {
            rlLoading.setVisibility(View.VISIBLE);

            SharedPreferences prefs = getSharedPreferences("placeids", MODE_PRIVATE);
            placeIdDest = prefs.getString("placeIdDest", "");
            placeIdOrigin = prefs.getString("placeIdOrigin", "");

            Intent intent = new Intent(CChooseRideActivity.this, CMapsAssignedDriverActivity.class);

            try {
                intent.putExtra("isSedan", isSedan);
                intent.putExtra("price", 0);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                e.fillInStackTrace();
            }
            //place back when has net
            /*
            new GetDirectionsDuration(new GetDirectionsDurationListener() {
                @Override
                public void OnGetDuration(Integer mins) {

                    int price = 0;
                    if(mins == null || mins == 0){
                        price = 0;
                    }else {
                        int numHours = mins / 60;

                        if (numHours >= 5) {
                            price += CChooseRideActivity.currentCar.getPriceExcess() * Math.abs(numHours - 5);
                            price += CChooseRideActivity.currentCar.getPrice5Hours();
                        } else {
                            if (numHours < 0) {
                                price += CChooseRideActivity.currentCar.getPriceExcess() * (numHours - 3);
                            }

                            price += CChooseRideActivity.currentCar.getPrice3Hours();
                        }
                    }

                    Intent intent = new Intent(CChooseRideActivity.this, CMapsAssignedDriverActivity.class);

                    try {
                        intent.putExtra("isSedan", isSedan);
                        intent.putExtra("price", price);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        e.fillInStackTrace();
                    }

                }


            }).execute(placeIdOrigin, placeIdDest);*/
        }

    }


    interface GetDirectionsDurationListener{
        public void OnGetDuration(Integer doc);
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

}