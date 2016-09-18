package com.nego.screenoff;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED") || intent.getAction().equals("android.intent.action.MY_PACKAGE_REPLACED")) {
            SharedPreferences SP = context.getSharedPreferences(Constants.PREFERENCES_COSTANT, Context.MODE_PRIVATE);
            Utils.showNotification(context, SP.getBoolean(Constants.PREFERENCE_SHOW_NOTIFICATION, false));
        }
    }
}