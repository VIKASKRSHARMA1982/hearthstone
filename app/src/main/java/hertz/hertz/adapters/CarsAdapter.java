package hertz.hertz.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import hertz.hertz.R;
import hertz.hertz.activities.CarManagementActivity;
import hertz.hertz.helpers.AppConstants;

/**
 * Created by rsbulanon on 11/23/15.
 */
public class CarsAdapter extends RecyclerView.Adapter<CarsAdapter.ViewHolder> {

    private ArrayList<ParseObject> records;
    private Context context;
    private CarManagementActivity activity;
    private ImageLoader imageLoader;

    public CarsAdapter(Context context, ArrayList<ParseObject> records) {
        this.context = context;
        this.records = records;
        this.activity = (CarManagementActivity)context;
        this.imageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_car, parent, false);
        ViewHolder  holder = new ViewHolder (v);
        return holder;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivCarImage;
        TextView tvCarModel;
        TextView tvRatePer3Hours;
        TextView tvRatePer10Hours;
        TextView tvExcessRate;
        ImageView ivEdit;
        ImageView ivDelete;

        ViewHolder (View view) {
            super(view);
            ivCarImage = (CircleImageView)view.findViewById(R.id.ivCarImage);
            tvCarModel = (TextView)view.findViewById(R.id.tvCarModel);
            tvRatePer3Hours = (TextView)view.findViewById(R.id.tvRatePer3Hours);
            tvRatePer10Hours = (TextView)view.findViewById(R.id.tvRatePer10Hours);
            tvExcessRate = (TextView)view.findViewById(R.id.tvExcessRate);
            ivEdit = (ImageView)view.findViewById(R.id.ivEdit);
            ivDelete = (ImageView)view.findViewById(R.id.ivDelete);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int i) {
        final ParseObject car = records.get(i);
        holder.tvCarModel.setText(car.getString("carModel"));
        holder.tvRatePer3Hours.setText("Php " + activity.getDecimalFormatter()
                .format(car.getNumber("ratePer3Hours").doubleValue()));
        holder.tvRatePer10Hours.setText("Php " + activity.getDecimalFormatter()
                .format(car.getNumber("ratePer10Hours").doubleValue()));
        holder.tvExcessRate.setText("Php " + activity.getDecimalFormatter()
                .format(car.getNumber("excessRate").doubleValue()));
        imageLoader.displayImage(car.getParseFile("carImage").getUrl(), holder.ivCarImage);
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity.isNetworkAvailable()) {
                    new SweetAlertDialog(context,SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Hertz")
                            .setContentText("Are you sure you want to delete this car record?")
                            .setConfirmText("Yes")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                    activity.showCustomProgress(AppConstants.LOAD_DELETE_CAR);
                                    car.put("markedAsDeleted", true);
                                    car.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            activity.dismissCustomProgress();
                                            if (e == null) {
                                                activity.removeCarFromList(i);
                                                activity.showSweetDialog(AppConstants.OK_CAR_DELETED,"success");
                                            } else {
                                                activity.showSweetDialog(e.getMessage(),"error");
                                            }
                                        }
                                    });
                                }
                            })
                            .setCancelText("No")
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            })
                            .show();
                } else {
                    activity.showSweetDialog(AppConstants.ERR_CONNECTION, "error");
                }
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
