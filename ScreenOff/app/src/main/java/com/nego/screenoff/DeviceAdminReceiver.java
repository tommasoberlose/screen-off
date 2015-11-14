package com.nego.screenoff;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;


public class DeviceAdminReceiver extends android.app.admin.DeviceAdminReceiver {

    SharedPreferences SP;

    @Override
    public void onEnabled(Context context, Intent intent) {
        Log.i("DEVADMIN", "ENABLED");
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return context.getString(R.string.warning_remove_dev);
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        Log.i("DEVADMIN", "DISABLED");
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
    }
}
