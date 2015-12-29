package hertz.hertz.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hertz.hertz.R;
import hertz.hertz.activities.BaseActivity;
import hertz.hertz.helpers.AppConstants;

/**
 * Created by rsbulanon on 11/17/15.
 */
public class BookingInfoDialogFragment extends DialogFragment {

    @Bind(R.id.etClient) EditText etClient;
    @Bind(R.id.etFrom) EditText etFrom;
    @Bind(R.id.etTo) EditText etTo;
    @Bind(R.id.etContactNo) EditText etContactNo;
    private View view;
    private ParseObject booking;
    private BaseActivity activity;
    private OnAttendBookingListener onAttendBookingListener;

    public static BookingInfoDialogFragment newInstance(ParseObject booking) {
        final BookingInfoDialogFragment frag = new BookingInfoDialogFragment();
        frag.booking = booking;
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
        view = getActivity().getLayoutInflater().inflate(R.layout.dialog_fragment_booking_info, null);
        ButterKnife.bind(this, view);
        activity = (BaseActivity)getActivity();
        etClient.setText(booking.getParseObject("user").getString("firstName") + " "
        +booking.getParseObject("user").getString("lastName"));
        etFrom.setText(booking.getString("from"));
        etTo.setText(booking.getString("to"));
        etContactNo.setText(booking.getParseObject("user").getString("mobileNo"));
        final Dialog mDialog = new Dialog(getActivity());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        return mDialog;
    }

    @OnClick(R.id.btnAttend)
    public void attendClient() {
        if (activity.isNetworkAvailable()) {
            onAttendBookingListener.onAttend(booking.getObjectId());
        } else {
            activity.showToast(AppConstants.ERR_CONNECTION);
        }
    }

    public interface OnAttendBookingListener {
        void onAttend(String bookingId);
    }

    public void setOnAttendBookingListener(OnAttendBookingListener onAttendBookingListener) {
        this.onAttendBookingListener = onAttendBookingListener;
    }

}
