package hertz.hertz.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.rey.material.widget.Spinner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hertz.hertz.R;
import hertz.hertz.activities.DriverManagementActivity;
import hertz.hertz.helpers.AppConstants;

/**
 * Created by rsbulanon on 12/30/15.
 */
public class AddDriverDialogFragment extends DialogFragment {

    @Bind(R.id.ivProfilePic) ImageView ivProfilePic;
    @Bind(R.id.etEmail) EditText etEmail;
    @Bind(R.id.etLastName) EditText etLastName;
    @Bind(R.id.etFirstName) EditText etFirstName;
    @Bind(R.id.etMobileNo) EditText etMobileNo;
    @Bind(R.id.tvHeader) TextView tvHeader;
    @Bind(R.id.spnrAvailableCar) Spinner spnrAvailableCar;
    private static final int CAPTURE_IMAGE = 2;
    private View view;
    private DriverManagementActivity activity;
    private ParseUser driver;
    private boolean hasImage;
    private File profilePic;
    private OnAddDriverListener onAddDriverListener;
    private Bitmap bitmap;
    private String prevFirstName;
    private String prevLastName;
    private String prevMobile;
    private String prevCarId;
    private int prevCarIndex;

    public static AddDriverDialogFragment newInstance(ParseUser driver) {
        AddDriverDialogFragment frag = new AddDriverDialogFragment();
        frag.driver = driver;
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
        view = getActivity().getLayoutInflater().inflate(R.layout.dialog_fragment_add_driver, null);
        ButterKnife.bind(this, view);
        activity = (DriverManagementActivity)getActivity();
        initSpinner();
        if (driver != null) {
            tvHeader.setText("Update Driver Profile");
            prevFirstName = driver.getParseObject("driver").getString("firstName");
            prevLastName = driver.getParseObject("driver").getString("lastName");
            prevMobile = driver.getParseObject("driver").getString("mobileNo");
            prevCarId = driver.getParseObject("driver").getParseObject("car").getObjectId();
            prevCarIndex = getIndexOfAssignedCar();

            etEmail.setText(driver.getString("email"));
            etEmail.setFocusable(false);
            etFirstName.setText(prevFirstName);
            etFirstName.setSelection(prevFirstName.length());
            etLastName.setText(prevLastName);
            etMobileNo.setText(prevMobile);

            ImageLoader.getInstance().loadImage(driver.getParseObject("driver").getParseFile("profilePic").getUrl(),
                    new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            bitmap = loadedImage;
                            ivProfilePic.setImageBitmap(loadedImage);
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    });
        }
        final Dialog mDialog = new Dialog(getActivity());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        return mDialog;
    }

    @OnClick(R.id.ivProfilePic)
    public void changeProfilePic() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAPTURE_IMAGE);
        }
    }

    public static String getPath(Context context, Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            hasImage = true;
            bitmap = BitmapFactory.decodeFile(getPath(getActivity(), data.getData()));
            bitmap = getResizedBitmap(bitmap,300,300);
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ivProfilePic.setImageBitmap(photo);
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    @OnClick(R.id.btnAdd)
    public void addDriver() {
        final String email = etEmail.getText().toString();
        final String firstName = etFirstName.getText().toString();
        final String lastName = etLastName.getText().toString();
        final String mobileNo = etMobileNo.getText().toString();

        if (driver == null) {
            if (email.isEmpty()) {
                activity.setError(etEmail, AppConstants.WARN_FIELD_REQUIRED);
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                activity.setError(etEmail, AppConstants.WARN_INVALID_EMAIL_FORMAT);
            } else if (lastName.isEmpty()) {
                activity.setError(etFirstName, AppConstants.WARN_FIELD_REQUIRED);
            } else if (firstName.isEmpty()) {
                activity.setError(etLastName, AppConstants.WARN_FIELD_REQUIRED);
            } else if (mobileNo.isEmpty()) {
                activity.setError(etMobileNo, AppConstants.WARN_FIELD_REQUIRED);
            } else if (!hasImage) {
                activity.showToast(AppConstants.WARN_SELECT_DRIVER_IMAGE);
            } else {
                new ConvertImage(email,firstName,lastName,mobileNo).execute();
            }
        } else {
            ParseObject selectedCar = activity.getAvailableCars().get(spnrAvailableCar.getSelectedItemPosition());
            if (firstName.equals(prevFirstName) && lastName.equals(prevLastName)
                    && mobileNo.equals(prevMobile) && prevCarId.equals(selectedCar.getObjectId())) {
                activity.showSweetDialog(AppConstants.WARN_NO_CHANGES_DETECTED,"warning");
            } else {
                new ConvertImage(email,firstName,lastName,mobileNo).execute();
            }
        }
    }

    private class ConvertImage extends AsyncTask<Void,Void,byte[]> {

        private String email;
        private String firstName;
        private String lastName;
        private String mobileNo;

        public ConvertImage(String email, String firstName, String lastName, String mobileNo) {
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.mobileNo = mobileNo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            onAddDriverListener.onAddUpdateStart();
        }

        @Override
        protected byte[] doInBackground(Void... params) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            final String session = ParseUser.getCurrentUser().getSessionToken();
            final ParseFile pf = new ParseFile("img.png", bytes);
            pf.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        final int selectedSpinnerIndex = spnrAvailableCar.getSelectedItemPosition();
                        final ParseObject selectedCar = activity.getAvailableCars().get(selectedSpinnerIndex);
                        if (driver == null) {
                            /** create new driver record */
                            final ParseUser newDriver = new ParseUser();
                            newDriver.setEmail(email);
                            newDriver.setUsername(email);
                            newDriver.setPassword("123");
                            newDriver.put("isPasswordDefault", true);
                            newDriver.put("userRole", "driver");
                            newDriver.put("status", "active");
                            newDriver.signUpInBackground(new SignUpCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        final ParseObject object = new ParseObject("Driver");
                                        object.put("firstName", firstName);
                                        object.put("lastName", lastName);
                                        object.put("profilePic", pf);
                                        object.put("mobileNo", mobileNo);
                                        object.put("car", selectedCar);
                                        object.put("status","active");
                                        object.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    newDriver.put("driver", object);
                                                    newDriver.saveInBackground(new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            selectedCar.put("status", "assigned");
                                                            selectedCar.saveInBackground(new SaveCallback() {
                                                                @Override
                                                                public void done(ParseException e) {
                                                                    if (e == null) {
                                                                        try {
                                                                            activity.getAvailableCars().set(selectedSpinnerIndex, selectedCar);
                                                                            ParseUser.getCurrentUser().logOut();
                                                                            ParseUser.become(session);
                                                                            onAddDriverListener.onNewDriverAdded(newDriver);
                                                                        } catch (ParseException e1) {
                                                                            e1.printStackTrace();
                                                                            onAddDriverListener.onAddDriverFailed(e1);
                                                                        }
                                                                    } else {
                                                                        onAddDriverListener.onAddDriverFailed(e);
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    });
                                                } else {
                                                    onAddDriverListener.onAddDriverFailed(e);
                                                }
                                            }
                                        });
                                    } else {
                                        onAddDriverListener.onAddDriverFailed(e);
                                    }
                                }
                            });
                        } else {
                            final ParseObject prevCar = activity.getAvailableCars().get(prevCarIndex);
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Driver");
                            query.getInBackground(driver.getParseObject("driver").getObjectId(),
                                    new GetCallback<ParseObject>() {
                                        @Override
                                        public void done(ParseObject object, ParseException e) {
                                            /** update driver record */
                                            object.put("firstName", firstName);
                                            object.put("lastName", lastName);
                                            object.put("profilePic", pf);
                                            object.put("mobileNo", mobileNo);
                                            object.put("car",selectedCar);
                                            object.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        prevCar.put("status","available");
                                                        prevCar.saveInBackground(new SaveCallback() {
                                                            @Override
                                                            public void done(ParseException e) {
                                                                if (e == null) {
                                                                    activity.getAvailableCars().set(prevCarIndex,prevCar);
                                                                    selectedCar.put("status", "assigned");
                                                                    selectedCar.saveInBackground(new SaveCallback() {
                                                                        @Override
                                                                        public void done(ParseException e) {
                                                                            if (e == null) {
                                                                                activity.getAvailableCars().set(selectedSpinnerIndex, selectedCar);
                                                                                onAddDriverListener.onDriverRecordUpdated();
                                                                            } else {
                                                                                onAddDriverListener.onAddDriverFailed(e);
                                                                            }
                                                                        }
                                                                    });
                                                                } else {
                                                                    onAddDriverListener.onAddDriverFailed(e);
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                        onAddDriverListener.onAddDriverFailed(e);
                                                    }
                                                }
                                            });
                                        }
                                    });
                        }
                    } else {
                        onAddDriverListener.onAddDriverFailed(e);
                    }
                }
            });
        }
    }

    public interface OnAddDriverListener {
        void onAddUpdateStart();
        void onNewDriverAdded(ParseUser newDriver);
        void onDriverRecordUpdated();
        void onAddDriverFailed(ParseException ex);
    }

    public void setOnAddDriverListener(OnAddDriverListener onAddDriverListener) {
        this.onAddDriverListener = onAddDriverListener;
    }

    private void initSpinner() {
        ArrayList<String> items = new ArrayList<>();
        if (driver == null) {
            for (ParseObject o : activity.getAvailableCars()) {
                if (o.getString("status").equals("available")) {
                    items.add(o.getString("carModel"));
                }
            }
        } else {
            for (ParseObject o : activity.getAvailableCars()) {
                items.add(o.getString("carModel"));
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.row_spinner, items);
        adapter.setDropDownViewResource(R.layout.row_spinner_dropdown);
        spnrAvailableCar.setAdapter(adapter);

        if (driver != null) {
            spnrAvailableCar.setSelection(getIndexOfAssignedCar());
        }
    }

    private int getIndexOfAssignedCar() {
        for (int i = 0 ; i < activity.getAvailableCars().size() ; i++) {
            if (activity.getAvailableCars().get(i).getString("carModel")
                    .equals(driver.getParseObject("driver").getParseObject("car").getString("carModel"))) {
                return i;
            }
        }
        return 0;
    }
}
