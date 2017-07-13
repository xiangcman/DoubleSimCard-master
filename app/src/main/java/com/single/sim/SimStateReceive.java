package com.single.sim;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import de.greenrobot.event.EventBus;

public class SimStateReceive extends BroadcastReceiver {
    private static final String TAG = SimStateReceive.class.getSimpleName();
    public final static String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_SIM_STATE_CHANGED)) {
            Log.d(TAG, "onReceive");
            EventBus.getDefault().post(new SimStateChange());
        }
    }

}
