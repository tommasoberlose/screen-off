package com.nego.screenoff;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;


public class Utils {

    public static boolean checkAdmin(Context context) {
        DevicePolicyManager mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName mAdminName = new ComponentName(context, DeviceAdminReceiver.class);

        return mDPM.isAdminActive(mAdminName);
    }

    public static void screenOff(Context context) {
        final DevicePolicyManager policyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminReceiver = new ComponentName(context, DeviceAdminReceiver.class);
        final boolean admin = policyManager.isAdminActive(adminReceiver);
        if (admin) {
            policyManager.lockNow();
        } else {
            context.startActivity(new Intent(context, Main.class));
        }
    }

    public static void welcome(Context context, SharedPreferences SP) {
        if (SP.getBoolean(Costants.FIRST_VIEW, true)) {
            SP.edit().putBoolean(Costants.FIRST_VIEW, false).apply();
            new AlertDialog.Builder(context)
                    .setTitle(R.string.text_welcome)
                    .setMessage(R.string.text_welcome_msg)
                    .setPositiveButton(R.string.text_great, null)
                    .show();
        }
    }

}
