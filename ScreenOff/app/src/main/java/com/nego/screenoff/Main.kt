package com.nego.screenoff

import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_main.*

class Main : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // VERSION
    version.text = getString(R.string.text_version).format(packageManager.getPackageInfo(packageName, 0).versionName)

    // Shortcuts
    with(getSystemService(ShortcutManager::class.java) as ShortcutManager) {
      val shortcut = ShortcutInfo.Builder(this@Main, "screen-off")
          .setShortLabel(getString(R.string.app_name))
          .setLongLabel(getString(R.string.app_name))
          .setIcon(Icon.createWithResource(this@Main, R.mipmap.ic_launcher))
          .setIntent(Intent(this@Main, ShortcutReceiver::class.java).apply {
            action = Constants.ACTION_SCREEN_OFF
          })
          .build()

      this.dynamicShortcuts = listOf(shortcut)
    }
  }

  override fun onResume() {
    updateUI()
    super.onResume()
  }

  private fun updateUI() {
    button.setOnClickListener {
      val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
      startActivityForResult(intent, 0)
    }

    button_label.text = if (MyAccessibilityService.isAccessibilityServiceEnabled(this)) getString(R.string.service_enabled) else getString(R.string.action_enable)
  }
}