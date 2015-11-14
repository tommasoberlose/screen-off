package com.nego.screenoff;

import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Main extends AppCompatActivity {

    private Button button;
    private SharedPreferences SP;
    private ImageView action_feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SP = getSharedPreferences(Costants.PREFERENCES_COSTANT, Context.MODE_PRIVATE);
        button = (Button) findViewById(R.id.button);

        // FEEDBACK
        action_feedback = (ImageView) findViewById(R.id.action_feedback);
        action_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/communities/100614116200820350356/stream/bda977a4-6f0f-4f72-bd8e-08d148a6fa7e")));
            }
        });

        // VERSION
        String version = getString(R.string.text_version);
        try {
            version += " " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((TextView) findViewById(R.id.version)).setText(version);

        updateUI(Utils.checkAdmin(this));

        Utils.welcome(this, SP);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI(Utils.checkAdmin(this));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void updateUI(boolean admin) {
        button.setText(admin ? getString(R.string.action_disable) : getString(R.string.action_enable));
        button.setSelected(admin);
        if (admin) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ComponentName devAdminReceiver = new ComponentName(Main.this, DeviceAdminReceiver.class);
                    DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
                    dpm.removeActiveAdmin(devAdminReceiver);
                    updateUI(false);
                }
            });
        } else {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ComponentName mAdminName = new ComponentName(Main.this, DeviceAdminReceiver.class);
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                            mAdminName);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                            getString(R.string.text_request_dev));
                    startActivityForResult(intent, 5);
                }
            });
        }
    }
}
