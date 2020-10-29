package com.nego.screenoff

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager


class MyAccessibilityService : AccessibilityService() {
  override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    event?.let {
      if (event.eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED && event.text?.get(0) == COMMAND_SCREEN_OFF) {
        performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
      }
    }
  }

  override fun onServiceConnected() {
    this.serviceInfo = serviceInfo.apply {
      eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED
      flags = AccessibilityServiceInfo.DEFAULT
      notificationTimeout = 100
    }
  }

  override fun onInterrupt() {
  }

  companion object {
    const val COMMAND_SCREEN_OFF = "com.nego.screenoff.command.SCREEN_OFF"

    fun screenOff(context: Context) {
      if (isAccessibilityServiceEnabled(context)) {
        with(context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager) {
          if (isEnabled) {
            val event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED)
            event.packageName = context.packageName
            event.className = this.javaClass.name
            event.isEnabled = true
            event.text.add(COMMAND_SCREEN_OFF)
            sendAccessibilityEvent(event)
          }
        }
      } else {
        context.startActivity(Intent(context, Main::class.java).apply {
          flags += Intent.FLAG_ACTIVITY_NEW_TASK
        })
      }
    }

    fun isAccessibilityServiceEnabled(context: Context): Boolean {
      with(context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager) {
        return getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK).map { it.resolveInfo.serviceInfo }.any {
          it.packageName == context.packageName && it.name == MyAccessibilityService::class.java.name
        }
      }
    }
  }

}