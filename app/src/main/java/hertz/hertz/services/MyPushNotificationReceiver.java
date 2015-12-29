package hertz.hertz.services;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.parse.ParsePushBroadcastReceiver;

/**
 * Created by rsbulanon on 11/25/15.
 */
public class MyPushNotificationReceiver extends ParsePushBroadcastReceiver {

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);
        intent.setAction("broadcast_action");
        LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(context);
        mgr.sendBroadcast(intent);
    }
}
