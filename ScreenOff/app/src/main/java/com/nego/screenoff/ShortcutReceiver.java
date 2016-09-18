package com.nego.screenoff;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import cyanogenmod.app.CMStatusBarManager;
import cyanogenmod.app.CustomTile;

public class ShortcutReceiver extends Activity {

    public void onCreate(Bundle savedInstanceState) {

        if (getIntent().getAction().equals("android.intent.action.CREATE_SHORTCUT")) {
            setResult(RESULT_OK, getAddIntent(this));
            finish();
        }

        if (getIntent().getAction().equals(Constants.ACTION_SCREEN_OFF)) {
            Utils.rootScreenOff(this);
            finish();
        }

        super.onCreate(savedInstanceState);
    }

    public static void addShortcutToHome(Context context) {
        context.sendBroadcast(getAddIntent(context));
    }

    public static Intent getAddIntent(Context context) {
        Intent shortcutIntent = new Intent(context, ShortcutReceiver.class);
        shortcutIntent.setAction(Constants.ACTION_SCREEN_OFF);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getResources().getString(R.string.app_name));
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, R.mipmap.ic_shortcut));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

        return addIntent;
    }
}
