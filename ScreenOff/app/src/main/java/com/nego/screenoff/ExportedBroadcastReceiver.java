package com.nego.screenoff;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ExportedBroadcastReceiver extends BroadcastReceiver {

    /**
     * To use this function send a Broadcast with
     * this custom action:
     * com.nego.screenoff.action.SCREEN_OFF
     * and before, of course, check if this application is installed
     *
     * So
     * sendBroadcast(new Intent("com.nego.screenoff.action.SCREEN_OFF"));
     */

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Constants.ACTION_SCREEN_OFF)) {
            Utils.rootScreenOff(context);
        }
    }
}
