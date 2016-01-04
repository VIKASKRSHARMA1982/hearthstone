package hertz.hertz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hertz.hertz.R;
import hertz.hertz.adapters.ViewPagerAdapter;
import hertz.hertz.fragments.CarFragment;
import hertz.hertz.helpers.AppConstants;

public class CReservationActivity extends BaseActivity {

    @Bind(R.id.forDropOffPager) ViewPager forDropOffPager;
    @Bind(R.id.forHirePager) ViewPager forHirePager;
    @Bind(R.id.rbPickUp) RadioButton rbPickUp;
    @Bind(R.id.rbCarHire) RadioButton rbCarHire;
    private ArrayList<Fragment> forHireFragments = new ArrayList<>();
    private ArrayList<Fragment> forDropOffsFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creservation);
        ButterKnife.bind(this);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Car");
        query.whereEqualTo("status", "available");
        query.orderByAscending("carModel");
        showCustomProgress(AppConstants.LOAD_FETCH_AVAILABLE_CARS);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                dismissCustomProgress();
                if (e == null) {
                    rbPickUp.setChecked(true);
                    for (ParseObject car : objects) {
                        if (car.getString("purpose").equals("For Hire")) {
                            forHireFragments.add(CarFragment.newInstance(car));
                        } else {
                            forDropOffsFragments.add(CarFragment.newInstance(car));
                        }
                    }
                    forDropOffPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), forHireFragments));
                    forHirePager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), forDropOffsFragments));
                } else {
                    showSweetDialog(e.getMessage(), "error");
                }
            }
        });

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.pick_car);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbPickUp:
                        forDropOffPager.setVisibility(View.VISIBLE);
                        forHirePager.setVisibility(View.GONE);
                        break;
                    case R.id.rbCarHire:
                        forDropOffPager.setVisibility(View.GONE);
                        forHirePager.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }
}
