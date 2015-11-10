package hertz.hertz.activities;

import android.app.Application;

import com.firebase.client.Firebase;
import com.firebase.geofire.GeoFire;

import hertz.hertz.helpers.AppConstants;


/**
 * Created by rsbulanon on 11/11/15.
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        AppConstants.FIREBASE = new Firebase(AppConstants.BASE_URL);
        AppConstants.GEOFIRE = new GeoFire(AppConstants.FIREBASE);
    }
}
