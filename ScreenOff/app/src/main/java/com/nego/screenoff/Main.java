package com.nego.screenoff;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nego.screenoff.util.RootUtil;

public class Main extends AppCompatActivity {

    private CardView button;
    private SharedPreferences SP;
    private ImageView action_menu;
    private CardView help_card;
    private LinearLayout action_add_shortcut;
    private ImageView notification_icon;
    private LinearLayout action_show_notification;
    private TextView subtitle_show_notification;
    private LinearLayout action_root_function;
    private ImageView root_function_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SP = getSharedPreferences(Constants.PREFERENCES_COSTANT, Context.MODE_PRIVATE);
        button = (CardView) findViewById(R.id.button);

        action_menu = (ImageView) findViewById(R.id.action_menu);
        help_card = (CardView) findViewById(R.id.help_card);
        action_add_shortcut = (LinearLayout) findViewById(R.id.action_add_shortcut);
        notification_icon = (ImageView) findViewById(R.id.notification_icon);
        action_show_notification = (LinearLayout) findViewById(R.id.toggle_notification);
        subtitle_show_notification = (TextView) findViewById(R.id.subtitle_show_notification);
        action_root_function = (LinearLayout) findViewById(R.id.use_root_function);
        root_function_icon = (ImageView) findViewById(R.id.root_function_icon);

        // DONATION
        action_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bDialog = new BottomSheetDialog(Main.this);
                View bView = LayoutInflater.from(Main.this).inflate(R.layout.bottom_sheet_menu, null);

                bView.findViewById(R.id.action_remove_p).setVisibility(Utils.checkAdmin(Main.this) ? View.VISIBLE : View.GONE);
                bView.findViewById(R.id.action_remove_p).setOnClickListener(new View.OnClickListener() {
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
                        bDialog.dismiss();
                    }
                });

                bView.findViewById(R.id.action_feedback).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/communities/100614116200820350356/stream/bda977a4-6f0f-4f72-bd8e-08d148a6fa7e")));
                        bDialog.dismiss();
                    }
                });

                bView.findViewById(R.id.action_donation).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Main.this, Donation.class));
                        bDialog.dismiss();
                    }
                });

                bView.findViewById(R.id.action_uninstall).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(Main.this)
                                .setTitle(getResources().getString(R.string.title_help_uninstall))
                                .setMessage(getResources().getString(R.string.message_help_uninstall))
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
                        bDialog.dismiss();
                    }
                });

                bDialog.setContentView(bView);
                bDialog.show();
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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                action_add_shortcut.setVisibility(View.GONE);
            } else {
                action_add_shortcut.setVisibility(View.VISIBLE);
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
            }
            notification_icon.setClickable(false);
            if (SP.getBoolean(Constants.PREFERENCE_SHOW_NOTIFICATION, false)) {
                notification_icon.setImageResource(R.drawable.ic_action_toggle_check_box);
                subtitle_show_notification.setText(R.string.subtitle_toggle_notification_on);
                action_show_notification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SP.edit().putBoolean(Constants.PREFERENCE_SHOW_NOTIFICATION, false).apply();
                        updateUI(admin);
                        Utils.showNotification(Main.this, false);
                    }
                });
            } else {
                notification_icon.setImageResource(R.drawable.ic_action_toggle_check_box_outline_blank);
                subtitle_show_notification.setText(R.string.subtitle_toggle_notification_off);
                action_show_notification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SP.edit().putBoolean(Constants.PREFERENCE_SHOW_NOTIFICATION, true).apply();
                        updateUI(admin);
                        Utils.showNotification(Main.this, true);
                    }
                });
            }

            action_root_function.setVisibility(RootUtil.isDeviceRooted() ? View.VISIBLE : View.GONE);
            if (SP.getBoolean(Constants.PREFERENCE_LOCK_INSTANTLY, true)) {
                root_function_icon.setImageResource(R.drawable.ic_action_toggle_check_box);
                action_root_function.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SP.edit().putBoolean(Constants.PREFERENCE_LOCK_INSTANTLY, false).apply();
                        updateUI(admin);
                    }
                });
            } else {
                root_function_icon.setImageResource(R.drawable.ic_action_toggle_check_box_outline_blank);
                action_root_function.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SP.edit().putBoolean(Constants.PREFERENCE_LOCK_INSTANTLY, true).apply();
                        updateUI(admin);
                    }
                });
            }
        } else {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View welcome_view = View.inflate(Main.this, R.layout.disclosure_layout, null);

                    new android.support.v7.app.AlertDialog.Builder(Main.this)
                            .setView(welcome_view)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ComponentName mAdminName = new ComponentName(Main.this, DeviceAdminReceiver.class);
                                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                                            mAdminName);
                                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                                            getString(R.string.text_request_dev));
                                    startActivityForResult(intent, 5);
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null)
                            .show();
                }
            });
        }
    }
}
