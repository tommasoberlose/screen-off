package com.nego.screenoff;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;


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

    public static void showNotification(Context context, boolean show) {
        if (show) {

            Intent i = new Intent(context, ShortcutReceiver.class);
            i.setAction(Costants.ACTION_SCREEN_OFF);
            PendingIntent pi = PendingIntent.getActivity(context, -1, i, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder n = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_stat_power_off)
                    .setContentIntent(pi)
                    .setOngoing(true)
                    .setPriority(-1)
                    .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(context.getString(R.string.subtitle_notification));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                n.setPriority(Notification.PRIORITY_MIN);
            }

            notificationManager.notify(-1, n.build());
        } else {
            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        }
    }

}
