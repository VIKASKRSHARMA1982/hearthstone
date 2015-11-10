package hertz.hertz.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import cn.pedant.SweetAlert.SweetAlertDialog;
import hertz.hertz.R;

/**
 * Created by rsbulanon on 11/11/15.
 */
public class BaseActivity extends AppCompatActivity {

    private SweetAlertDialog sweetAlertDialog;
    private ProgressDialog pDialog;

    public void showProgressDialog(String message) {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage(message);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public void dismissProgressDialog() {
        pDialog.dismiss();
    }

    public void updateProgressDialog(String message) {
        pDialog.setMessage(message);
    }

    public void animateToRight(Activity activity) {
        activity.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    public void animateToLeft(Activity activity) {
        activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    public void animateToUp(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_up, R.anim.slide_up);
    }

    public void animateToBottom(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_up, R.anim.slide_up);
    }

    public void animateFadeIn(Activity activity) {
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void animateFadeOut(Activity activity) {
        activity.overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void setError(TextView textView, String errorMsg) {
        textView.setError(errorMsg);
        textView.requestFocus();
    }

    /**
     * check network connection availability
     */
    public boolean isNetworkAvailable() {
        boolean isConnected = false;
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi.isConnected()) {
            isConnected = true;
        } else {
            NetworkInfo mData = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mData == null) {
                isConnected = false;
            } else {
                boolean isDataEnabled = mData.isConnected();
                isConnected = isDataEnabled ? true : false;
            }
        }
        return isConnected;
    }

    /** check if GPS is enabled */
    public boolean isGPSEnabled() {
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return false;
        }
        return true;
    }

    /** Date formatter */
    public SimpleDateFormat getDateFormatter() { return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); }

    /** get simple date formatter */
    public SimpleDateFormat getSDFWithTime() { return  new SimpleDateFormat("EEE, MMM dd, yyyy hh:mm a"); }

    public SimpleDateFormat getSDF() { return  new SimpleDateFormat("EEE, MMM dd, yyyy"); }

    /** Decimal formatter */
    public DecimalFormat getDecimalFormatter() { return new DecimalFormat("###,###.00"); }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public void showSweetDialog(String message, String type, final boolean finish,
                                final Activity activity, final String direction) {
        int dialogType = 0;
        if (type.equals("error")) {
            dialogType = SweetAlertDialog.ERROR_TYPE;
        } else if (type.equals("warning")) {
            dialogType = SweetAlertDialog.WARNING_TYPE;
        } else if (type.equals("success")) {
            dialogType = SweetAlertDialog.SUCCESS_TYPE;
        }
        new SweetAlertDialog(this,dialogType)
                .setTitleText("Hertz")
                .setContentText(message)
                .setConfirmText("Close")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        if (finish) {
                            finish();
                            if (direction.equals("left")) {
                                animateToLeft(activity);
                            } else {
                                animateToRight(activity);
                            }
                        }
                    }
                }).show();
    }

    public void updateSweetProgressDialog(String message) {
        sweetAlertDialog.setContentText(message);
    }

    public void dismissSweetProgressDialog() {
        sweetAlertDialog.dismiss();
    }

    public String getText(TextView view) {
        return view.getText().toString();
    }

    public Marker addMapMarker(GoogleMap map, double lat, double longi, String title,
                             String snippet, int icon, boolean showInfo) {
        Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(lat, longi)).
                title(title).snippet(snippet).icon(
                icon == -1 ? BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                        : BitmapDescriptorFactory.fromResource(icon)));
        if (showInfo) {
            marker.showInfoWindow();
        }
        return marker;
    }
}
