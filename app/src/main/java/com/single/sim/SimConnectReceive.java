package com.single.sim;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import de.greenrobot.event.EventBus;

public class SimConnectReceive extends BroadcastReceiver {
    private static final String TAG = SimConnectReceive.class.getSimpleName();
    public final static String ACTION_SIM_STATE_CHANGED = ConnectivityManager.CONNECTIVITY_ACTION;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_SIM_STATE_CHANGED)) {
            Log.d(TAG, "onReceive");
            EventBus.getDefault().post(new SimConnectChange());
        }
    }

}
