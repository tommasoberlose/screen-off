package com.nego.screenoff;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class ShortcutReceiver extends Activity {

    public void onCreate(Bundle savedInstanceState) {

        if (getIntent().getAction().equals("android.intent.action.CREATE_SHORTCUT")) {
            Intent shortcutIntent = new Intent(getApplicationContext(), ShortcutReceiver.class);
            shortcutIntent.setAction(Costants.ACTION_SCREEN_OFF);
            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            Intent addIntent = new Intent();
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getResources().getString(R.string.app_name));
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(this, R.mipmap.ic_launcher));
            addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

            setResult(RESULT_OK, addIntent);
            finish();
        }

        if (getIntent().getAction().equals(Costants.ACTION_SCREEN_OFF)) {
            Utils.screenOff(this);
            finish();
        }

        super.onCreate(savedInstanceState);
    }
}
