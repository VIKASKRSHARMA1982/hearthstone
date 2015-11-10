package hertz.hertz.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import hertz.hertz.R;
import hertz.hertz.helpers.AppConstants;

public class COpeningScreenActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copening_screen);

        if (!isNetworkAvailable()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(AppConstants.ERR_CONNECTION)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void onClick(View view) {
        switch(view.getId()){
            case R.id.bLogin:
                startActivity(new Intent(this, CLoginActivity.class));
                animateToLeft(this);
                break;
            case R.id.bSignUp:
                startActivity(new Intent(this, CRegistrationActivity.class));
                animateToLeft(this);
                break;
        }
    }
}
