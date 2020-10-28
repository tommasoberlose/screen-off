package com.nego.screenoff

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ExportedBroadcastReceiver : BroadcastReceiver() {
  /**
   * To use this function send a Broadcast with
   * this custom action:
   * com.nego.screenoff.action.SCREEN_OFF
   * and before, of course, check if this application is installed
   *
   * So
   * sendBroadcast(new Intent("com.nego.screenoff.action.SCREEN_OFF"));
   */
  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action == Constants.ACTION_SCREEN_OFF) {
      MyAccessibilityService.screenOff(context)
    }
  }
}