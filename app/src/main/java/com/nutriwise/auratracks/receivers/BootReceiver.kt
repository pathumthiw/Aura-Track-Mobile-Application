package com.nutriwise.auratracks.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nutriwise.auratracks.data.SharedPreferencesHelper
import com.nutriwise.auratracks.services.HydrationReminderService

/**
 * Broadcast receiver to restart hydration reminders after device reboot
 */
class BootReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        try {
            if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
                val prefsHelper = SharedPreferencesHelper(context)
                val settings = prefsHelper.getHydrationSettings()
                
                // Restart reminders if they were enabled
                if (settings.isEnabled) {
                    val serviceIntent = Intent(context, HydrationReminderService::class.java)
                    context.startService(serviceIntent)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
