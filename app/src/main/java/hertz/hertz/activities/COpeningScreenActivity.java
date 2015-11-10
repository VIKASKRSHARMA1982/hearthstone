package hertz.hertz.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import hertz.hertz.R;

public class COpeningScreenActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copening_screen);

        if(!checkInternetConnection()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You are not connected to the internet. Please check your connection.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    });/*
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dismiss();
                        }
                    });*/
            AlertDialog alert = builder.create();
            alert.show();
        }


    }
    private boolean checkInternetConnection() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() &&    conMgr.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            System.out.println("Internet Connection Not Present");
            return false;
        }
    }
    public void onClick(View view){

        switch(view.getId()){
            case R.id.bLogin:
                startActivity(new Intent(this, CLoginActivity.class));
                break;
            case R.id.bSignUp:
                startActivity(new Intent(this, CRegistrationActivity.class));
                break;
        }
    }

}
