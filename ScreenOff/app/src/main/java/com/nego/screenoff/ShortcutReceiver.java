package com.nego.screenoff;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.pm.ShortcutInfoCompat;
import android.support.v4.content.pm.ShortcutManagerCompat;
import android.support.v4.graphics.drawable.IconCompat;

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
        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, getAddIntent(context));
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getResources().getString(R.string.app_name));
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, R.mipmap.ic_launcher));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        context.sendBroadcast(addIntent);
    }

    public static Intent getAddIntent(Context context) {
        Intent shortcutIntent = new Intent(context, ShortcutReceiver.class);
        shortcutIntent.setAction(Constants.ACTION_SCREEN_OFF);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ShortcutInfoCompat shortcut = new ShortcutInfoCompat.Builder(context, "screen_off").setIntent(shortcutIntent)
                .setShortLabel(context.getResources().getString(R.string.app_name))
                .setIcon(IconCompat.createWithResource(context, R.mipmap.ic_launcher))
                .setLongLabel(context.getResources().getString(R.string.app_name))
                .build();

        return ShortcutManagerCompat.createShortcutResultIntent(context, shortcut);
    }
}
