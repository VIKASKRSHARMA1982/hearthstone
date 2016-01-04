package hertz.hertz.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.support.v4.app.FragmentManager;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hertz.hertz.R;
import hertz.hertz.activities.BaseActivity;
import hertz.hertz.adapters.ViewPagerAdapter;
import hertz.hertz.helpers.AppConstants;

/**
 * Created by rsbulanon on 1/4/16.
 */
public class SelectCarDialogFragment extends DialogFragment {

    @Bind(R.id.viewPager) ViewPager viewPager;
    @Bind(R.id.rbPickUp) RadioButton rbPickUp;
    @Bind(R.id.rbCarHire) RadioButton rbCarHire;
    private View view;
    private BaseActivity activity;
    private ArrayList<ParseObject> cars = new ArrayList<>();
    private ArrayList<Fragment> carsFragment = new ArrayList<>();

    public static SelectCarDialogFragment newInstance() {
        final SelectCarDialogFragment frag = new SelectCarDialogFragment();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.dialog_fragment_select_car, null);
        ButterKnife.bind(this, view);
        activity = (BaseActivity)getActivity();


        final Dialog mDialog = new Dialog(getActivity());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        return mDialog;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Car");
        query.whereEqualTo("status","available");
        activity.showCustomProgress(AppConstants.LOAD_FETCH_AVAILABLE_CARS);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                activity.dismissCustomProgress();
                if (e == null) {
                    cars.addAll(objects);
                    rbPickUp.setChecked(true);
                    carsFragment.clear();
                    for (ParseObject car : filterCarsByPurpose("For Pick and Drop off")) {
                        carsFragment.add(CarFragment.newInstance(car));
                    }
                    Log.d("cars", "cars fragment size --> " + carsFragment.size());

                    viewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), carsFragment));
                } else {
                    activity.showSweetDialog(e.getMessage(), "error");
                }
            }
        });
    }

    private ArrayList<ParseObject> filterCarsByPurpose(String purpose) {
        ArrayList<ParseObject> c = new ArrayList<>();
        for (ParseObject o : cars) {
            if (o.getString("purpose").equalsIgnoreCase(purpose)) {
                c.add(o);
            }
        }
        return c;
    }
}
