package hertz.hertz.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hertz.hertz.R;
import hertz.hertz.adapters.DriversAdapter;
import hertz.hertz.helpers.AppConstants;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;

/**
 * Created by rsbulanon on 12/30/15.
 */
public class DriverManagementActivity extends BaseActivity {

    @Bind(R.id.rvDrivers) RecyclerView rvDrivers;
    @Bind(R.id.swipeRefresh) SwipeRefreshLayout swipeRefresh;
    private ArrayList<ParseUser> drivers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_management);
        ButterKnife.bind(this);
        DriversAdapter adapter = new DriversAdapter(this,drivers);
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(alphaAdapter);
        rvDrivers.setAdapter(scaleAdapter);
        rvDrivers.setLayoutManager(new LinearLayoutManager(this));
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(false);
            }
        });
        getDrivers();
    }

    public void getDrivers() {
        showCustomProgress(AppConstants.LOAD_FETCH_ALL_DRIVERS);
        final ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("status","active");
        query.whereEqualTo("userRole","driver");
        query.orderByAscending("lastName");
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                dismissCustomProgress();
                if (e == null) {
                    drivers.clear();
                    drivers.addAll(objects);
                    rvDrivers.getAdapter().notifyDataSetChanged();
                } else {
                    showSweetDialog(e.getMessage(),"error");
                }
            }
        });
    }
}
