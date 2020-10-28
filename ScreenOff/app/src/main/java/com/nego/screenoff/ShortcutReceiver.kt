package com.nego.screenoff

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat

class ShortcutReceiver : Activity() {
  public override fun onCreate(savedInstanceState: Bundle?) {
    if (intent.action == "android.intent.action.CREATE_SHORTCUT") {
      setResult(RESULT_OK, getAddIntent(this))
      finish()
    }
    if (intent.action == Constants.ACTION_SCREEN_OFF) {
      MyAccessibilityService.screenOff(this)
      finish()
    }
    super.onCreate(savedInstanceState)
  }

  companion object {
    fun getAddIntent(context: Context): Intent {
      val shortcutIntent = Intent(context, ShortcutReceiver::class.java)
      shortcutIntent.action = Constants.ACTION_SCREEN_OFF
      shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
      val shortcut = ShortcutInfoCompat.Builder(context, "screen_off").setIntent(shortcutIntent)
          .setShortLabel(context.resources.getString(R.string.app_name))
          .setIcon(IconCompat.createWithResource(context, R.mipmap.ic_launcher))
          .setLongLabel(context.resources.getString(R.string.app_name))
          .build()
      return ShortcutManagerCompat.createShortcutResultIntent(context, shortcut)
    }
  }
}