package com.nego.screenoff;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Main extends AppCompatActivity {

    private CardView button;
    private SharedPreferences SP;
    private ImageView action_feedback;
    private ImageView action_remove_p;
    private CardView help_card;
    private LinearLayout action_add_shortcut;
    private ImageView notification_icon;
    private LinearLayout action_show_notification;
    private TextView subtitle_show_notification;
    private LinearLayout action_open_store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SP = getSharedPreferences(Costants.PREFERENCES_COSTANT, Context.MODE_PRIVATE);
        button = (CardView) findViewById(R.id.button);

        action_feedback = (ImageView) findViewById(R.id.action_feedback);
        action_remove_p = (ImageView) findViewById(R.id.action_remove_p);
        help_card = (CardView) findViewById(R.id.help_card);
        action_add_shortcut = (LinearLayout) findViewById(R.id.action_add_shortcut);
        notification_icon = (ImageView) findViewById(R.id.notification_icon);
        action_show_notification = (LinearLayout) findViewById(R.id.toggle_notification);
        subtitle_show_notification = (TextView) findViewById(R.id.subtitle_show_notification);
        action_open_store = (LinearLayout) findViewById(R.id.action_open_store);

        // FEEDBACK
        action_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/communities/100614116200820350356/stream/bda977a4-6f0f-4f72-bd8e-08d148a6fa7e")));
            }
        });

        // OPEN PLAY STORE
        action_open_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://")));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store")));
                }
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

    public void updateUI(final boolean admin) {

        button.setVisibility(admin ? View.GONE : View.VISIBLE);
        help_card.setVisibility(admin ? View.VISIBLE : View.GONE);

        if (admin) {
            action_remove_p.animate().scaleY(1).scaleX(1).alpha(1).setInterpolator(new AccelerateDecelerateInterpolator()).start();
            action_remove_p.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(Main.this)
                            .setTitle(getResources().getString(R.string.text_attention))
                            .setMessage(getResources().getString(R.string.ask_remove_privilege))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    ComponentName devAdminReceiver = new ComponentName(Main.this, DeviceAdminReceiver.class);
                                    DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
                                    dpm.removeActiveAdmin(devAdminReceiver);
                                    updateUI(false);
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null).show();

                }
            });
            action_add_shortcut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShortcutReceiver.addShortcutToHome(Main.this);
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                }
            });
            if (SP.getBoolean(Costants.PREFERENCE_SHOW_NOTIFICATION, false)) {
                notification_icon.setImageResource(R.drawable.ic_action_check_circle);
                subtitle_show_notification.setText(R.string.subtitle_toggle_notification_on);
                action_show_notification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SP.edit().putBoolean(Costants.PREFERENCE_SHOW_NOTIFICATION, false).apply();
                        updateUI(admin);
                        Utils.showNotification(Main.this, false);
                    }
                });
            } else {
                notification_icon.setImageResource(R.drawable.ic_action_circle);
                subtitle_show_notification.setText(R.string.subtitle_toggle_notification_off);
                action_show_notification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SP.edit().putBoolean(Costants.PREFERENCE_SHOW_NOTIFICATION, true).apply();
                        updateUI(admin);
                        Utils.showNotification(Main.this, true);
                    }
                });
            }
        } else {
            action_remove_p.animate().scaleY(0).scaleX(0).alpha(0).setInterpolator(new AccelerateDecelerateInterpolator()).start();
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
