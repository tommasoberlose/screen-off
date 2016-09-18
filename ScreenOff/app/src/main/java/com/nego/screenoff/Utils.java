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
import android.view.View;

import com.nego.screenoff.util.RootUtil;

import cyanogenmod.app.CMStatusBarManager;
import cyanogenmod.app.CustomTile;


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

    public static void rootScreenOff(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences SP = context.getSharedPreferences(Constants.PREFERENCES_COSTANT, Context.MODE_PRIVATE);
                if (SP.getBoolean(Constants.PREFERENCE_LOCK_INSTANTLY, true)) {
                    try {
                        if (Runtime.getRuntime().exec(new String[]{"su", "-c", "input keyevent 26"}).waitFor() != 0) {
                            Utils.screenOff(context);
                        }
                    } catch (Exception e) {
                        Utils.screenOff(context);
                    }
                } else {
                    Utils.screenOff(context);
                }
            }
        }).start();
    }

    public static void welcome(Context context, SharedPreferences SP) {
        if (SP.getBoolean(Constants.FIRST_VIEW, true)) {
            SP.edit().putBoolean(Constants.FIRST_VIEW, false).apply();
            View welcome_view = View.inflate(context, R.layout.welcome_dialog, null);

            boolean isDeviceRooted = RootUtil.isDeviceRooted();
            welcome_view.findViewById(R.id.root_tip).setVisibility(isDeviceRooted ? View.VISIBLE : View.GONE);

            new AlertDialog.Builder(context)
                    .setView(welcome_view)
                    .setPositiveButton(R.string.text_great, null)
                    .show();
        }
    }

    public static void showNotification(Context context, boolean show) {
        if (show) {

            Intent i = new Intent(context, ShortcutReceiver.class);
            i.setAction(Constants.ACTION_SCREEN_OFF);
            PendingIntent pi = PendingIntent.getActivity(context, -1, i, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder n = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_stat_screen_off)
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


    public static void publishCMCustomTile(Context context) {
        try {
            Intent intent = new Intent();
            intent.setAction(Constants.ACTION_SCREEN_OFF);

            //intent.putExtra(MainActivity.STATE, States.STATE_OFF);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent long_i = new Intent(context, Main.class);
            PendingIntent longPendingIntent = PendingIntent.getBroadcast(context, 1,
                    long_i, PendingIntent.FLAG_UPDATE_CURRENT);

            CustomTile customTile = new CustomTile.Builder(context)
                    .setOnClickIntent(pendingIntent)
                    .setContentDescription(context.getString(R.string.text_how_to_use))
                    .setLabel(context.getString(R.string.app_name))
                    .shouldCollapsePanel(true)
                    .setOnLongClickIntent(longPendingIntent)
                    .setIcon(R.drawable.ic_tile_screen_off)
                    .build();

            CMStatusBarManager.getInstance(context)
                    .publishTile(1, customTile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
