package hertz.hertz.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.parse.ParseObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import hertz.hertz.R;
import hertz.hertz.activities.BaseActivity;

/**
 * Created by rsbulanon on 1/4/16.
 */
public class CarFragment extends Fragment {

    @Bind(R.id.pbLoadImage) ProgressBar pbLoadImage;
    @Bind(R.id.ivCarImage) ImageView ivCarImage;
    @Bind(R.id.tvCarModel) TextView tvCarModel;
    @Bind(R.id.tvCapacity) TextView tvCapacity;
    @Bind(R.id.tvRatePer3Hours) TextView tvRatePer3Hours;
    @Bind(R.id.tvRatePer10Hours) TextView tvRatePer10Hours;
    @Bind(R.id.tvExcessRate) TextView tvExcessRate;
    private ParseObject car;
    private BaseActivity activity;

    public static CarFragment newInstance(ParseObject car) {
        CarFragment fragment = new CarFragment();
        fragment.car = car;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_select_car,container,false);
        ButterKnife.bind(this,view);
        activity = (BaseActivity)getActivity();
        tvCarModel.setText("Model : " + car.getString("carModel"));
        tvCapacity.setText("Seating Capacity : " + car.getNumber("capacity").intValue());
        tvRatePer3Hours.setText("Rate Per 3 Hours : " + activity.getDecimalFormatter().format(car.getNumber("ratePer3Hours")));
        tvRatePer10Hours.setText("Rate Per 10 Hours : " + activity.getDecimalFormatter().format(car.getNumber("ratePer10Hours")));
        tvExcessRate.setText("Excess Rate : " + activity.getDecimalFormatter().format(car.getNumber("excessRate")));
        if (car.getParseFile("carImage") == null) {
            pbLoadImage.setVisibility(View.GONE);
            ivCarImage.setVisibility(View.VISIBLE);
        } else {
            ImageLoader.getInstance().loadImage(car.getParseFile("carImage").getUrl(),new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    pbLoadImage.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    pbLoadImage.setVisibility(View.GONE);
                    ivCarImage.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    pbLoadImage.setVisibility(View.GONE);
                    ivCarImage.setVisibility(View.VISIBLE);
                    ivCarImage.setImageBitmap(loadedImage);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        }
        return view;
    }
}
