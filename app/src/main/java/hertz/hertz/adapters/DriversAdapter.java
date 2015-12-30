package hertz.hertz.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import hertz.hertz.R;
import hertz.hertz.activities.CarManagementActivity;
import hertz.hertz.activities.DriverManagementActivity;
import hertz.hertz.fragments.AddCarDialogFragment;
import hertz.hertz.helpers.AppConstants;

/**
 * Created by rsbulanon on 11/23/15.
 */
public class DriversAdapter extends RecyclerView.Adapter<DriversAdapter.ViewHolder> {

    private ArrayList<ParseUser> drivers;
    private Context context;
    private DriverManagementActivity activity;

    public DriversAdapter(Context context, ArrayList<ParseUser> drivers) {
        this.context = context;
        this.drivers = drivers;
        this.activity = (DriverManagementActivity)context;
    }

    @Override
    public int getItemCount() {
        return drivers.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_driver, parent, false);
        ViewHolder  holder = new ViewHolder (v);
        return holder;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivCarImage;
        TextView tvDriverName;
        TextView tvMobileNo;
        ImageView ivEdit;
        ImageView ivDelete;
        ProgressBar pbLoadImage;

        ViewHolder (View view) {
            super(view);
            ivCarImage = (CircleImageView)view.findViewById(R.id.ivCarImage);
            tvDriverName = (TextView)view.findViewById(R.id.tvDriverName);
            tvMobileNo = (TextView)view.findViewById(R.id.tvMobileNo);
            ivEdit = (ImageView)view.findViewById(R.id.ivEdit);
            ivDelete = (ImageView)view.findViewById(R.id.ivDelete);
            pbLoadImage = (ProgressBar)view.findViewById(R.id.pbLoadImage);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int i) {
        final ParseUser driver = drivers.get(i);

        holder.tvDriverName.setText(driver.getString("firstName") + " " + driver.getString("lastName"));
        holder.tvMobileNo.setText(driver.getString("mobileNo"));
        if (driver.getParseFile("profilePic") != null) {
            ImageLoader.getInstance().loadImage(driver.getParseFile("profilePic").getUrl(), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    holder.pbLoadImage.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    holder.pbLoadImage.setVisibility(View.GONE);
                    holder.ivCarImage.setImageBitmap(loadedImage);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        } else {
            holder.pbLoadImage.setVisibility(View.GONE);
        }

        /** edit car */
        holder.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        /** delete car */
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity.isNetworkAvailable()) {
                    new SweetAlertDialog(context,SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Hertz")
                            .setContentText("Are you sure you want to delete this driver record?")
                            .setConfirmText("Yes")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();

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